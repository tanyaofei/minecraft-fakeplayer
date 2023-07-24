package io.github.hello09x.fakeplayer.manager;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.entity.FakePlayer;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.repository.model.Configs;
import io.github.hello09x.fakeplayer.util.AddressUtils;
import io.github.hello09x.fakeplayer.util.MetadataUtils;
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
import org.jetbrains.annotations.ApiStatus;
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

    private final static String META_KEY_CREATOR = "fakeplayer:creator";
    private final static String META_KEY_CREATOR_IP = "fakeplayer:creator-ip";
    private final static String META_KEY_NAME_SOURCE = "fakeplayer:name-source";
    private final static String META_KEY_NAME_SEQUENCE = "fakeplayer:name-sequence";
    private final static String META_KEY_NAME_ATTACK_TASK_ID = "fakeplayer:attack-task-id";

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    private final UsedIdRepository usedIdRepository = UsedIdRepository.instance;

    private final NameManager nameManager = NameManager.instance;

    private final UserConfigRepository userConfigRepository = UserConfigRepository.instance;

    private FakeplayerManager() {
        // 服务器 tps 过低删除所有假人
        new Timer().schedule(new TimerTask() {
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
    }

    /**
     * 创建一个假人
     *
     * @param creator 创建者
     * @param spawnAt 生成地点
     */
    public synchronized void spawn(
            @NotNull CommandSender creator,
            @NotNull Location spawnAt
    ) {
        var playerLimit = properties.getPlayerLimit();
        if (!creator.isOp() && playerLimit != Integer.MAX_VALUE && getAll(creator).size() >= playerLimit) {
            creator.sendMessage(text("你创建的假人数量已达到上限...", RED));
            return;
        }

        var serverLimit = properties.getServerLimit();
        if (!creator.isOp() && serverLimit != Integer.MAX_VALUE && getAll().size() >= serverLimit) {
            creator.sendMessage(text("服务器假人数量已达到上限...", RED));
            return;
        }

        if (!creator.isOp() && properties.isDetectIp() && countByAddress(AddressUtils.getAddress(creator)) >= 1) {
            creator.sendMessage(text("你所在 IP 创建的假人数量已达到上限...", RED));
            return;
        }

        var name = nameManager.take(creator);
        boolean invulnerable = true, lookAtEntity = true, collidable = true;
        if (creator instanceof Player p) {
            var creatorId = p.getUniqueId();
            invulnerable = userConfigRepository.selectOrDefault(creatorId, Configs.invulnerable);
            lookAtEntity = userConfigRepository.selectOrDefault(creatorId, Configs.look_at_entity);
            collidable = userConfigRepository.selectOrDefault(creatorId, Configs.collidable);
        }

        var serverPlayer = new FakePlayer(
                creator.getName(),
                ((CraftServer) Bukkit.getServer()).getServer(),
                generateId(name.name()),
                name.name()
        );

        var player = serverPlayer.getBukkitPlayer();
        player.setMetadata(META_KEY_CREATOR, new FixedMetadataValue(Main.getInstance(), creator.getName()));
        player.setMetadata(META_KEY_CREATOR_IP, new FixedMetadataValue(Main.getInstance(), AddressUtils.getAddress(creator)));
        player.setMetadata(META_KEY_NAME_SOURCE, new FixedMetadataValue(Main.getInstance(), name.source()));
        player.setMetadata(META_KEY_NAME_SEQUENCE, new FixedMetadataValue(Main.getInstance(), name.sequence()));
        player.playerListName(text(creator.getName() + "的假人").style(Style.style(GRAY, ITALIC)));

        serverPlayer.spawn(invulnerable, collidable, lookAtEntity);

        usedIdRepository.add(player.getUniqueId());

        dispatchCommands(player, properties.getPreparingCommands());

        // 先等待玩家 spawn 之后再 tp, 否则 tp 不生效
        // 可能会被别的插件干预, 因此 tp 两次
        var spawnpoint = spawnAt.getWorld().getSpawnLocation().clone();
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(spawnpoint);
            }
        }.runTaskLater(Main.getInstance(), 5);

        var moveTo = spawnAt.clone();
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(moveTo);
                moveTo.getWorld().playSound(moveTo, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }.runTaskLater(Main.getInstance(), 20);
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
                .ofNullable(Bukkit.getServer().getPlayer(name))
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
        return Optional.ofNullable(MetadataUtils.getFirst(fakePlayer, META_KEY_CREATOR))
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
                .filter(p -> !p.getMetadata(META_KEY_CREATOR).isEmpty())
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

        var metas = MetadataUtils.get(fakePlayer, META_KEY_NAME_SOURCE, META_KEY_NAME_SEQUENCE);
        var source = metas[0];
        var sequence = metas[1];
        nameManager.giveback(source.asString(), sequence.asInt());
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
                .filter(p -> p.getMetadata(META_KEY_CREATOR)
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
        return !player.getMetadata(META_KEY_CREATOR).isEmpty();
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
                .filter(p -> p.getMetadata(META_KEY_CREATOR_IP).stream().anyMatch(meta -> meta.asString().equals(address)))
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

    @ApiStatus.Experimental
    public void setAttack(@NotNull Player fakePlayer, int tickPeriod) {
        if (!isFake(fakePlayer)) {
            return;
        }

        var meta = MetadataUtils.getFirst(fakePlayer, META_KEY_NAME_ATTACK_TASK_ID);
        if (meta != null) {
            var oldTaskId = meta.asInt();
            Bukkit.getScheduler().cancelTask(oldTaskId);
            fakePlayer.removeMetadata(META_KEY_NAME_ATTACK_TASK_ID, Main.getInstance());
        }

        Runnable attack = () -> {
            var entity = fakePlayer.getTargetEntity(3);
            if (entity != null) {
                fakePlayer.swingMainHand();
                fakePlayer.attack(entity);
            }
        };

        if (tickPeriod <= 1) {
            attack.run();
            return;
        }

        var task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!fakePlayer.isOnline()) {
                    cancel();
                }
                attack.run();
            }
        }.runTaskTimer(Main.getInstance(), 0, tickPeriod);
        fakePlayer.setMetadata(META_KEY_NAME_ATTACK_TASK_ID, new FixedMetadataValue(Main.getInstance(), task.getTaskId()));
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
