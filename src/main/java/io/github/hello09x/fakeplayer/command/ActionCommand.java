package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import io.github.hello09x.fakeplayer.entity.action.PlayerActionManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class ActionCommand extends AbstractCommand {

    public final static ActionCommand instance = new ActionCommand();

    private final PlayerActionManager actionManager = PlayerActionManager.instance;

    public void action(@NotNull CommandSender sender, @NotNull CommandArguments args, @NotNull Action action, @NotNull ActionSetting setting) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        actionManager.setAction(target, action, setting);
    }

    public void sneak(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var sneaking = args
                .getOptional("sneaking")
                .map(String.class::cast)
                .map(Boolean::valueOf)
                .orElse(!target.isSneaking());

        target.setSneaking(sneaking);

        sender.sendMessage(textOfChildren(
                text(target.getName(), WHITE),
                text("现在", GRAY),
                text(sneaking ? "潜行中" : "取消了潜行", GRAY)
        ));

    }

}
