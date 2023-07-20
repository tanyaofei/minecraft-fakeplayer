package io.github.hello09x.fakeplayer.command.player.spawn;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class RemoveCommand extends AbstractCommand {

    public final static RemoveCommand instance = new RemoveCommand(
            "移除假人",
            "/fp remove [名称]",
            "fakeplayer.spawn"
    );

    private final FakePlayerManager manager = FakePlayerManager.instance;

    public RemoveCommand(
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
        if (args.length != 1) {
            return false;
        }

        var name = args[0];
        if (name.equals("@all") || name.equals("@a")) {
            int removed = manager.removeAll();
            sender.sendMessage(text(String.format("已移除 %d 个假人", removed), GRAY));
            return true;
        }

        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }
        manager.remove(target.getName());
        sender.sendMessage(text("移除假人成功", GRAY));
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
        if (sender.isOp() && suggestion != null) {
            suggestion.add(0, "@all");
        }
        return suggestion;
    }
}
