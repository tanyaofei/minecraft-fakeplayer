package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.fakeplayer.core.util.Mth;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.Objects;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@Singleton
public class DistanceCommand extends AbstractCommand {

    /**
     * 查看距离
     */
    public void distance(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        var from = target.getLocation().toBlockLocation();
        var to = sender.getLocation().toBlockLocation();
        var locale = TranslatorUtils.getLocale(sender);

        if (!Objects.equals(from.getWorld(), to.getWorld())) {
            sender.sendMessage(translator.translate(
                    "fakeplayer.command.distance.error.too-far", locale, GRAY,
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
            return;
        }

        var euclidean = Mth.floor(from.distance(to), 0.5);
        var x = Math.abs(from.getBlockX() - to.getBlockX());
        var y = Math.abs(from.getBlockY() - to.getBlockY());
        var z = Math.abs(from.getBlockZ() - to.getBlockZ());

        sender.sendMessage(textOfChildren(
                translator.translate(
                        "fakeplayer.command.distance.title", locale, GRAY,
                        Placeholder.component("name", text(target.getName(), WHITE))
                ),
                newline(),
                translator.translate("fakeplayer.command.distance.euclidean", locale, GRAY), space(), text(euclidean, WHITE), newline(),
                translator.translate("fakeplayer.command.distance.x", locale, GRAY), space(), text(x, WHITE), newline(),
                translator.translate("fakeplayer.command.distance.y", locale, GRAY), space(), text(y, WHITE), newline(),
                translator.translate("fakeplayer.command.distance.z", locale, GRAY), space(), text(z, WHITE)
        ));
    }


}
