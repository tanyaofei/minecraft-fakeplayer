package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DropCommand extends AbstractCommand {

    public final static DropCommand instance = new DropCommand();

    public @NotNull CommandExecutor drop() {
        return (sender, args) -> ActionCommand.instance.action(
                sender,
                args,
                args.getOptional("all").isPresent() ? ActionType.DROP_STACK : ActionType.DROP_INVENTORY,
                ActionSetting.once()
        );
    }

    public @NotNull CommandExecutor dropinv() {
        return ActionCommand.instance.action(ActionType.DROP_INVENTORY, ActionSetting.once());
    }

}
