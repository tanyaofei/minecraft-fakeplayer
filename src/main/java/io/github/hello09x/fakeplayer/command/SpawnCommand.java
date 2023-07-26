package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.tanyaofei.plugin.toolkit.database.Page;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class SpawnCommand extends AbstractCommand {

    public final static SpawnCommand instance = new SpawnCommand();

    public void create(@NotNull Player sender, CommandArguments args) {
        var fakePlayer = fakeplayerManager.spawn(sender, sender.getLocation());
        if (fakePlayer != null) {
            sender.sendMessage(textOfChildren(
                    text("你创建了假人 ", GRAY),
                    text(fakePlayer.getName())
            ));
        }
    }

    public void kill(@NotNull CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        @SuppressWarnings("unchecked")
        var targets = (List<Player>) Optional.ofNullable(args.get("targets")).orElse(Collections.emptyList());

        int count = 0;
        for (var target : targets) {
            if (fakeplayerManager.remove(target.getName())) {
                count++;
            }
        }
        sender.sendMessage(textOfChildren(
                text("你移除了 ", GRAY),
                text(count, WHITE),
                text(" 个假人", GRAY)
        ));
    }

    public void list(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        var page = (int) args.getOptional("page").orElse(1);
        var size = (int) args.getOptional("size").orElse(10);

        var fakers = sender.isOp()
                ? fakeplayerManager.getAll()
                : fakeplayerManager.getAll(sender);

        var total = fakers.size();
        var pages = total == 0 ? 1 : (int) Math.ceil((double) total / size);
        var p = new Page<>(
                fakers.subList((page - 1) * size, Math.min(total, page * size)),
                total,
                pages,
                page,
                size
        );

        sender.sendMessage(p.toComponent(
                "假人",
                record -> textOfChildren(
                        text(record.getName()),
                        text(" - "),
                        text(Optional.ofNullable(fakeplayerManager.getCreator(record)).orElse("<不在线>"))
                ),
                String.format("/fp list %d %d", page - 1, size),
                String.format("/fp list %d %d", page + 1, size)
        ));
    }
}
