package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.transaction.TranslatorUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@Singleton
public class KillCommand extends AbstractCommand {

    /**
     * 移除假人
     */
    public void kill(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var targets = super.getTargets(sender, args);
        var locale = TranslatorUtils.getLocale(sender);

        if (targets.isEmpty()) {
            sender.sendMessage(translator.translate("fakeplayer.command.kill.error.non-removed", locale, GRAY));
            return;
        }

        var names = new StringJoiner(", ");
        for (var target : targets) {
            if (manager.remove(target.getName(), "command kill")) {
                names.add(target.getName());
            }
        }
        sender.sendMessage(textOfChildren(
                translator.translate("fakeplayer.command.kill.success.removed", locale, GRAY),
                space(),
                text(names.toString())
        ));
    }


}
