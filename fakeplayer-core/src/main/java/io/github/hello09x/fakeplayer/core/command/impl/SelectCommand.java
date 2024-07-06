package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@Singleton
public class SelectCommand extends AbstractCommand {

    public void select(@NotNull Player sender, @NotNull CommandArguments args) {
        var target = this.getTargetNullable(sender, args);
        manager.setSelection(sender, target);
        if (target == null) {
            sender.sendMessage(i18n.translate("fakeplayer.command.select.success.clear", GRAY));
        } else {
            sender.sendMessage(i18n.translate(
                    "fakeplayer.command.select.success.selected", GRAY,
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
        }
    }

    public void selection(@NotNull Player sender, @NotNull CommandArguments args) {
        var selection = manager.getSelection(sender);
        if (selection == null) {
            sender.sendMessage(i18n.translate("fakeplayer.command.selection.error.none", GRAY));
        } else {
            sender.sendMessage(i18n.translate(
                    "fakeplayer.command.selection.success", GRAY,
                    Placeholder.component("name", text(selection.getName(), WHITE))
            ));
        }
    }

}
