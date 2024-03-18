package io.github.hello09x.fakeplayer.core.manager;

import com.google.common.io.ByteStreams;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WildFakeplayerManager implements PluginMessageListener {

    public final static WildFakeplayerManager instance = new WildFakeplayerManager();
    private final static Logger log = Main.getInstance().getLogger();
    private final static boolean IS_BUNGEECORD = Bukkit
            .getServer()
            .spigot()
            .getSpigotConfig()
            .getBoolean("settings.bungeecord", false);
    private final static String CHANNEL = "BungeeCord";
    private final static String SUB_CHANNEL = "PlayerList";

    /**
     * 定义探测到连续 n 次不在线时进行清理
     * <br>
     * 仅在 {@link #IS_BUNGEECORD} 为 {@code true} 时生效
     */
    private final static int CLEANUP_THRESHOLD = 2;
    private final static int CLEANUP_PERIOD = 6000;

    private final FakeplayerManager manager = FakeplayerManager.instance;
    private final FakeplayerConfig config = FakeplayerConfig.instance;
    private final Map<String, AtomicInteger> offline = new HashMap<>();

    public WildFakeplayerManager() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::cleanup, 0, CLEANUP_PERIOD);
    }

    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte @NotNull [] message
    ) {
        if (!channel.equals(CHANNEL)) {
            return;
        }

        @SuppressWarnings("UnstableApiUsage")
        var in = ByteStreams.newDataInput(message);
        if (!in.readUTF().equals(SUB_CHANNEL)) {
            return;
        }

        if (!in.readUTF().equals("ALL")) {
            return;
        }

        var players = new HashSet<String>();
        players.addAll(Arrays.asList(in.readUTF().split(", ")));
        players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        this.cleanup0(players);
    }

    /**
     * 清除所有不在 {@code online} 列表中的玩家的假人
     *
     * @param online 在线玩家
     */
    public void cleanup0(@NotNull Set<String> online) {
        @SuppressWarnings("all")
        var group = manager.getAll()
                .stream()
                .collect(Collectors.groupingBy(manager::getCreatorName));

        for (var entry : group.entrySet()) {
            var creator = entry.getKey();
            var targets = entry.getValue();
            if (targets.isEmpty() || online.contains(creator)) {
                continue;
            }

            if (offline.computeIfAbsent(creator, x -> new AtomicInteger()).incrementAndGet() < CLEANUP_THRESHOLD) {
                continue;
            }

            for (var target : targets) {
                manager.remove(target.getName(), "Creator offline");
            }
            log.info("%s is offline more than %d ticks, removing %d fake players".formatted(
                    creator,
                    CLEANUP_PERIOD * CLEANUP_THRESHOLD,
                    targets.size())
            );
        }

        for (var player : online) {
            offline.remove(player);
        }
    }

    /**
     * 清理召唤者下线的假人
     */
    public void cleanup() {
        if (!config.isFollowQuiting()) {
            return;
        }

        // 非 bungeeCord 服务器立即清理
        if (!IS_BUNGEECORD) {
            this.cleanup0(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet()));
            return;
        }

        // BungeeCord 服务器请求获取所有服务器在线玩家后
        // 在接收到在线列表后再进行清理
        var recipient = Bukkit
                .getServer()
                .getOnlinePlayers()
                .stream()
                .filter(manager::notContains)
                .findAny()
                .orElse(null);

        if (recipient == null) {
            return;
        }

        @SuppressWarnings("UnstableApiUsage")
        var out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_CHANNEL);
        out.writeUTF("ALL");
        recipient.sendPluginMessage(
                Main.getInstance(),
                CHANNEL,
                out.toByteArray()
        );
    }

}
