package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCommand extends ExecutableCommand {

    private final FakeplayerManager manager = FakeplayerManager.instance;

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
            var name = Arrays.stream(args).filter(arg -> !arg.startsWith("-")).findFirst().orElse(null);
            if (name != null) {
                return sender.isOp()
                        ? manager.get(args[0])
                        : manager.get(sender, args[0]);
            }
        }

        if (!(sender instanceof Player p)) {
            return null;
        }

        var lookAt = p.getTargetEntity(32);
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


    protected @Nullable String getArgs(@NotNull String[] args, @NotNull String name) {
        for (var arg : args) {
            if (!arg.startsWith(name + ":")) {
                continue;
            }

            return arg.split(":")[1];
        }
        return null;
    }

    protected @NotNull String getArgs(@NotNull String[] args, @NotNull String name, @NotNull String defaultValue) {
        return Optional.ofNullable(getArgs(args, name)).orElse(defaultValue);
    }

    protected boolean hasFlag(@NotNull String[] args, @NotNull String flag) {
        return Arrays.stream(args).anyMatch(i -> i.contains(flag));
    }

}
