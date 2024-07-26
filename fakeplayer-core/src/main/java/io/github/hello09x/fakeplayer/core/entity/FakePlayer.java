package io.github.hello09x.fakeplayer.core.entity;

import io.github.hello09x.bedrock.command.MessageException;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.bedrock.task.CompletableTask;
import io.github.hello09x.bedrock.util.Teleportor;
import io.github.hello09x.bedrock.util.Worlds;
import io.github.hello09x.fakeplayer.api.event.FakePlayerSpawnEvent;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.api.spi.NMSNetwork;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.constant.FakePlayerStatus;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.manager.naming.SequenceName;
import io.github.hello09x.fakeplayer.core.util.InternalAddressGenerator;
import io.github.hello09x.fakeplayer.core.util.Skins;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class FakePlayer {

    private final static InternalAddressGenerator ipGen = new InternalAddressGenerator();

    private final static FakeplayerConfig config = Main.getInjector().getInstance(FakeplayerConfig.class);

    private final static I18n i18n = Main.getI18n();

    private final static NMSBridge bridge = Main.getInjector().getInstance(NMSBridge.class);

    @NotNull
    @Getter
    private final CommandSender creator;

    @NotNull
    @Getter
    private final NMSServerPlayer handle;

    @NotNull
    @Getter
    private final Player player;

    @NotNull
    @Getter
    private final String creatorIp;

    @NotNull
    @Getter
    private final SequenceName sequenceName;

    @NotNull
    private final FakeplayerTicker ticker;

    @NotNull
    @Getter
    private final String name;

    @NotNull
    private final UUID uuid;

    @UnknownNullability
    @Getter
    private NMSNetwork network;

    /**
     * @param creator      创建者
     * @param creatorIp    创建者 IP
     * @param sequenceName 序列名
     * @param lifespan     存活时间
     */
    public FakePlayer(
            @NotNull CommandSender creator,
            @NotNull String creatorIp,
            @NotNull SequenceName sequenceName,
            long lifespan
    ) {
        this.name = sequenceName.name();
        this.uuid = sequenceName.uuid();

        this.creator = creator;
        this.creatorIp = creatorIp;
        this.sequenceName = sequenceName;
        this.handle = bridge.fromServer(Bukkit.getServer()).newPlayer(uuid, name);
        this.player = handle.getPlayer();

        this.player.setMetadata(FakePlayerStatus.METADATA_KEY, new FixedMetadataValue(Main.getInstance(), FakePlayerStatus.PENDING));
        this.ticker = new FakeplayerTicker(this, lifespan);
        this.player.setPersistent(config.isPersistData());
        this.player.setSleepingIgnored(true);
        this.handle.setPlayBefore(); // 可避免一些插件的第一次入服欢迎信息
        this.handle.disableAdvancements(Main.getInstance()); // 不提示成就信息
    }

    /**
     * 让假人诞生
     */
    public CompletableFuture<Void> spawnAsync(@NotNull SpawnOption option) {
        var address = ipGen.next();
        this.player.setMetadata(FakePlayerStatus.METADATA_KEY, new FixedMetadataValue(Main.getInstance(), FakePlayerStatus.SPAWNING));
        return CompletableTask
                .joinAsync(Main.getInstance(), () -> {
                    var event = this.callPreLoginEvent(address);
                    if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                        throw new MessageException(i18n.translate(
                                "fakeplayer.command.spawn.error.disallowed", RED,
                                Placeholder.component("name", text(player.getName(), WHITE)),
                                Placeholder.component("reason", event.kickMessage())
                        ));
                    }
                })
                .thenComposeAsync(nul -> CompletableTask.join(Main.getInstance(), () -> {
                    {
                        var event = this.callLoginEvent(address);
                        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
                            throw new MessageException(i18n.translate(
                                    "fakeplayer.command.spawn.error.disallowed", RED,
                                    Placeholder.component("name", text(player.getName(), WHITE)),
                                    Placeholder.component("reason", event.kickMessage())
                            ));
                        }
                    }

                    {
                        var event = this.callSpawnEvent();
                        if (event.isCancelled()) {
                            throw new MessageException(i18n.translate(
                                    "fakeplayer.command.spawn.error.disallowed", RED,
                                    Placeholder.component("name", text(player.getName(), WHITE)),
                                    Placeholder.component("reason", event.getReason())
                            ));
                        }
                    }

                    if (config.isDropInventoryOnQuiting()) {
                        // 跨服背包同步插件可能导致假人既丢弃了一份到地上，在重新生成的时候又回来了
                        // 因此在生成的时候清空一次背包
                        // 但无法解决登陆后延迟同步背包的情况
                        this.player.getInventory().clear();
                    }

                    this.player.setInvulnerable(option.invulnerable());
                    this.player.setCollidable(option.collidable());
                    this.player.setCanPickupItems(option.pickupItems());
                    if (option.lookAtEntity()) {
                        Main.getInjector().getInstance(ActionManager.class).setAction(player, Action.ActionType.LOOK_AT_NEAREST_ENTITY, Action.ActionSetting.continuous());
                    }
                    if (option.skin() && this.creator instanceof Player playerCreator) {
                        Skins.copySkin(playerCreator, this.player);
                    }
                    if (option.replenish()) {
                        Main.getInjector().getInstance(FakeplayerManager.class).setReplenish(player, true);
                    }

                    this.network = bridge.createNetwork(address);
                    this.network.placeNewPlayer(Bukkit.getServer(), this.player);
                    this.setupName();
                    this.handle.setupClientOptions();   // 处理皮肤设置问题

                    this.teleportToSpawnpoint(option.spawnAt().clone());
                    this.ticker.runTaskTimer(Main.getInstance(), 0, 1);
                    this.player.setMetadata(FakePlayerStatus.METADATA_KEY, new FixedMetadataValue(Main.getInstance(), FakePlayerStatus.SPAWNED));
                }));
    }

    /**
     * 将假人传送到指定位置
     *
     * @param to 目标位置
     */
    private void teleportToSpawnpoint(@NotNull Location to) {
        var from = this.player.getLocation();
        if (from.getWorld().equals(to.getWorld())) {
            // 如果生成世界等于目的世界, 则需要穿越一次维度才能获取刷怪能力
            var otherWorld = Worlds.getOtherWorld(from.getWorld());
            if (otherWorld == null || !player.teleport(otherWorld.getSpawnLocation())) {
                this.creator.sendMessage(i18n.translate(
                        "fakeplayer.command.spawn.error.no-mob-spawning-ability", GRAY,
                        Placeholder.component("name", text(player.getName(), WHITE))
                ));
            }
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            if (!Teleportor.teleportAndSound(player, to)) {
                this.creator.sendMessage(i18n.translate(
                        "fakeplayer.command.spawn.error.teleport-failed", GRAY,
                        Placeholder.component("name", text(player.getName(), WHITE))
                ));
            }
        });
    }

    public boolean isOnline() {
        return this.player.isOnline();
    }

    public int getTickCount() {
        return handle.getTickCount();
    }

    private @NotNull AsyncPlayerPreLoginEvent callPreLoginEvent(@NotNull InetAddress address) {
        var event = new AsyncPlayerPreLoginEvent(
                this.name,
                address,
                this.uuid
        );
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    private @NotNull PlayerLoginEvent callLoginEvent(@NotNull InetAddress address) {
        var event = new PlayerLoginEvent(
                player,
                address.getHostAddress(),
                address
        );
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    private @NotNull FakePlayerSpawnEvent callSpawnEvent() {
        var event = new FakePlayerSpawnEvent(this.creator, this.player);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * 判断是否是创建者
     * <p>如果玩家下线再重新登陆, entityID 将会不一样导致 {@link Object#equals(Object)} 返回 {@code false}</p>
     *
     * @param sender 命令执行者
     * @return 是否是创建者
     */
    public boolean isCreator(@NotNull CommandSender sender) {
        if (this.creator instanceof Player pc && sender instanceof Player ps) {
            return pc.getUniqueId().equals(ps.getUniqueId());
        }
        return creator.getClass() == sender.getClass() && creator.getName().equals(sender.getName());
    }

    public @NotNull UUID getUUID() {
        return uuid;
    }

    private void setupName() {
        var displayName = text(player.getName(), GRAY, ITALIC);
        player.playerListName(displayName);
        player.displayName(displayName);
        player.customName(displayName);
    }
}
