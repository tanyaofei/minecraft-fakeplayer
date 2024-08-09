package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
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
        var fakes = super.getFakeplayers(sender, args);

        if (fakes.isEmpty()) {
            sender.sendMessage(translatable("fakeplayer.command.kill.error.non-removed", GRAY));
            return;
        }

        var names = new StringJoiner(", ");
        for (var fake : fakes) {
            if (manager.remove(fake.getName(), "command kill")) {
                names.add(fake.getName());
            }
        }
        sender.sendMessage(textOfChildren(
                translatable("fakeplayer.command.kill.success.removed", GRAY),
                space(),
                text(names.toString())
        ));
    }


}
