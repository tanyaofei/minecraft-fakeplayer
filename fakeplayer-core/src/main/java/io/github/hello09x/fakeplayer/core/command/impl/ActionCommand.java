package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class ActionCommand extends AbstractCommand {

    private final ActionManager actionManager;

    @Inject
    public ActionCommand(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public @NotNull CommandExecutor action(@NotNull Action.ActionType action, @NotNull Action.ActionSetting setting) {
        return (sender, args) -> action(sender, args, action, setting.clone());
    }

    /**
     * 执行动作
     */
    public void action(
            @NotNull CommandSender sender,
            @NotNull CommandArguments args,
            @NotNull Action.ActionType action,
            @NotNull Action.ActionSetting setting
    ) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        actionManager.setAction(target, action, setting);
        if (!setting.equals(Action.ActionSetting.once()) || sender instanceof ConsoleCommandSender) {
            sender.sendMessage(translatable("fakeplayer.command.generic.success"));
        }
    }

}
