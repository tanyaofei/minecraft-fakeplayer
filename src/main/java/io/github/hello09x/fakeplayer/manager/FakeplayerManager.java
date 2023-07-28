package io.github.hello09x.fakeplayer.manager;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.entity.FakePlayer;
import io.github.hello09x.fakeplayer.entity.Metadatas;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.repository.model.Configs;
import io.github.hello09x.fakeplayer.util.AddressUtils;
import io.github.hello09x.fakeplayer.util.Teleportor;
import io.github.hello09x.fakeplayer.util.Unwrapper;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class FakeplayerManager {

    public final static FakeplayerManager instance = new FakeplayerManager();

    private final static Logger log = Main.getInstance().getLogger();

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    private final UsedIdRepository usedIdRepository = UsedIdRepository.instance;

    private final NameManager nameManager = NameManager.instance;

    private final UserConfigRepository userConfigRepository = UserConfigRepository.instance;

    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    private FakeplayerManager() {
        timer.scheduleAtFixedRate(() -> {
                    if (Bukkit.getServer().getTPS()[1] < properties.getKaleTps()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (removeAll() > 0) {
                                    Bukkit.getServer().broadcast(text("[服务器过于卡顿, 已移除所有假人]").style(Style.style(RED, ITALIC)));
                                }
                            }
                        }.runTask(Main.getInstance());
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
    public synchronized @Nullable Player spawn(
            @NotNull CommandSender creator,
            @NotNull Location spawnAt
    ) {
        var playerLimit = properties.getPlayerLimit();
        if (!creator.isOp() && playerLimit != Integer.MAX_VALUE && getAll(creator).size() >= playerLimit) {
            creator.sendMessage(text("你创建的假人数量已达到上限...", RED));
            return null;
        }

        var serverLimit = properties.getServerLimit();
        if (!creator.isOp() && serverLimit != Integer.MAX_VALUE && getAll().size() >= serverLimit) {
            creator.sendMessage(text("服务器假人数量已达到上限...", RED));
            return null;
        }

        if (!creator.isOp() && properties.isDetectIp() && countByAddress(AddressUtils.getAddress(creator)) >= 1) {
            creator.sendMessage(text("你所在 IP 创建的假人数量已达到上限...", RED));
            return null;
        }

        var sn = nameManager.take(creator);
        boolean invulnerable = true, lookAtEntity = true, collidable = true, pickupItems = true;
        if (creator instanceof Player p) {
            var creatorId = p.getUniqueId();
            invulnerable = userConfigRepository.selectOrDefault(creatorId, Configs.invulnerable);
            lookAtEntity = userConfigRepository.selectOrDefault(creatorId, Configs.look_at_entity);
            collidable = userConfigRepository.selectOrDefault(creatorId, Configs.collidable);
            pickupItems = userConfigRepository.selectOrDefault(creatorId, Configs.pickup_items);
        }

        var player = new FakePlayer(
                creator.getName(),
                ((CraftServer) Bukkit.getServer()).getServer(),
                generateId(sn.name()),
                sn.name()
        );

        var bukkitPlayer = player.getBukkitPlayer();
        Metadatas.CREATOR.set(bukkitPlayer, creator.getName());
        Metadatas.CREATOR_IP.set(bukkitPlayer, AddressUtils.getAddress(creator));
        Metadatas.NAME_SOURCE.set(bukkitPlayer, sn.source());
        Metadatas.NAME_SEQUENCE.set(bukkitPlayer, sn.sequence());
        bukkitPlayer.playerListName(text(bukkitPlayer.getName() + " (假人)").style(Style.style(GRAY, ITALIC)));

        player.spawn(invulnerable, collidable, lookAtEntity, pickupItems);

        usedIdRepository.add(bukkitPlayer.getUniqueId());
        dispatchCommands(bukkitPlayer, properties.getPreparingCommands());
        performCommands(bukkitPlayer, properties.getSelfCommands());

        spawnAt = spawnAt.clone();
        Teleportor.teleportAndSound(bukkitPlayer, spawnAt); // 当前 tick 必须传到出生点否则无法触发区块刷新
        ensureSpawnpoint(bukkitPlayer, spawnAt);    // 防止别的插件比如 `multicore` 把他带离出生点

        return bukkitPlayer;
    }

    private void ensureSpawnpoint(@NotNull Player player, @NotNull Location spawnpoint) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (spawnpoint.getWorld().equals(spawnpoint.getWorld()) && spawnpoint.distance(player.getLocation()) < 16) {
                    return;
                }

                player.teleport(spawnpoint.getWorld().getSpawnLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(spawnpoint);
                    }
                }.runTaskLater(Main.getInstance(), 1);
            }
        }.runTaskLater(Main.getInstance(), 1);
    }

    public @Nullable Player get(@NotNull CommandSender creator, @NotNull String name) {
        return Optional
                .ofNullable(get(name))
                .filter(faker -> Objects.equals(this.getCreator(faker), creator.getName()))
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
                .ofNullable(Bukkit.getServer().getPlayerExact(name))
                .filter(this::isFake)
                .orElse(null);
    }

    /**
     * 移除指定创建者创建的假人
     *
     * @param creator 创建者
     * @return 移除假人的数量
     */
    public int removeAll(@NotNull CommandSender creator) {
        var fakers = getAll(creator);
        fakers.forEach(Player::kick);
        return fakers.size();
    }

    /**
     * 根据名称删除假人
     *
     * @param name 名称
     * @return 名称对应的玩家不在线或者不是假人
     */
    public boolean remove(@NotNull String name) {
        var fakePlayer = Optional.ofNullable(get(name)).filter(this::isFake).orElse(null);
        if (fakePlayer == null) {
            return false;
        }
        fakePlayer.kick();
        return true;
    }

    /**
     * 获取一个假人的创建者, 如果这个玩家不是假人, 则为 {@code null}
     *
     * @param fakePlayer 假人
     * @return 假人的创建者
     */
    public @Nullable String getCreator(@NotNull Player fakePlayer) {
        return Metadatas.CREATOR.getOptional(fakePlayer).map(MetadataValue::asString).orElse(null);
    }

    /**
     * 移除所有假人
     *
     * @return 移除的假人数量
     */
    public int removeAll() {
        var fakers = getAll();
        fakers.forEach(Player::kick);
        return fakers.size();
    }

    /**
     * @return 获取所有假人
     */
    public @NotNull List<Player> getAll() {
        return Bukkit
                .getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> Metadatas.CREATOR.getOptional(p).isPresent())
                .collect(Collectors.toList());
    }

    public void cleanup(@NotNull Player fakePlayer) {
        if (!isFake(fakePlayer)) {
            return;
        }

        nameManager.giveback(
                Metadatas.NAME_SOURCE.get(fakePlayer).asString(),
                Metadatas.NAME_SEQUENCE.get(fakePlayer).asInt()
        );
        Arrays.stream(Metadatas.values()).forEach(meta -> meta.remove(fakePlayer));
        if (properties.isDropInventoryOnQuiting()) {
            Action.dropInventory(Unwrapper.getServerPlayer(fakePlayer));
        }
    }

    /**
     * 获取创建者创建的所有假人
     *
     * @param creator 创建者
     * @return 创建者创建的假人
     */
    public @NotNull List<Player> getAll(@NotNull CommandSender creator) {
        var name = creator.getName();
        return Bukkit
                .getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> Metadatas.CREATOR.getOptional(p).filter(c -> c.asString().equals(name)).isPresent())
                .collect(Collectors.toList());
    }

    /**
     * 判断一名玩家是否是假人
     *
     * @param player 玩家
     * @return 是否是假人
     */
    public boolean isFake(@NotNull Player player) {
        return Metadatas.CREATOR.getOptional(player).isPresent();
    }

    /**
     * 获取 IP 地址创建着多少个假人
     *
     * @param address IP 地址
     * @return 该 IP 地址创建着多少个假人
     */
    public long countByAddress(@NotNull String address) {
        return Bukkit.getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> Metadatas.CREATOR_IP.getOptional(p).map(MetadataValue::asString).filter(v -> v.equals(address)).isPresent())
                .count();
    }

    /**
     * 生成一个 UUID
     *
     * @return UUID
     */
    private @NotNull UUID generateId(@NotNull String name) {
        var uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        if (Bukkit.getServer().getOfflinePlayer(uuid).hasPlayedBefore()) {
            uuid = UUID.randomUUID();
            log.warning("Could not generate a UUID bound with name which is never played at this server, using random UUID as fallback: " + uuid);
        }
        return uuid;
    }

    public void performCommands(@NotNull Player player, @NotNull List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }
        if (!isFake(player)) {
            return;
        }

        for (var cmd : commands) {
            cmd = cmd.trim();
            if (cmd.startsWith("/")) {
                cmd = cmd.substring(1);
            }
            if (cmd.isBlank()) {
                continue;
            }

            player.performCommand(cmd);
        }
    }

    public void dispatchCommands(@NotNull Player player, @NotNull List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }

        if (!isFake(player)) {
            return;
        }

        var server = Bukkit.getServer();
        var sender = Bukkit.getConsoleSender();
        for (var cmd : commands) {
            cmd = cmd.trim();
            if (cmd.startsWith("/")) {
                cmd = cmd.substring(1);
            }
            if (cmd.length() > 1) {
                cmd = cmd
                        .replace("%p", player.getName())
                        .replace("%u", player.getUniqueId().toString())
                        .replace("%c", Objects.requireNonNull(getCreator(player), "Missing creator id at fake player metadata"));
            }

            if (cmd.isBlank()) {
                continue;
            }

            server.dispatchCommand(sender, cmd);
        }
    }

    public void onDisable() {
        this.timer.shutdown();
    }

}
