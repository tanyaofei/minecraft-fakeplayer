package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


/**
 * @author tanyaofei
 * @since 2024/7/26
 **/
@Singleton
public class DebugCommand {

    private final FakeplayerManager manager;
    private final NMSBridge bridge;

    @Inject
    public DebugCommand(FakeplayerManager manager, NMSBridge bridge) {
        this.manager = manager;
        this.bridge = bridge;
    }

    public void sendPluginMessage(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        var player = (Player) args.get("player");
        var channel = (String) args.get("channel");
        var message = (String) args.get("message");
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Main.getInstance(), channel);

        var msg = ByteStreams.newDataOutput();
        msg.writeUTF(message);
        player.sendPluginMessage(Main.getInstance(), channel, msg.toByteArray());
    }

}
