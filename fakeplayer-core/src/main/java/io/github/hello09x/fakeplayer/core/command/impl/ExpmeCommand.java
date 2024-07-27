package io.github.hello09x.fakeplayer.core.command.impl;


import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.io.Experiences;
import io.github.hello09x.devtools.transaction.TranslatorUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
public class ExpmeCommand extends AbstractCommand {

    public void expme(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var exp = Experiences.getExp(target);
        var local = TranslatorUtils.getLocale(sender);

        if (exp == 0) {
            sender.sendMessage(translator.translate(
                    "fakeplayer.command.expme.error.non-experience",
                    local,
                    GRAY,
                    Placeholder.component("name", text(target.getName(), WHITE)))
            );
            return;
        }

        Experiences.clean(target);
        sender.giveExp(exp, false);
        sender.sendMessage(translator.translate(
                "fakeplayer.command.expme.success", local, GRAY,
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("experience", text(exp, DARK_GREEN))
        ));
    }


}
