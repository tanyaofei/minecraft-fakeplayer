package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractCommand extends ExecutableCommand {

    private final FakePlayerManager manager = FakePlayerManager.instance;

    public AbstractCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    public @Nullable Player getTarget(
            @NotNull CommandSender sender,
            @NotNull String[] args
    ) {
        if (args.length != 0) {
            return sender.isOp()
                    ? manager.get(args[0])
                    : manager.get(sender, args[0]);
        }

        if (!(sender instanceof Player p)) {
            return null;
        }

        var lookAt = p.getTargetEntity(5);
        if (!(lookAt instanceof Player target) || !manager.isFake(target)) {
            var creations = manager.getAll(sender);
            if (creations.size() == 1) {
                return creations.get(0);
            }
            return null;
        }

        return p.isOp() || Objects.equals(p.getName(), manager.getCreator(target))
                ? target
                : null;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 1) {
            return new ArrayList<>();
        }

        var fakers = sender.isOp()
                ? manager.getAll()
                : manager.getAll(sender);

        if (fakers.isEmpty()) {
            return new ArrayList<>();
        }

        var input = args[0];
        var names = fakers.stream().map(Player::getName);
        if (!input.isEmpty()) {
            names = names.filter(name -> name.toLowerCase().contains(input));
        }

        return names.collect(Collectors.toList());
    }
}
