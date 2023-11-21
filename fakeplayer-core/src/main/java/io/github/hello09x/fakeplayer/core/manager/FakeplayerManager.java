package io.github.hello09x.fakeplayer.core.manager;

import io.github.hello09x.bedrock.command.MessageException;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.entity.FakePlayer;
import io.github.hello09x.fakeplayer.core.entity.SpawnOption;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.manager.invsee.Invsee;
import io.github.hello09x.fakeplayer.core.manager.naming.NameManager;
import io.github.hello09x.fakeplayer.core.manager.naming.SequenceName;
import io.github.hello09x.fakeplayer.core.manager.naming.exception.IllegalCustomNameException;
import io.github.hello09x.fakeplayer.core.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import io.github.hello09x.fakeplayer.core.util.AddressUtils;
import io.github.hello09x.fakeplayer.core.util.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class FakeplayerManager {

    public final static FakeplayerManager instance = new FakeplayerManager();

    private final static Logger log = Main.getInstance().getLogger();

    private final FakeplayerConfig config = FakeplayerConfig.instance;

    private final UsedIdRepository usedIdRepository = UsedIdRepository.instance;

    private final NameManager nameManager = NameManager.instance;

    private final FakeplayerList playerList = FakeplayerList.instance;

    private final UserConfigManager configManager = UserConfigManager.instance;

    private final I18n i18n = Main.getI18n();

    private final Invsee invsee = Invsee.getInstance();

    private FakeplayerManager() {
        var timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleWithFixedDelay(() -> {
                    if (Bukkit.getServer().getTPS()[1] < config.getKaleTps()) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            if (FakeplayerManager.this.removeAll("low tps") > 0) {
                                Bukkit.broadcast(i18n.translate("fakeplayer.manager.remove-all-on-low-tps", GRAY, ITALIC));
                            }
                        });
                    }
                }, 0, 60, TimeUnit.SECONDS
        );

        Main.getInstance().registerOnDisable(() -> this.removeAll("Plugin disabled"));
        Main.getInstance().registerOnDisable(timer::shutdown);
    }

    /**
     * 创建一个假人
     *
     * @param creator 创建者
     * @param spawnAt 生成地点
     */
    public @NotNull CompletableFuture<Player> spawnAsync(
            @NotNull CommandSender creator,
            @Nullable String name,
            @NotNull Location spawnAt,
            long lifespan
    ) throws MessageException {
        this.checkLimit(creator);

        SequenceName sn;
        try {
            sn = name == null ? nameManager.register(creator) : nameManager.custom(creator, name);
        } catch (IllegalCustomNameException e) {
            throw new MessageException(e.getMessage());
        }

        var fp = new FakePlayer(
                creator,
                AddressUtils.getAddress(creator),
                sn,
                lifespan
        );
        var target = fp.getPlayer();    // 即使出现移除也不需要处理这个玩家, Bukkit 自行清除

        return CompletableFuture
                .supplyAsync(() -> {
                    var configs = configManager.getConfigs(creator);
                    return new SpawnOption(
                            spawnAt,
                            configs.getOrDefault(Config.invulnerable),
                            configs.getOrDefault(Config.collidable),
                            configs.getOrDefault(Config.look_at_entity),
                            configs.getOrDefault(Config.pickup_items),
                            configs.getOrDefault(Config.skin),
                            configs.getOrDefault(Config.replenish)
                    );
                })
                .thenComposeAsync(fp::spawnAsync)
                .thenApply(nul -> {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        this.playerList.add(fp);
                        this.usedIdRepository.add(target.getUniqueId());
                    });

                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        this.dispatchCommands(target, config.getPreparingCommands());
                        this.issueCommands(target, config.getSelfCommands());
                    }, 20);
                    return target;
                });
    }

    /**
     * 获取一个假人
     *
     * @param creator 创建者
     * @param name    假人名称
     * @return 假人
     */
    public @Nullable Player get(@NotNull CommandSender creator, @NotNull String name) {
        return Optional
                .ofNullable(this.playerList.getByName(name))
                .filter(p -> p.isCreator(creator))
                .map(FakePlayer::getPlayer)
                .orElse(null);
    }

    /**
     * 根据名称获取假人
     *
     * @param name 名称
     * @return 假人
     */
    public @Nullable Player get(@NotNull String name) {
        return Optional
                .ofNullable(this.playerList.getByName(name))
                .map(FakePlayer::getPlayer)
                .orElse(null);
    }

    /**
     * 获取一个假人的创建者, 如果这个玩家不是假人, 则为 {@code null}
     *
     * @param target 假人
     * @return 假人的创建者
     */
    public @Nullable String getCreatorName(@NotNull Player target) {
        return Optional
                .ofNullable(this.playerList.getByUUID(target.getUniqueId()))
                .map(FakePlayer::getCreator)
                .map(CommandSender::getName)
                .orElse(null);
    }

    /**
     * 获取假人的创建者
     *
     * @param target 假人
     * @return 创建者
     */
    public @Nullable CommandSender getCreator(@NotNull Player target) {
        return Optional.ofNullable(this.playerList.getByUUID(target.getUniqueId()))
                .map(FakePlayer::getCreator)
                .map(creator -> {
                    if (creator instanceof Player p) {
                        return Bukkit.getPlayer(p.getUniqueId());
                    } else {
                        return creator;
                    }
                })
                .orElse(null);
    }

    /**
     * 根据名称删除假人
     *
     * @param name   名称
     * @param reason 原因
     * @return 是否删除成功
     */
    public boolean remove(@NotNull String name, @Nullable String reason) {
        return this.remove(name, reason == null ? null : text(reason));
    }

    /**
     * 根据名称删除假人
     *
     * @param name   名称
     * @param reason 原因
     * @return 是否移除成功
     */
    public boolean remove(@NotNull String name, @Nullable Component reason) {
        var target = this.get(name);
        if (target == null) {
            return false;
        }

        target.kick(textOfChildren(
                text("[fakeplayer] "),
                reason == null ? text("removed") : reason
        ));
        return true;
    }

    /**
     * 移除所有假人
     *
     * @return 移除的假人数量
     */
    public int removeAll(@Nullable String reason) {
        var targets = getAll();
        for (var target : targets) {
            target.kick(text("[fakeplayer] " + (reason == null ? "removed" : reason)));
        }
        return targets.size();
    }

    /**
     * @return 获取所有假人
     */
    public @NotNull List<Player> getAll() {
        return this.getAll((Predicate<Player>) null);
    }

    /**
     * @param predicate 筛选条件
     * @return 经过筛选的假人
     */
    public @NotNull List<Player> getAll(@Nullable Predicate<Player> predicate) {
        var stream = this.playerList.getAll().stream().map(FakePlayer::getPlayer);
        if (predicate != null) {
            stream = stream.filter(predicate);
        }
        return stream.toList();
    }

    /**
     * 清理假人
     *
     * @param target 假人
     */
    public void cleanup(@NotNull Player target) {
        var fakeplayer = this.playerList.removeByUUID(target.getUniqueId());
        if (fakeplayer == null) {
            return;
        }
        this.nameManager.unregister(fakeplayer.getSequenceName());
        if (config.isDropInventoryOnQuiting()) {
            ActionManager.instance.setAction(fakeplayer.getPlayer(), Action.ActionType.DROP_INVENTORY, Action.ActionSetting.once());
        }
    }

    /**
     * 获取创建者创建的所有假人
     *
     * @param creator 创建者
     * @return 创建者创建的假人
     */
    public @NotNull List<Player> getAll(@NotNull CommandSender creator) {
        return this.getAll(creator, null);
    }

    /**
     * 获取筛选过的创建者创建的假人
     *
     * @param creator   创建者
     * @param predicate 筛选条件
     * @return 假人
     */
    public @NotNull List<Player> getAll(@NotNull CommandSender creator, @Nullable Predicate<Player> predicate) {
        var stream = this.playerList.getByCreator(creator.getName()).stream().map(FakePlayer::getPlayer);
        if (predicate != null) {
            stream = stream.filter(predicate);
        }
        return stream.toList();
    }

    /**
     * 判断一名玩家是否是假人
     *
     * @param target 玩家
     * @return 是否是假人
     */
    public boolean isFake(@NotNull Player target) {
        return this.playerList.getByUUID(target.getUniqueId()) != null;
    }

    /**
     * 判断一名玩家不是假人
     *
     * @param target 玩家
     * @return 是否不是假人
     */
    public boolean isNotFake(@NotNull Player target) {
        return this.playerList.getByUUID(target.getUniqueId()) == null;
    }

    /**
     * 获取 IP 地址创建着多少个假人
     *
     * @param address IP 地址
     * @return 该 IP 地址创建着多少个假人
     */
    public long countByAddress(@NotNull String address) {
        return this.playerList
                .stream()
                .filter(p -> p.getCreatorIp().equals(address))
                .count();
    }

    /**
     * 获取这个玩家创建了多少个假人
     *
     * @param creator 玩家
     * @return 创建了多少个假人
     */
    public int countByCreator(@NotNull CommandSender creator) {
        return this.playerList.countByCreator(creator.getName());
    }

    /**
     * 设置假人是否自动填装
     *
     * @param target    假人
     * @param replenish 是否自动补货
     */
    public void setReplenish(@NotNull Player target, boolean replenish) {
        if (!replenish) {
            target.removeMetadata("fakeplayer:replenish", Main.getInstance());
        } else {
            target.setMetadata("fakeplayer:replenish", new FixedMetadataValue(Main.getInstance(), true));
        }
    }

    /**
     * 判断假人是否自动补货
     *
     * @param target 假人
     * @return 是否自动补货
     */
    public boolean isReplenish(@NotNull Player target) {
        return target.hasMetadata("fakeplayer:replenish");
    }

    /**
     * 设置玩家当前选择的假人
     *
     * @param creator 玩家
     * @param target  假人
     */
    public void setSelection(@NotNull Player creator, @Nullable Player target) {
        if (target == null) {
            creator.removeMetadata("fakeplayer:selection", Main.getInstance());
            return;
        }

        if (!this.isFake(target)) {
            return;
        }

        creator.setMetadata("fakeplayer:selection", new FixedMetadataValue(Main.getInstance(), target.getUniqueId()));
    }

    /**
     * 获取当前选中的假人
     *
     * @param creator 创建者
     * @return 选中的假人
     */
    public @Nullable Player getSelection(@NotNull CommandSender creator) {
        if (!(creator instanceof Player p)) {
            return null;
        }
        if (!p.hasMetadata("fakeplayer:selection")) {
            return null;
        }

        var uuid = (UUID) p.getMetadata("fakeplayer:selection")
                .stream()
                .map(MetadataValue::value)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
        if (uuid == null) {
            return null;
        }

        var target = Optional.ofNullable(playerList.getByUUID(uuid)).map(FakePlayer::getPlayer).orElse(null);
        if (target == null) {
            this.setSelection(p, null);
        }
        return target;
    }

    /**
     * 以假人身份执行命令
     *
     * @param target   假人
     * @param commands 命令
     */
    public void issueCommands(@NotNull Player target, @NotNull List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }
        if (this.isNotFake(target)) {
            return;
        }

        for (var cmd : Commands.formatCommands(commands)) {
            if (!target.performCommand(cmd)) {
                log.warning(target.getName() + " failed to execute command: " + cmd);
            } else {
                log.info(target.getName() + " issued command: " + cmd);
            }
        }
    }

    /**
     * 以控制台身份对玩家执行命令
     *
     * @param player   假人
     * @param commands 命令
     */
    public void dispatchCommands(@NotNull Player player, @NotNull List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }

        if (this.isNotFake(player)) {
            return;
        }

        var server = Bukkit.getServer();
        var sender = Bukkit.getConsoleSender();
        for (var cmd : Commands.formatCommands(
                commands,
                "%p", player.getName(),
                "%u", player.getUniqueId().toString(),
                "%c", Objects.requireNonNull(this.getCreatorName(player)))
        ) {
            if (!server.dispatchCommand(sender, cmd)) {
                log.warning("Failed to execute command for %s: ".formatted(player.getName()) + cmd);
            } else {
                log.info("Dispatched command: " + cmd);
            }
        }
    }

    /**
     * 让玩家打开假人背包
     *
     * @param creator 玩家
     * @param target  假人
     * @return 是否打开成功
     */
    public boolean openInventory(@NotNull Player creator, @NotNull Player target) {
        var fp = this.playerList.getByName(target.getName());
        if (fp == null) {
            return false;
        }
        if (!creator.isOp() && !fp.isCreator(creator)) {
            return false;
        }

        this.invsee.openInventory(creator, target);
        creator.playSound(target.getLocation(), Sound.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5f, 1.0f);
        return true;
    }

    /**
     * 检测限制, 不满足条件则抛出异常
     *
     * @param creator 创建者
     * @throws MessageException 消息
     */
    private void checkLimit(@NotNull CommandSender creator) throws MessageException {
        if (creator.isOp()) {
            return;
        }

        if (this.playerList.count() >= this.config.getServerLimit()) {
            throw new MessageException(i18n.asString("fakeplayer.command.spawn.error.server-limit"));
        }

        if (this.playerList.getByCreator(creator.getName()).size() >= this.config.getPlayerLimit()) {
            throw new MessageException(i18n.asString("fakeplayer.command.spawn.error.player-limit"));
        }

        if (this.config.isDetectIp() && this.countByAddress(AddressUtils.getAddress(creator)) >= this.config.getPlayerLimit()) {
            throw new MessageException(i18n.asString("fakeplayer.command.spawn.error.ip-limit"));
        }
    }


}
