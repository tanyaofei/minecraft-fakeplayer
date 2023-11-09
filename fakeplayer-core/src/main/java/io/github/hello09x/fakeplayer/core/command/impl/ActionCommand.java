package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActionCommand extends AbstractCommand {

    public final static ActionCommand instance = new ActionCommand();

    private final ActionManager actionManager = ActionManager.instance;

    public @NotNull CommandExecutor action(@NotNull ActionType action, @NotNull ActionSetting setting) {
        return (sender, args) -> action(sender, args, action, setting.clone());
    }

    public void action(
            @NotNull CommandSender sender,
            @NotNull CommandArguments args,
            @NotNull ActionType action,
            @NotNull ActionSetting setting
    ) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        actionManager.setAction(target, action, setting);

        String baseline;
        if (setting.equals(ActionSetting.stop())) {
            baseline = i18n.asString("fakeplayer.command.action.stop");
        } else if (setting.equals(ActionSetting.once())) {
            baseline = i18n.asString("fakeplayer.command.action.once");
        } else {
            baseline = i18n.asString("fakeplayer.command.action.continuous");
        }

        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + baseline + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("action", i18n.translate(action, WHITE))
        ));
    }

    public void swap(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        var item1 = target.getInventory().getItemInMainHand();
        var item2 = target.getInventory().getItemInOffHand();

        target.getInventory().setItemInMainHand(item2);
        target.getInventory().setItemInOffHand(item1);

        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.swap.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE))
        ));
    }

}
