package io.github.hello09x.fakeplayer.core.manager;

import io.github.hello09x.bedrock.command.MessageException;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.bedrock.task.Tasks;
import io.github.hello09x.bedrock.util.Components;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.api.constant.ConstantPool;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.entity.FakePlayer;
import io.github.hello09x.fakeplayer.core.entity.SpawnOption;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.manager.naming.NameManager;
import io.github.hello09x.fakeplayer.core.manager.naming.SequenceName;
import io.github.hello09x.fakeplayer.core.manager.naming.exception.IllegalCustomNameException;
import io.github.hello09x.fakeplayer.core.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import io.github.hello09x.fakeplayer.core.softdepend.OpenInvDepend;
import io.github.hello09x.fakeplayer.core.util.AddressUtils;
import io.github.hello09x.fakeplayer.core.util.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

import java.time.LocalDateTime;
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

    private final static MiniMessage miniMessage = MiniMessage.miniMessage();

    private final FakeplayerConfig config = FakeplayerConfig.instance;

    private final UsedIdRepository usedIdRepository = UsedIdRepository.instance;

    private final NameManager nameManager = NameManager.instance;

    private final FakeplayerList playerList = FakeplayerList.instance;

    private final UserConfigManager configManager = UserConfigManager.instance;

    private final I18n i18n = Main.i18n();

    private final OpenInvDepend openInvDepend = OpenInvDepend.instance;

    private FakeplayerManager() {
        var timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(() -> {
                    if (Bukkit.getServer().getTPS()[1] < config.getKaleTps()) {
                        Tasks.run(() -> {
                            if (FakeplayerManager.this.removeAll("low tps") > 0) {
                                Bukkit.broadcast(i18n.translate("fakeplayer.manager.remove-all-on-low-tps", GRAY, ITALIC));
                            }
                        }, Main.getInstance());
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
            @NotNull String name,
            @NotNull Location spawnAt,
            @Nullable LocalDateTime removeAt
    ) throws MessageException {
        this.checkLimit(creator);

        SequenceName sn;
        try {
            sn = name.isBlank() ? nameManager.register(creator) : nameManager.custom(creator, name);
        } catch (IllegalCustomNameException e) {
            throw new MessageException(e.getText());
        }

        var fp = new FakePlayer(
                creator,
                AddressUtils.getAddress(creator),
                sn,
                removeAt
        );

        var target = fp.getPlayer();
        target.playerListName(text(target.getName(), GRAY, ITALIC));

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
                            configs.getOrDefault(Config.refillable)
                    );
                })
                .thenCompose(fp::spawnAsync)
                .thenRunAsync(() -> {
                    Tasks.run(() -> {
                        playerList.add(fp);
                        usedIdRepository.add(target.getUniqueId());
                    }, Main.getInstance());

                    Tasks.run(() -> {
                        FakeplayerManager.this.dispatchCommands(target, config.getPreparingCommands());
                        FakeplayerManager.this.performCommands(target, config.getSelfCommands());
                    }, Main.getInstance(), 20);
                }).thenApply(ignored -> target);
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
                .ofNullable(playerList.getByName(name))
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
                .ofNullable(playerList.getByName(name))
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
                .ofNullable(playerList.getByUUID(target.getUniqueId()))
                .map(FakePlayer::getCreator)
                .map(CommandSender::getName)
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
        var player = this.get(name);
        if (player == null) {
            return false;
        }

        player.kick(textOfChildren(
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
        var fakers = getAll();
        for (var f : fakers) {
            f.kick(text("[fakeplayer] " + (reason == null ? "removed" : reason)));
        }
        return fakers.size();
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
        var stream = playerList.getAll().stream().map(FakePlayer::getPlayer);
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
        var fakeplayer = playerList.removeByUUID(target.getUniqueId());
        if (fakeplayer == null) {
            return;
        }
        nameManager.unregister(fakeplayer.getSequenceName());
        if (config.isDropInventoryOnQuiting()) {
            ActionManager.instance.setAction(fakeplayer.getPlayer(), ActionType.DROP_INVENTORY, ActionSetting.once());
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
        var stream = playerList.getByCreator(creator.getName()).stream().map(FakePlayer::getPlayer);
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
        return playerList.getByUUID(target.getUniqueId()) != null;
    }

    /**
     * 获取 IP 地址创建着多少个假人
     *
     * @param address IP 地址
     * @return 该 IP 地址创建着多少个假人
     */
    public long countByAddress(@NotNull String address) {
        return playerList
                .stream()
                .filter(p -> p.getCreatorIp().equals(address))
                .count();
    }

    /**
     * 设置假人是否自动填装
     *
     * @param target     假人
     * @param refillable 是否自动装填
     */
    public void setRefillable(@NotNull Player target, boolean refillable) {
        if (!this.isFake(target)) {
            return;
        }

        if (!refillable) {
            target.removeMetadata("fakeplayer:refillable", Main.getInstance());
        } else {
            target.setMetadata("fakeplayer:refillable", new FixedMetadataValue(Main.getInstance(), true));
        }
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
     * 判断假人是否自动填装
     *
     * @param target 假人
     * @return 是否自动填装
     */
    public boolean isRefillable(@NotNull Player target) {
        return target.hasMetadata("fakeplayer:refillable");
    }

    /**
     * 以假人身份执行命令
     *
     * @param target   假人
     * @param commands 命令
     */
    public void performCommands(@NotNull Player target, @NotNull List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }
        if (!isFake(target)) {
            return;
        }

        for (var cmd : Commands.formatCommands(commands)) {
            if (!target.performCommand(cmd)) {
                log.warning(target.getName() + " failed to execute command: " + cmd);
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

        if (!isFake(player)) {
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
                log.info("dispatched command: " + cmd);
            }
        }
    }

    public boolean openInventory(@NotNull Player creator, @NotNull Player player) {
        var target = this.playerList.getByName(player.getName());
        if (target == null) {
            return false;
        }
        if (!creator.isOp() && !target.isCreator(creator)) {
            return false;
        }

        if (!openInvDepend.openInventory(creator, player)) {
            this.openInventoryDefault(creator, player);
        }
        creator.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.8f, 0.8f);
        return true;
    }

    /**
     * 判断当前创建者是否有假人
     *
     * @param creator 创建者
     * @return 是否创建了假人
     */
    public boolean hasSpawned(@NotNull CommandSender creator) {
        return !playerList.getByCreator(creator.getName()).isEmpty();
    }

    /**
     * 检测限制, 不满足条件则抛出一场
     *
     * @param creator 创建者
     * @throws MessageException 消息
     */
    private void checkLimit(@NotNull CommandSender creator) throws MessageException {
        if (creator.isOp()) {
            return;
        }

        if (this.playerList.count() >= config.getServerLimit()) {
            throw new MessageException(i18n.asString("fakeplayer.command.spawn.error.server-limit"));
        }

        if (this.playerList.getByCreator(creator.getName()).size() >= config.getPlayerLimit()) {
            throw new MessageException(i18n.asString("fakeplayer.command.spawn.error.player-limit"));
        }

        if (config.isDetectIp() && this.countByAddress(AddressUtils.getAddress(creator)) >= config.getPlayerLimit()) {
            throw new MessageException(i18n.asString("fakeplayer.command.spawn.error.ip-limit"));
        }
    }

    private void openInventoryDefault(@NotNull Player player, @NotNull Player target) {
        var view = player.openInventory(target.getInventory());
        if (view != null) {
            view.setTitle(ConstantPool.UNMODIFIABLE_INVENTORY_TITLE_PREFIX + Components.asString(miniMessage.deserialize(
                    i18n.asString("fakeplayer.manager.inventory.title"),
                    Placeholder.component("name", text(target.getName()))
            )));
        }
    }

}
