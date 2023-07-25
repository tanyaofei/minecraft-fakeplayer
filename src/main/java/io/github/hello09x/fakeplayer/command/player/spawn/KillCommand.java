package io.github.hello09x.fakeplayer.command.player.spawn;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class KillCommand extends AbstractCommand {

    public final static KillCommand instance = new KillCommand(
            "移除假人",
            "/fp kill [假人] [-a|-all]",
            "fakeplayer.spawn"
    );

    private final FakeplayerManager manager = FakeplayerManager.instance;

    public KillCommand(
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
        if (hasFlag(args, "-a") || hasFlag(args, "--all")) {
            int removed = sender.isOp()
                    ? manager.removeAll()
                    : manager.removeAll(sender);
            sender.sendMessage(text(String.format("已移除 %d 个假人", removed), GRAY));
            return true;
        }

        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }
        manager.remove(target.getName());
        sender.sendMessage(textOfChildren(
                text("你移除了假人 ", GRAY),
                text(target.getName(), WHITE)
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
        var suggestion = super.onTabComplete(
                sender,
                command,
                label,
                args
        );
        if (args.length == 1 && suggestion != null) {
            suggestion.add(0, "--all");
        }
        return suggestion;
    }
}
