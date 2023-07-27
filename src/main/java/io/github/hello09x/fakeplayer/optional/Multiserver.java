package io.github.hello09x.fakeplayer.optional;

import com.google.common.io.ByteStreams;
import io.github.hello09x.fakeplayer.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Multiserver implements PluginMessageListener {


    public final static Multiserver instance = new Multiserver();

    private final Set<String> onlinePlayers = new HashSet<>();

    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    public Multiserver() {
        timer.scheduleAtFixedRate(this::refreshOnlinePlayersAsynchronously, 0, 10, TimeUnit.SECONDS);
    }

    public boolean isPlayerOnline(@NotNull String name) {
        return onlinePlayers.contains(name) || (Bukkit.getServer().getPlayerExact(name) != null);
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

        var in = ByteStreams.newDataInput(message);
        if (!in.readUTF().equals("PlayerList")) {
            return;
        }

        if (!in.readUTF().equals("ALL")) {
            return;
        }

        var onlinePlayers = in.readUTF().split(", ");
        synchronized (this.onlinePlayers) {
            this.onlinePlayers.clear();
            this.onlinePlayers.addAll(Arrays.asList(onlinePlayers));
        }
    }

    public void refreshOnlinePlayersAsynchronously() {
        var recipient = Bukkit
                .getServer()
                .getOnlinePlayers()
                .stream()
                .findAny()
                .orElse(null);

        if (recipient == null) {
            return;
        }

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


}
