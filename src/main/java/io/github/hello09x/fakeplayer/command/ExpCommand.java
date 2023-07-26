package io.github.hello09x.fakeplayer.command;


import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class ExpCommand extends AbstractCommand {

    public final static ExpCommand instance = new ExpCommand();

    public void expme(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var exp = target.getTotalExperience();

        target.setTotalExperience(0);
        sender.setTotalExperience(sender.getTotalExperience() + exp);
        sender.sendMessage(textOfChildren(
                text(target.getName(), GRAY),
                text(" 转移 ", GRAY),
                text(exp, DARK_GREEN),
                text(" 点经验值给你", GRAY)
        ));
    }


}
