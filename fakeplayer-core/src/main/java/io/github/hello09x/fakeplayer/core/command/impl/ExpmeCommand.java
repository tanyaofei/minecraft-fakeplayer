package io.github.hello09x.fakeplayer.core.command.impl;


import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.utils.ExperienceUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
public class ExpmeCommand extends AbstractCommand {

    public void expme(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var exp = ExperienceUtils.getExp(target);

        if (exp == 0) {
            sender.sendMessage(
                    translatable(
                            "fakeplayer.command.expme.error.non-experience",
                            text(target.getName(), WHITE)
                    ).color(GRAY)
            );
            return;
        }

        ExperienceUtils.clean(target);
        sender.giveExp(exp, false);
        sender.sendMessage(translatable(
                "fakeplayer.command.expme.success",
                text(target.getName(), WHITE),
                text(exp, DARK_GREEN)
        ).color(GRAY));
    }


}
