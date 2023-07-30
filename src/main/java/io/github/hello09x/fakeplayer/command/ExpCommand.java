package io.github.hello09x.fakeplayer.command;


import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.util.Experience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ExpCommand extends AbstractCommand {

    public final static ExpCommand instance = new ExpCommand();

    public void expme(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var exp = Experience.getExp(target);

        if (exp == 0) {
            sender.sendMessage(textOfChildren(
                    text(target.getName(), WHITE),
                    text(" 已经没有经验啦", GRAY)
            ));
            return;
        }

        target.setLevel(0);
        target.setExp(0);

        Experience.changeExp(sender, Experience.getExp(sender) + exp);
        sender.sendMessage(textOfChildren(
                text(target.getName(), WHITE),
                text(" 转移 ", GRAY),
                text(exp, DARK_GREEN),
                text(" 点经验值给你", GRAY)
        ));
    }


}
