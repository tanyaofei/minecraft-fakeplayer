package io.github.hello09x.fakeplayer.core.manager;

import com.google.common.io.ByteStreams;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WildFakeplayerManager implements PluginMessageListener {


    public final static WildFakeplayerManager instance = new WildFakeplayerManager();
    private final static Logger log = Main.getInstance().getLogger();
    private final static boolean IS_BUNGEE_CORD = Bukkit.getServer().spigot().getSpigotConfig().getBoolean("settings.bungeecord", false);
    private final static String CHANNEL = "BungeeCord";
    private final static String SUB_CHANNEL = "PlayerList";

    private final FakeplayerManager manager = FakeplayerManager.instance;
    private final FakeplayerConfig config = FakeplayerConfig.instance;

    @NotNull
    private final Set<String> bungeeCordPlayers = new HashSet<>();

    public WildFakeplayerManager() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::cleanup, 0, 100);
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

        this.bungeeCordPlayers.clear();
        this.bungeeCordPlayers.addAll(Arrays.asList(in.readUTF().split(", ")));
        this.cleanup0();
    }

    public void cleanup0() {
        @SuppressWarnings("all")
        var group = manager.getAll()
                .stream()
                .collect(Collectors.groupingBy(manager::getCreatorName));

        for (var entry : group.entrySet()) {
            var creator = entry.getKey();
            var targets = entry.getValue();
            if (targets.isEmpty() || this.isOnline(creator)) {
                continue;
            }

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                for (var target : targets) {
                    manager.remove(target.getName(), "Creator offline");
                }
                log.info("%s is offline, removing %d fake players".formatted(creator, targets.size()));
            });
        }
    }

    /**
     * 清理召唤者下线的假人
     *
     * @see #onPluginMessageReceived(String, Player, byte[]) 如果是 BungeeCord 服务器则执行这个方法时才清理
     */
    public void cleanup() {
        if (!config.isFollowQuiting()) {
            return;
        }

        // 非 bungeeCord 服务器立即清理
        if (!IS_BUNGEE_CORD) {
            this.cleanup0();
            return;
        }

        // BungeeCord 服务器请求获取所有服务器在线玩家后
        // 在接收到在线列表后再进行清理
        var recipient = Bukkit
                .getServer()
                .getOnlinePlayers()
                .stream()
                .filter(manager::isNotFake)
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

    private boolean isOnline(@NotNull String name) {
        return Bukkit.getConsoleSender().getName().equals(name)
                || this.bungeeCordPlayers.contains(name)
                || Bukkit.getServer().getPlayerExact(name) != null;
    }

}
