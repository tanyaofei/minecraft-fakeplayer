package io.github.hello09x.fakeplayer.core.command;


import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.bedrock.io.Experiences;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ExpCommand extends AbstractCommand {


    public final static ExpCommand instance = new ExpCommand();

    public void expme(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var exp = Experiences.getExp(target);

        if (exp == 0) {
            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + I18n.asString("fakeplayer.command.expme.error.non-experience") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
            return;
        }

        Experiences.clean(target);
        sender.giveExp(exp, false);
        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + I18n.asString("fakeplayer.command.expme.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("experience", text(exp, DARK_GREEN))
        ));
    }


}
