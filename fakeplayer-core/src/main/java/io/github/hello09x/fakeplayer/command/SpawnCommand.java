package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.command.MessageException;
import io.github.hello09x.bedrock.page.Page;
import io.github.hello09x.fakeplayer.util.Mth;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.time.LocalDateTime;
import java.util.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class SpawnCommand extends AbstractCommand {

    public final static SpawnCommand instance = new SpawnCommand();

    private static String toLocationString(@NotNull Location location) {
        return location.getWorld().getName()
                + ": "
                + StringUtils.joinWith(", ",
                Mth.floor(location.getX(), 0.5),
                Mth.floor(location.getY(), 0.5),
                Mth.floor(location.getZ(), 0.5));
    }

    public void spawn(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        var name = (String) args.get("名称");
        var world = (World) args.get("世界");
        var location = (Location) args.get("坐标");

        Location spawnpoint;
        if (world == null || location == null) {
            spawnpoint = sender instanceof Player p
                    ? p.getLocation().clone()
                    : Bukkit.getWorlds().get(0).getSpawnLocation().clone();
        } else {
            spawnpoint = new Location(
                    world,
                    location.getX(),
                    location.getY(),
                    location.getZ()
            );
        }

        var keepalive = config.getKeepalive();
        Player player;
        try {
            player = fakeplayerManager.spawn(
                    sender,
                    Optional.ofNullable(name).map(String::trim).orElse(""),
                    spawnpoint,
                    keepalive == null ? null : LocalDateTime.now().plus(keepalive)
            );
        } catch (MessageException e) {
            sender.sendMessage(e.asComponent());
            return;
        }

        sender.sendMessage(textOfChildren(
                text("你创建了假人 ", GRAY),
                text(player.getName()),
                text(", 位于 ", GRAY),
                text(toLocationString(spawnpoint)),
                keepalive == null ? empty() : textOfChildren(
                        text(", 存活时间 ", GRAY),
                        text(keepalive.toString()
                                .substring(2)
                                .replaceAll("(\\\\d[HMS])(?!$)", "$1")
                                .toLowerCase(Locale.ROOT))
                )
        ));
    }

    public void kill(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        @SuppressWarnings("unchecked")
        var targets = (List<Player>) args.get("targets");
        if (targets == null) {
            var reserved = fakeplayerManager.getAll(sender);
            if (reserved.size() == 1) {
                targets = Collections.singletonList(reserved.get(0));
            } else {
                targets = Collections.emptyList();
            }
        }

        if (targets.isEmpty()) {
            sender.sendMessage(text("没有移除任何假人", GRAY));
            return;
        }

        var names = new StringJoiner(", ");
        for (var target : targets) {
            if (fakeplayerManager.remove(target.getName(), "command kill")) {
                names.add(target.getName());
            }
        }
        sender.sendMessage(textOfChildren(
                text("你移除了假人: ", GRAY),
                text(names.toString())
        ));
    }

    public void list(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        var page = (int) args.getOptional("页码").orElse(1);
        var size = (int) args.getOptional("数量").orElse(10);

        var fakers = sender.isOp()
                ? fakeplayerManager.getAll()
                : fakeplayerManager.getAll(sender);

        var p = Page.of(fakers, page, size);

        var canTp = sender instanceof Player && sender.hasPermission(Permission.tp);
        sender.sendMessage(p.asComponent(
                text("假人", AQUA, BOLD),
                fakeplayer -> textOfChildren(
                        text(fakeplayer.getName() + " (" + fakeplayerManager.getCreator(fakeplayer) + ")", GOLD),
                        text(" - ", GRAY),
                        text(toLocationString(fakeplayer.getLocation()), WHITE),
                        canTp ? text(" [<--传送]", AQUA).clickEvent(runCommand("/fp tp " + fakeplayer.getName())) : empty(),
                        text(" [<--移除]", RED).clickEvent(runCommand("/fp kill " + fakeplayer.getName()))
                ),
                i -> "/fp list " + i + " " + size
        ));
    }

    public void distance(
            @NotNull Player sender,
            @NotNull CommandArguments args
    ) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var from = target.getLocation().toBlockLocation();
        var to = sender.getLocation().toBlockLocation();

        if (!from.getWorld().equals(to.getWorld())) {
            sender.sendMessage(textOfChildren(
                    text("你离 ", GRAY),
                    text(target.getName()),
                    text(" 十分遥远", GRAY)
            ));
            return;
        }

        var euclidean = Mth.floor(from.distance(to), 0.5);
        var x = Math.abs(from.getBlockX() - to.getBlockX());
        var y = Math.abs(from.getBlockY() - to.getBlockY());
        var z = Math.abs(from.getBlockZ() - to.getBlockZ());

        sender.sendMessage(textOfChildren(
                text("你与 ", GRAY),
                text(target.getName(), WHITE),
                text(" 的距离: ", GRAY),
                newline(),
                text("- 欧氏距离: ", GRAY), text(euclidean, WHITE),
                newline(),
                text("- x 距离: ", GRAY), text(x, WHITE),
                newline(),
                text("- y 距离: ", GRAY), text(y, WHITE),
                newline(),
                text("- z 距离: ", GRAY), text(z, WHITE)
        ));
    }

}
