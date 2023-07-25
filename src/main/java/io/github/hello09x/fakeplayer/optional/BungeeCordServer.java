package io.github.hello09x.fakeplayer.optional;

import com.google.common.io.ByteStreams;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BungeeCordServer implements PluginMessageListener {


    public final static BungeeCordServer instance = new BungeeCordServer();

    private final Set<String> onlinePlayers = new HashSet<>();

    public BungeeCordServer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                refreshOnlinePlayersAsynchronously();
            }
        }, 0, 10_000);
    }

    public boolean isPlayerOnline(@NotNull String name) {
        return onlinePlayers.contains(name) || Bukkit.getServer().getPlayer(name) != null;
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
                .filter(p -> !FakeplayerManager.instance.isFake(p))
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


}
