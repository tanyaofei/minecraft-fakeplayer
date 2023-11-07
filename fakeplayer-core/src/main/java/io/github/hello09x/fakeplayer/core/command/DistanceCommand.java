package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.util.Mth;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.Objects;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DistanceCommand extends AbstractCommand {

    public final static DistanceCommand instance = new DistanceCommand();

    public void distance(
            @NotNull Player sender,
            @NotNull CommandArguments args
    ) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        var from = target.getLocation().toBlockLocation();
        var to = sender.getLocation().toBlockLocation();

        if (!Objects.equals(from.getWorld(), to.getWorld())) {
            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + i18n.asString("fakeplayer.command.distance.error.too-far") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
            return;
        }

        var euclidean = Mth.floor(from.distance(to), 0.5);
        var x = Math.abs(from.getBlockX() - to.getBlockX());
        var y = Math.abs(from.getBlockY() - to.getBlockY());
        var z = Math.abs(from.getBlockZ() - to.getBlockZ());

        sender.sendMessage(textOfChildren(
                miniMessage.deserialize(
                        "<gray>" + i18n.asString("fakeplayer.command.distance.baseline") + "</gray>",
                        Placeholder.component("name", text(target.getName(), WHITE))
                ),
                newline(),
                i18n.translate("fakeplayer.command.distance.euclidean", GRAY), space(), text(euclidean, WHITE), newline(),
                i18n.translate("fakeplayer.command.distance.x", GRAY), space(), text(x, WHITE), newline(),
                i18n.translate("fakeplayer.command.distance.y", GRAY), space(), text(y, WHITE), newline(),
                i18n.translate("fakeplayer.command.distance.z", GRAY), space(), text(z, WHITE)
        ));
    }


}
