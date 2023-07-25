package io.github.hello09x.fakeplayer.command.player.action;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import io.github.hello09x.fakeplayer.entity.action.PlayerActionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DropInvCommand extends AbstractCommand {

    private final static PlayerActionManager manager = PlayerActionManager.instance;

    public final static DropInvCommand instance = new DropInvCommand(
            "丢弃背包的物品",
            "/fp dropinv [假人]",
            "fakeplayer.action"
    );

    public DropInvCommand(
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

        manager.setAction(target, Action.DROP_INVENTORY, ActionSetting.once());
        return true;
    }

}
