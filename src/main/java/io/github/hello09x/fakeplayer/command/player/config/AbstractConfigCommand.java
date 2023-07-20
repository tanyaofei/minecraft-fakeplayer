package io.github.hello09x.fakeplayer.command.player.config;

import io.github.hello09x.fakeplayer.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.repository.model.Config;
import io.github.hello09x.fakeplayer.repository.model.Configs;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractConfigCommand extends ExecutableCommand {


    protected final UserConfigRepository repository = UserConfigRepository.instance;

    public AbstractConfigCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    public @Nullable Config<Object> getConfig(
            @NotNull Player sender,
            @NotNull String[] args
    ) {
        if (args.length < 1) {
            return null;
        }

        try {
            return Configs.valueOf(args[0]);
        } catch (IllegalArgumentException e) {
            return null;
        }
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

        var prefix = args[0].toLowerCase();
        var options = Arrays.stream(Configs.values()).map(Config::name);
        if (!prefix.isEmpty()) {
            options = options.filter(name -> name.toLowerCase().contains(prefix));
        }
        return options.collect(Collectors.toList());
    }
}
