package io.github.hello09x.fakeplayer.core.entity;

import io.github.hello09x.bedrock.command.MessageException;
import io.github.hello09x.bedrock.task.Tasks;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.manager.naming.SequenceName;
import io.github.hello09x.fakeplayer.core.util.InternalAddressGenerator;
import io.github.hello09x.fakeplayer.core.util.Teleportor;
import io.github.hello09x.fakeplayer.core.util.Worlds;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class FakePlayer {

    private final static Logger log = Main.getInstance().getLogger();
    private final static InternalAddressGenerator ipGen = new InternalAddressGenerator();

    private final FakeplayerConfig config = FakeplayerConfig.instance;

    @NotNull
    @Getter
    private CommandSender creator;

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

    public FakePlayer(
            @NotNull CommandSender creator,
            @NotNull String creatorIp,
            @NotNull SequenceName sequenceName,
            @Nullable LocalDateTime removeAt
    ) {
        this.name = sequenceName.name();
        this.uuid = sequenceName.uuid();

        this.creator = creator;
        this.creatorIp = creatorIp;
        this.sequenceName = sequenceName;
        this.handle = Main.getVersionSupport().server(Bukkit.getServer()).newPlayer(uuid, name);
        this.player = handle.getPlayer();
        this.ticker = new FakeplayerTicker(this, removeAt);

        player.setPersistent(false);
        player.setSleepingIgnored(true);
        handle.setPlayBefore();
        handle.unpersistAdvancements(Main.getInstance()); // 可避免一些插件的第一次入服欢迎信息
    }

    /**
     * 让假人诞生
     */
    public CompletableFuture<Void> spawnAsync(@NotNull SpawnOption option) {
        return CompletableFuture.runAsync(() -> {
            var address = ipGen.next();
            try {
                Tasks.joinAsync(() -> {
                    var event = this.preLogin(InetAddress.getLoopbackAddress());
                    if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                        throw new MessageException(textOfChildren(
                                text("创建假人失败, 无法登陆(preLogin): ", RED), event.kickMessage()
                        ));
                    }
                }, Main.getInstance());
            } catch (Throwable e) {
                throw MessageException.tryCast(e);
            }

            try {
                Tasks.join(() -> {
                    var login = this.login(InetAddress.getLoopbackAddress());
                    if (login.getResult() != PlayerLoginEvent.Result.ALLOWED) {
                        throw new MessageException(textOfChildren(
                                text("创建假人失败, 无法登陆(login): ", RED), login.kickMessage()
                        ));
                    }

                    if (config.isDropInventoryOnQuiting()) {
                        // 跨服背包同步插件可能导致假人既丢弃了一份到地上，在重新生成的时候又回来了
                        // 因此在生成的时候清空一次背包
                        // 但无法解决登陆后延迟同步背包的情况
                        player.getInventory().clear();
                    }

                    player.setInvulnerable(option.invulnerable());
                    player.setCollidable(option.collidable());
                    player.setCanPickupItems(option.pickupItems());
                    if (option.lookAtEntity()) {
                        ActionManager.instance.setAction(player, ActionType.LOOK_AT_NEAREST_ENTITY, ActionSetting.continuous());
                    }
                    if (option.skin() && this.creator instanceof Player playerCreator) {
                        handle.copyTexture(playerCreator);
                    }

                    var network = Main.getVersionSupport().network();
                    network.bindEmptyServerGamePacketListener(Bukkit.getServer(), this.player, address);
                    network.bindEmptyLoginPacketListener(Bukkit.getServer(), this.player, address);
                    handle.configClientOptions();   // 处理皮肤设置问题

                    var spawnAt = option.spawnAt().clone();
                    if (Worlds.isOverworld(spawnAt.getWorld())) {
                        // 创建在主世界时需要跨越一次世界才能拥有刷怪能力
                        teleportToSpawnpointAfterChangingDimension(spawnAt);
                    } else {
                        teleportToSpawnpoint(spawnAt);
                    }

                    ticker.runTaskTimer(Main.getInstance(), 0, 1);
                }, Main.getInstance());
            } catch (Throwable e) {
                throw MessageException.tryCast(e);
            }
        });
    }

    /**
     * 让假人完成一次维度旅行, 只有跨越过维度的假人才有刷怪能力(bug)
     *
     * @param spawnpoint 最终目的地, 即出生点
     */
    private void teleportToSpawnpointAfterChangingDimension(@NotNull Location spawnpoint) {
        var world = Worlds.getNonOverworld();
        if (world == null || !player.teleport(world.getSpawnLocation())) {
            this.creator.sendMessage(
                    textOfChildren(
                            text(player.getName(), WHITE),
                            text(" 无法获取刷怪能力, 需要你帮他完成一次世界间传送, 你可以使用 tphere 命令传送到地狱再传送回来", GRAY)
                    )
            );
            return;
        }

        Tasks.run(() -> teleportToSpawnpoint(spawnpoint), Main.getInstance());
    }

    private void teleportToSpawnpoint(@NotNull Location spawnpoint) {
        if (!Teleportor.teleportAndSound(player, spawnpoint)) {
            this.creator.sendMessage(textOfChildren(
                    text(player.getName(), WHITE),
                    text(" 传送到你身边失败: 可能由于第三方插件影响", GRAY)
            ));
        }
    }

    public @NotNull UUID getUUID() {
        return this.uuid;
    }

    public boolean isOnline() {
        return this.player.isOnline();
    }

    public int getTickCount() {
        return handle.getTickCount();
    }

    private @NotNull AsyncPlayerPreLoginEvent preLogin(@NotNull InetAddress address) {
        var event = new AsyncPlayerPreLoginEvent(
                this.name,
                address,
                address,
                this.uuid,
                player.getPlayerProfile(),
                address.getHostName()
        );
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    private @NotNull PlayerLoginEvent login(@NotNull InetAddress address) {
        var event = new PlayerLoginEvent(
                player,
                address.getHostAddress(),
                address
        );
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

}
