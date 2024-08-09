package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.command.Page;
import io.github.hello09x.fakeplayer.core.command.Permission;
import io.github.hello09x.fakeplayer.core.util.Mth;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

@Singleton
public class ListCommand extends AbstractCommand {


    private static String toLocationString(@NotNull Location location) {
        return location.getWorld().getName()
                + ": "
                + StringUtils.joinWith(", ",
                                       Mth.floor(location.getX(), 0.5),
                                       Mth.floor(location.getY(), 0.5),
                                       Mth.floor(location.getZ(), 0.5));
    }

    public void list(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        var page = (int) args.getOptional("page").orElse(1);
        var size = (int) args.getOptional("size").orElse(10);

        var fakes = sender.isOp()
                ? manager.getAll()
                : manager.getAll(sender);

        var p = Page.of(fakes, page, size);

        var allowsTp = sender instanceof Player && sender.hasPermission(Permission.tp);
        sender.sendMessage(p.asComponent(
                translatable("fakeplayer.command.list.title", AQUA, BOLD),
                fakeplayer -> {
                    var partTp = allowsTp
                            ? textOfChildren(space(), translatable("fakeplayer.command.list.button.teleport", AQUA).clickEvent(runCommand("/fp tp " + fakeplayer.getName())))
                            : empty();

                    var partKill = textOfChildren(space(), translatable("fakeplayer.command.list.button.kill", RED)).clickEvent(runCommand("/fp kill " + fakeplayer.getName()));

                    return textOfChildren(
                            text(fakeplayer.getName() + " (" + manager.getCreatorName(fakeplayer) + ")", GOLD),
                            text(" - ", GRAY),
                            text(toLocationString(fakeplayer.getLocation()), WHITE),
                            partTp,
                            partKill
                    );
                },
                i -> "/fp list " + i + " " + size
        ));
    }


}
