package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

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

        String translationKey;
        if (setting.equals(Action.ActionSetting.stop())) {
            translationKey = "fakeplayer.command.action.stop";
        } else if (setting.equals(Action.ActionSetting.once())) {
            translationKey = "fakeplayer.command.action.once";
        } else {
            translationKey = "fakeplayer.command.action.continuous";
        }

        sender.sendMessage(i18n.translate(
                translationKey,
                GRAY,
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("action", i18n.translate(action, WHITE))
        ));
    }

}
