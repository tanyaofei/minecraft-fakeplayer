package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.command.exception.CommandException;
import io.github.hello09x.devtools.core.utils.Exceptions;
import io.github.hello09x.devtools.core.utils.MetadataUtils;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.constant.MetadataKeys;
import io.github.hello09x.fakeplayer.core.entity.Fakeplayer;
import io.github.hello09x.fakeplayer.core.entity.SpawnOption;
import io.github.hello09x.fakeplayer.core.manager.feature.FakeplayerFeatureManager;
import io.github.hello09x.fakeplayer.core.manager.naming.NameManager;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import io.github.hello09x.fakeplayer.core.util.AddressUtils;
import io.github.hello09x.fakeplayer.core.util.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

@Singleton
public class FakeplayerManager {

    public final static String REMOVAL_REASON_PREFIX = "[fakeplayer] ";

    private final static Logger log = Main.getInstance().getLogger();

    private final NameManager nameManager;
    private final FakeplayerList playerList;
    private final FakeplayerFeatureManager featureManager;
    private final NMSBridge nms;
    private final FakeplayerConfig config;
    private final ScheduledExecutorService lagMonitor;

    @Inject
    public FakeplayerManager(NameManager nameManager, FakeplayerList playerList, FakeplayerFeatureManager featureManager, NMSBridge nms, FakeplayerConfig config) {
        this.nameManager = nameManager;
        this.playerList = playerList;
        this.featureManager = featureManager;
        this.nms = nms;
        this.config = config;

        this.lagMonitor = Executors.newSingleThreadScheduledExecutor();
        this.lagMonitor.scheduleWithFixedDelay(() -> {
                                                   if (Bukkit.getServer().getTPS()[1] < config.getKaleTps()) {
                                                       Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                                           if (this.removeAll("low tps") > 0) {
                                                               Bukkit.broadcast(translatable("fakeplayer.manager.remove-all-on-low-tps", GRAY, ITALIC));
                                                           }
                                                       });
                                                   }
                                               }, 0, 60, TimeUnit.SECONDS
        );
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
    ) {
        this.checkLimit(creator);

        var sn = name == null ? nameManager.getRegularName(creator) : nameManager.getSpecifiedName(name);
        log.info("UUID of fake player %s is %s".formatted(sn.name(), sn.uuid()));

        var fp = new Fakeplayer(
                creator,
                AddressUtils.getAddress(creator),
                sn,
                lifespan
        );

        var target = fp.getPlayer();    // 即使出现异常也不需要处理这个玩家, 最终会被 GC 掉
        this.playerList.add(fp);

        this.dispatchCommandsEarly(fp, this.config.getPreSpawnCommands());
        return CompletableFuture
                .supplyAsync(() -> {
                    var configs = featureManager.getFeatures(creator);
                    return new SpawnOption(
                            spawnAt,
                            configs.get(Feature.invulnerable).asBoolean(),
                            configs.get(Feature.collidable).asBoolean(),
                            configs.get(Feature.look_at_entity).asBoolean(),
                            configs.get(Feature.pickup_items).asBoolean(),
                            configs.get(Feature.skin).asBoolean(),
                            configs.get(Feature.replenish).asBoolean(),
                            configs.get(Feature.autofish).asBoolean(),
                            configs.get(Feature.wolverine).asBoolean()
                    );
                })
                .thenComposeAsync(fp::spawnAsync)
                .thenApply(ignored -> target);
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
                .filter(p -> p.isCreatedBy(creator))
                .map(Fakeplayer::getPlayer)
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
                .map(Fakeplayer::getPlayer)
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
                .map(Fakeplayer::getCreator)
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
                       .map(Fakeplayer::getCreator)
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
            target.kick(text(REMOVAL_REASON_PREFIX + (reason == null ? "removed" : reason)));
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
        var stream = this.playerList.getAll().stream().map(Fakeplayer::getPlayer);
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
            this.nms.createAction(
                    fakeplayer.getPlayer(),
                    ActionType.DROP_INVENTORY,
                    ActionSetting.once()
            ).tick();
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
        var stream = this.playerList.getByCreator(creator.getName()).stream().map(Fakeplayer::getPlayer);
        if (predicate != null) {
            stream = stream.filter(predicate);
        }
        return stream.toList();
    }

    public int getSize() {
        return this.playerList.getSize();
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
     * 设置玩家当前选择的假人
     *
     * @param creator 玩家
     * @param target  假人
     */
    public void setSelection(@NotNull Player creator, @Nullable Player target) {
        if (target == null) {
            creator.removeMetadata(MetadataKeys.SELECTION, Main.getInstance());
            return;
        }

        if (!this.isFake(target)) {
            return;
        }

        creator.setMetadata(MetadataKeys.SELECTION, new FixedMetadataValue(Main.getInstance(), target.getUniqueId()));
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
        if (!p.hasMetadata(MetadataKeys.SELECTION)) {
            return null;
        }

        var uuid = MetadataUtils
                .find(Main.getInstance(), p, MetadataKeys.SELECTION, UUID.class)
                .map(MetadataValue::value)
                .map(UUID.class::cast)
                .orElse(null);

        if (uuid == null) {
            return null;
        }

        var target = Optional.ofNullable(this.playerList.getByUUID(uuid)).map(Fakeplayer::getPlayer).orElse(null);
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

        var p = target.getName();
        var u = target.getUniqueId().toString();
        var c = Objects.requireNonNull(this.getCreatorName(target));
        for (var cmd : Commands.formatCommands(commands, "%p", p, "%u", u, "%c", c)) {
            if (!target.performCommand(cmd)) {
                log.warning(target.getName() + " failed to execute command: " + cmd);
            } else {
                log.info(target.getName() + " issued command: " + cmd);
            }
        }
    }

    public void dispatchCommandsEarly(@NotNull Fakeplayer fp, @NotNull List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }

        var server = Bukkit.getServer();
        var sender = Bukkit.getConsoleSender();
        var p = fp.getName();
        var u = fp.getUUID().toString();
        var c = fp.getCreator().getName();
        for (var cmd : Commands.formatCommands(commands, "%p", p, "%u", u, "%c", c)) {
            if (!server.dispatchCommand(sender, cmd)) {
                log.warning("Failed to execute command for %s: ".formatted(p) + cmd);
            } else {
                log.info("Dispatched command: " + cmd);
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

        var server = Bukkit.getServer();
        var sender = Bukkit.getConsoleSender();

        var p = player.getName();
        var u = player.getUniqueId().toString();
        var c = Objects.requireNonNull(this.getCreatorName(player));
        for (var cmd : Commands.formatCommands(commands, "%p", p, "%u", u, "%c", c)) {
            if (!server.dispatchCommand(sender, cmd)) {
                log.warning("Failed to execute command for %s: ".formatted(p) + cmd);
            } else {
                log.info("Dispatched command: " + cmd);
            }
        }
    }

    /**
     * 检测限制, 不满足条件则抛出异常
     *
     * @param creator 创建者
     */
    private void checkLimit(@NotNull CommandSender creator) throws CommandException {
        if (creator.isOp()) {
            return;
        }

        if (this.playerList.getSize() >= this.config.getServerLimit()) {
            throw new CommandException(translatable("fakeplayer.command.spawn.error.server-limit"));
        }

        if (this.playerList.getByCreator(creator.getName()).size() >= this.config.getPlayerLimit()) {
            throw new CommandException(translatable("fakeplayer.command.spawn.error.player-limit"));
        }

        if (this.config.isDetectIp() && this.countByAddress(AddressUtils.getAddress(creator)) >= this.config.getPlayerLimit()) {
            throw new CommandException(translatable("fakeplayer.command.spawn.error.ip-limit"));
        }
    }

    public void onDisable() {
        Exceptions.suppress(Main.getInstance(), () -> this.removeAll("Plugin disabled"));
        Exceptions.suppress(Main.getInstance(), this.lagMonitor::shutdownNow);
    }

    /**
     * 获取玩家当前选中的假人（如果没有则取第一个自己创建的假人）
     */
    public Fakeplayer getByOwner(Player player) {
        Player selected = getSelection(player);
        if (selected != null) {
            return this.playerList.getByUUID(selected.getUniqueId());
        }
        // 没有选中则取第一个自己创建的
        List<Fakeplayer> list = this.playerList.getByCreator(player.getName());
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 获取玩家自己创建且带指定标签的所有假人
     */
    public List<Fakeplayer> getByTag(@NotNull CommandSender owner, @NotNull String tag) {
        return this.playerList.getByCreator(owner.getName())
                .stream()
                .filter(fp -> fp.getHandle().hasTag(tag))
                .toList();
    }
}
