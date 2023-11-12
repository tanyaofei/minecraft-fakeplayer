package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KillCommand extends AbstractCommand {

    public final static KillCommand instance = new KillCommand();

    /**
     * 移除假人
     */
    public void kill(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var targets = super.getTargets(sender, args);

        if (targets.isEmpty()) {
            sender.sendMessage(text(i18n.asString("fakeplayer.command.kill.error.non-removed"), GRAY));
            return;
        }

        var names = new StringJoiner(", ");
        for (var target : targets) {
            if (manager.remove(target.getName(), "command kill")) {
                names.add(target.getName());
            }
        }
        sender.sendMessage(textOfChildren(
                i18n.translate("fakeplayer.command.kill.success.removed", GRAY),
                space(),
                text(names.toString())
        ));
    }


}
