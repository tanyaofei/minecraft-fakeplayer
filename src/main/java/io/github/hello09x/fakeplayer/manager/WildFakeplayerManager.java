package io.github.hello09x.fakeplayer.manager;

import com.google.common.io.ByteStreams;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WildFakeplayerManager implements PluginMessageListener {


    public final static WildFakeplayerManager instance = new WildFakeplayerManager();
    private final static Logger log = Main.getInstance().getLogger();
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private final FakeplayerManager manager = FakeplayerManager.instance;
    private final FakeplayerProperties properties = FakeplayerProperties.instance;
    private final Set<String> bungeePlayers = new HashSet<>();

    public WildFakeplayerManager() {
        timer.scheduleAtFixedRate(this::preCleanup, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte @NotNull [] message
    ) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        @SuppressWarnings("UnstableApiUsage")
        var in = ByteStreams.newDataInput(message);
        if (!in.readUTF().equals("PlayerList")) {
            return;
        }

        if (!in.readUTF().equals("ALL")) {
            return;
        }

        synchronized (this) {
            this.bungeePlayers.clear();
            this.bungeePlayers.addAll(Arrays.asList(in.readUTF().split(", ")));
        }

        this.cleanup();
    }

    public void cleanup() {
        @SuppressWarnings("all")
        var group = manager.getAll()
                .stream()
                .collect(Collectors.groupingBy(manager::getCreator));

        for (var entry : group.entrySet()) {
            var creator = entry.getKey();
            var targets = entry.getValue();
            if (targets.isEmpty() || isPlayerOnline(creator)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (var target : targets) {
                        manager.remove(target.getName());
                    }
                    log.info(String.format("玩家 %s 已不在线, 移除他创建的 %d 个假人", entry.getKey(), entry.getValue().size()));
                }

            }.runTask(Main.getInstance());
        }
    }

    public void preCleanup() {
        if (!properties.isFollowQuiting()) {
            return;
        }

        if (!Bukkit.getServer().spigot().getSpigotConfig().getBoolean("settings.bungeecord", false)) {
            cleanup();
            return;
        }

        var recipient = Bukkit
                .getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> !manager.isFake(p))
                .findAny()
                .orElse(null);

        if (recipient == null) {
            return;
        }

        @SuppressWarnings("UnstableApiUsage")
        var out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");
        recipient.sendPluginMessage(
                Main.getInstance(),
                "BungeeCord",
                out.toByteArray()
        );
    }

    public void onDisable() {
        this.timer.shutdown();
    }

    private boolean isPlayerOnline(@NotNull String name) {
        return Bukkit.getConsoleSender().getName().equals(name)
                || bungeePlayers.contains(name)
                || Bukkit.getServer().getPlayerExact(name) != null;
    }

}
