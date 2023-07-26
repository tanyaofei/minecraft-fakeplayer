package io.github.hello09x.fakeplayer.manager;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.entity.FakePlayer;
import io.github.hello09x.fakeplayer.entity.FakeplayerMetadata;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.optional.BungeeCordServer;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.repository.model.Configs;
import io.github.hello09x.fakeplayer.util.AddressUtils;
import io.github.hello09x.fakeplayer.util.MetadataUtils;
import io.github.hello09x.fakeplayer.util.UnwrapUtils;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
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

    private final BungeeCordServer bungee = BungeeCordServer.instance;

    private FakeplayerManager() {
        var timer = new Timer();

        // 服务器 tps 过低删除所有假人
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
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
            }
        }, 60_000, 60_000);

        // 移除下线的
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!properties.isFollowQuiting()) {
                    return;
                }

                var group = getAll()
                        .stream()
                        .collect(Collectors.groupingBy(FakeplayerManager.this::getCreator));

                for (var entry : group.entrySet()) {
                    if (bungee.isPlayerOnline(entry.getKey())) {
                        continue;
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (var fakePlayer : entry.getValue()) {
                                remove(fakePlayer.getName());
                            }
                            log.info(String.format("玩家 %s 已不在线, 已移除 %d 个假人", entry.getKey(), entry.getValue().size()));
                        }
                    }.runTask(Main.getInstance());
                }


            }
        }, 15_000, 15_000);

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

        var name = nameManager.take(creator);
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
                generateId(name.name()),
                name.name(),
                spawnAt
        );

        var bukkitPlayer = player.getBukkitPlayer();
        bukkitPlayer.setMetadata(FakeplayerMetadata.CREATOR.key, new FixedMetadataValue(Main.getInstance(), creator.getName()));
        bukkitPlayer.setMetadata(FakeplayerMetadata.CREATOR_IP.key, new FixedMetadataValue(Main.getInstance(), AddressUtils.getAddress(creator)));
        bukkitPlayer.setMetadata(FakeplayerMetadata.NAME_SOURCE.key, new FixedMetadataValue(Main.getInstance(), name.source()));
        bukkitPlayer.setMetadata(FakeplayerMetadata.NAME_SEQUENCE.key, new FixedMetadataValue(Main.getInstance(), name.sequence()));
        bukkitPlayer.playerListName(text(creator.getName() + "的假人").style(Style.style(GRAY, ITALIC)));

        player.spawn(invulnerable, collidable, lookAtEntity, pickupItems);

        usedIdRepository.add(bukkitPlayer.getUniqueId());

        dispatchCommands(bukkitPlayer, properties.getPreparingCommands());
        performCommands(bukkitPlayer);

        bukkitPlayer.teleport(spawnAt); // 当前 tick 必须传到出生点否则无法触发区块刷新
        spawnAt.getWorld().playSound(spawnAt, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);

        // 可能被别的插件干预
        // 在下一 tick 里探测
        new BukkitRunnable() {
            @Override
            public void run() {
                if (spawnAt.distance(bukkitPlayer.getLocation()) < 16) {
                    return;
                }

                bukkitPlayer.teleport(spawnAt.getWorld().getSpawnLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        bukkitPlayer.teleport(spawnAt);
                    }
                }.runTaskLater(Main.getInstance(), 1);
            }
        }.runTaskLater(Main.getInstance(), 1);

        return bukkitPlayer;
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
        return Optional.ofNullable(MetadataUtils.getFirst(fakePlayer, FakeplayerMetadata.CREATOR.key))
                .map(MetadataValue::asString)
                .orElse(null);
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
                .filter(p -> !p.getMetadata(FakeplayerMetadata.CREATOR.key).isEmpty())
                .collect(Collectors.toList());
    }

    public void openInventory(@NotNull Player player, @NotNull Player fakePlayer) {
        if (!isFake(fakePlayer)) {
            return;
        }

        player.showTitle(Title.title(
                text("取物品有概率消失", RED),
                text("尽量使用 Shift + 左键取物品"),
                Title.Times.times(Ticks.duration(10), Ticks.duration(20), Ticks.duration(20))
        ));

        var inv = fakePlayer.getInventory();
        player.openInventory(inv);
    }

    public void cleanup(@NotNull Player fakePlayer) {
        if (!isFake(fakePlayer)) {
            return;
        }

        var metas = MetadataUtils.get(fakePlayer, FakeplayerMetadata.NAME_SOURCE.key, FakeplayerMetadata.NAME_SEQUENCE.key);
        var source = metas[0];
        var sequence = metas[1];
        nameManager.giveback(source.asString(), sequence.asInt());

        Action.dropInventory(UnwrapUtils.getServerPlayer(fakePlayer));

        Arrays.stream(FakeplayerMetadata.values()).map(m -> m.key).forEach(
                key -> {
                    if (fakePlayer.hasMetadata(key)) {
                        fakePlayer.removeMetadata(key, Main.getInstance());
                    }
                }
        );
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
                .filter(p -> p.getMetadata(FakeplayerMetadata.CREATOR.key)
                        .stream()
                        .anyMatch(meta -> meta.asString().equals(name)))
                .collect(Collectors.toList());
    }

    /**
     * 判断一名玩家是否是假人
     *
     * @param player 玩家
     * @return 是否是假人
     */
    public boolean isFake(@NotNull Player player) {
        return !player.getMetadata(FakeplayerMetadata.CREATOR.key).isEmpty();
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
                .filter(p -> p.getMetadata(FakeplayerMetadata.CREATOR_IP.key).stream().anyMatch(meta -> meta.asString().equals(address)))
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
            log.warning("Could not generate a UUID bound with name which is never used at this server, using random UUID as fallback: " + uuid);
        }
        return uuid;
    }

    public void performCommands(@NotNull Player player) {
        if (!isFake(player)) {
            return;
        }

        for (var cmd : properties.getSelfCommands()) {
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
        for (var cmd : properties.getPreparingCommands()) {
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


}
