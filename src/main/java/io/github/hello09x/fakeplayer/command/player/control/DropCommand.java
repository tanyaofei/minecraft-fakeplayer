package io.github.hello09x.fakeplayer.command.player.control;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DropCommand extends AbstractCommand {

    public final static DropCommand instance = new DropCommand(
            "丢弃手上的物品",
            "/fp drop [假人名称]",
            "fakeplayer.control"
    );

    public DropCommand(
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
        target.dropItem(true);
        return true;
    }

}
