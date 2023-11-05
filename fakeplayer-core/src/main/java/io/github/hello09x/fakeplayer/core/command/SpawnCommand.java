package io.github.hello09x.fakeplayer.core.command;

import com.google.common.base.Throwables;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.command.MessageException;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.bedrock.page.Page;
import io.github.hello09x.bedrock.task.Tasks;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.util.Mth;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class SpawnCommand extends AbstractCommand {

    public final static SpawnCommand instance = new SpawnCommand();
    private final static Logger log = Main.getInstance().getLogger();

    private final static MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static String toLocationString(@NotNull Location location) {
        return location.getWorld().getName()
                + ": "
                + StringUtils.joinWith(", ",
                Mth.floor(location.getX(), 0.5),
                Mth.floor(location.getY(), 0.5),
                Mth.floor(location.getZ(), 0.5));
    }

    public void spawn(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        var name = (String) args.get("name");
        var world = (World) args.get("world");
        var location = (Location) args.get("location");

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
        fakeplayerManager.spawnAsync(
                        sender,
                        Optional.ofNullable(name).map(String::trim).orElse(""),
                        spawnpoint,
                        keepalive == null ? null : LocalDateTime.now().plus(keepalive)
                )
                .exceptionally(e -> {
                    var message = Optional.ofNullable(e.getCause())
                            .filter(cause -> cause instanceof MessageException)
                            .map(MessageException.class::cast)
                            .map(MessageException::asComponent)
                            .orElse(null);

                    if (message != null) {
                        Tasks.run(() -> sender.sendMessage(message), Main.getInstance());
                    } else {
                        Tasks.run(() -> sender.sendMessage(I18n.translate(translatable("fakeplayer.command.spawn.error.unknown", RED))), Main.getInstance());
                        log.severe(Throwables.getStackTraceAsString(e));
                    }
                    return null;
                })
                .thenAccept(player -> {
                    if (player == null) {
                        return;
                    }
                    Tasks.run(() -> {
                        Component message;
                        if (keepalive == null) {
                            message = MINI_MESSAGE.deserialize(
                                    "<gray>" + I18n.asString("fakeplayer.command.spawn.success.without-keepalive") + "</gray>",
                                    Placeholder.component("name", text(player.getName(), WHITE)),
                                    Placeholder.component("location", text(toLocationString(spawnpoint), WHITE))
                            );
                        } else {
                            message = MINI_MESSAGE.deserialize(
                                    "<gray>" + I18n.asString("fakeplayer.command.spawn.success.with-keepalive") + "</gray>",
                                    Placeholder.component("name", text(player.getName(), WHITE)),
                                    Placeholder.component("location", text(toLocationString(spawnpoint), WHITE)),
                                    Placeholder.component("remove-at", text(keepalive.toString()
                                            .substring(2)
                                            .replaceAll("(\\\\d[HMS])(?!$)", "$1")
                                            .toLowerCase(Locale.ROOT)))
                            );
                        }
                        sender.sendMessage(textOfChildren(message));
                    }, Main.getInstance());
                });

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
            sender.sendMessage(text(I18n.asString("fakeplayer.command.kill.error.non-removed"), GRAY));
            return;
        }

        var names = new StringJoiner(", ");
        for (var target : targets) {
            if (fakeplayerManager.remove(target.getName(), "command kill")) {
                names.add(target.getName());
            }
        }
        sender.sendMessage(textOfChildren(
                I18n.translate(translatable("fakeplayer.command.kill.success.removed", GRAY)),
                space(),
                text(names.toString())
        ));
    }

    public void list(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        var page = (int) args.getOptional("page").orElse(1);
        var size = (int) args.getOptional("size").orElse(10);

        var fakers = sender.isOp()
                ? fakeplayerManager.getAll()
                : fakeplayerManager.getAll(sender);

        var p = Page.of(fakers, page, size);

        var canTp = sender instanceof Player && sender.hasPermission(Permission.tp);
        sender.sendMessage(p.asComponent(
                text("假人", AQUA, BOLD),
                fakeplayer -> textOfChildren(
                        text(fakeplayer.getName() + " (" + fakeplayerManager.getCreatorName(fakeplayer) + ")", GOLD),
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
