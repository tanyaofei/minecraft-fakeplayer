package io.github.hello09x.fakeplayer.entity;

import io.github.hello09x.bedrock.task.Tasks;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.api.nms.NMSFactory;
import io.github.hello09x.fakeplayer.api.nms.NMSServerPlayer;
import io.github.hello09x.fakeplayer.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.manager.naming.SequenceName;
import io.github.hello09x.fakeplayer.util.Teleportor;
import io.github.hello09x.fakeplayer.util.Worlds;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class FakePlayer {

    private final static Logger log = Main.getInstance().getLogger();
    private final static InetAddress fakeAddress = InetAddress.getLoopbackAddress();

    private final FakeplayerConfig config = FakeplayerConfig.instance;

    @NotNull
    @Getter
    private final String creator;

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
            @NotNull String creator,
            @NotNull String creatorIp,
            @NotNull SequenceName sequenceName,
            @Nullable LocalDateTime removeAt
    ) {
        this.name = sequenceName.name();
        this.uuid = sequenceName.uuid();

        this.creator = creator;
        this.creatorIp = creatorIp;
        this.sequenceName = sequenceName;
        this.handle = NMSFactory.getInstance().server(Bukkit.getServer()).newPlayer(uuid, name);
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
    public void spawn(@NotNull SpawnOption option) {
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
        if (option.skin()) {
            Optional.ofNullable(Bukkit.getPlayerExact(this.creator))
                    .ifPresent(handle::copyTexture);
        }

        if (config.isSimulateLogin()) {
            Tasks.runAsync(() -> {
                Bukkit.getPluginManager().callEvent(new AsyncPlayerPreLoginEvent(
                        this.name,
                        fakeAddress,
                        fakeAddress,
                        this.uuid,
                        player.getPlayerProfile(),
                        fakeAddress.getHostName()
                ));
            }, Main.getInstance());

            Bukkit.getPluginManager().callEvent(new PlayerLoginEvent(
                    player,
                    fakeAddress.getHostName(),
                    fakeAddress
            ));
        }

        var network = NMSFactory.getInstance().network();
        network.bindEmptyServerGamePacketListener(Bukkit.getServer(), this.player);
        network.bindEmptyLoginPacketListener(Bukkit.getServer(), this.player);

        var spawnAt = option.spawnAt().clone();
        if (Worlds.isOverworld(spawnAt.getWorld())) {
            // 创建在主世界时需要跨越一次世界才能拥有刷怪能力
            teleportToSpawnpointAfterChangingDimension(spawnAt);
        } else {
            teleportToSpawnpoint(spawnAt);
        }

        ticker.runTaskTimer(Main.getInstance(), 0, 1);
    }

    /**
     * 让假人完成一次维度旅行, 只有跨越过维度的假人才有刷怪能力(bug)
     *
     * @param spawnpoint 最终目的地, 即出生点
     */
    private void teleportToSpawnpointAfterChangingDimension(@NotNull Location spawnpoint) {
        var world = Worlds.getNonOverworld();
        if (world == null || !player.teleport(world.getSpawnLocation())) {
            Optional.ofNullable(Bukkit.getPlayerExact(creator))
                    .ifPresentOrElse(
                            c -> c.sendMessage(textOfChildren(
                                    text(player.getName(), WHITE),
                                    text(" 需要你手动帮他跨越过一次世界才具有刷怪能力", GRAY)
                            )),
                            () -> log.warning(String.format("假人 %s 需要手动跨越一次世界才具有刷怪能力", player.getName()))
                    );
            return;
        }

        Tasks.run(() -> teleportToSpawnpoint(spawnpoint), Main.getInstance());
    }

    private void teleportToSpawnpoint(@NotNull Location spawnpoint) {
        if (!Teleportor.teleportAndSound(player, spawnpoint)) {
            Optional.ofNullable(Bukkit.getPlayerExact(creator))
                    .ifPresentOrElse(
                            p -> p.sendMessage(textOfChildren(
                                    text(player.getName(), WHITE),
                                    text(" 传送到你身边失败: 可能由于第三方插件影响", GRAY, ITALIC)
                            )),
                            () -> log.warning(String.format("假人 %s 可能由于第三方插件而传送到创建者身边失败", player.getName())));
        }
    }

    public @NotNull UUID getUUID() {
        return this.uuid;
    }

    public boolean isOnline() {
        return this.player.isOnline();
    }

    public @Nullable Player getCreatorPlayer() {
        return Bukkit.getPlayerExact(this.creator);
    }

    public int getTickCount() {
        return handle.getTickCount();
    }

}
