package io.github.hello09x.fakeplayer.command.player.spawn;

import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import io.github.tanyaofei.plugin.toolkit.database.Page;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;

public class ListCommand extends ExecutableCommand {

    public final static ListCommand instance = new ListCommand(
            "查看所有假人",
            "/fp list [页码] [数量]",
            "fakeplayer.spawn"
    );
    private final static FakeplayerManager manager = FakeplayerManager.instance;

    public ListCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        int current = 1;
        if (args.length >= 1) {
            try {
                current = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (current < 1) {
            current = 1;
        }

        int size = 10;
        if (args.length >= 2) {
            try {
                size = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (size < 1) {
            size = 1;
        }

        var fakers = sender.isOp()
                ? manager.getAll()
                : manager.getAll(sender);

        var total = fakers.size();
        var pages = total == 0 ? 1 : (int) Math.ceil((double) total / size);
        if (current > pages) {
            current = pages;
        }

        var page = new Page<>(
                fakers.subList((current - 1) * size, Math.min(total, current * size)),
                total,
                pages,
                current,
                size
        );

        sender.sendMessage(page.toComponent(
                "假人",
                record -> textOfChildren(
                        text(record.getName()),
                        text(" - "),
                        text(Optional.ofNullable(manager.getCreator(record)).orElse("<不在线>"))
                ),
                String.format("/fp list %d %d", current - 1, size),
                String.format("/fp list %d %d", current + 1, size)
        ));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return Collections.emptyList();
    }
}
