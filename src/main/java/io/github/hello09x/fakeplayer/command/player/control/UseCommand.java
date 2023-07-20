package io.github.hello09x.fakeplayer.command.player.control;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UseCommand extends AbstractCommand {


    public final static UseCommand instance = new UseCommand(
            "控制假人点击鼠标右键",
            "/fp use [名称] [频率]",
            "fakeplayer.control"
    );


    public UseCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }

        var item = target.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return true;
        }


        var packet = new PacketContainer(PacketType.Play.Client.USE_ITEM);
        packet.getIntegers().write(0, 0);

        Main.getProtocolManager().receiveClientPacket(
                target,
                packet
        );

        return true;
    }
}
