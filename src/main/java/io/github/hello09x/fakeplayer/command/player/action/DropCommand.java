package io.github.hello09x.fakeplayer.command.player.action;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import io.github.hello09x.fakeplayer.entity.action.PlayerActionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DropCommand extends AbstractCommand {

    private final static PlayerActionManager manager = PlayerActionManager.instance;

    public final static DropCommand instance = new DropCommand(
            "丢弃手上的物品",
            "/fp drop [假人] [-a|--all]",
            "fakeplayer.action"
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

        var dropAll = hasFlag(args, "-a") || hasFlag(args, "--all");
        manager.setAction(target, dropAll ? Action.DROP_STACK : Action.DROP_ITEM, ActionSetting.once());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            return Collections.singletonList("--all");
        }
        return super.onTabComplete(sender, command, label, args);
    }
}
