package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.command.impl.ActionCommand;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.github.hello09x.bedrock.command.Commands.command;
import static io.github.hello09x.bedrock.command.Commands.int32;

public abstract class CommandSupports {

    private final static FakeplayerManager manager = FakeplayerManager.instance;

    private final static FakeplayerConfig config = FakeplayerConfig.instance;

    private final static I18n i18n = Main.getI18n();

    public static @NotNull CommandAPICommand[] newActionCommands(@NotNull ActionType action) {
        return new CommandAPICommand[]{
                command("once")
                        .withOptionalArguments(target("name"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.once())),
                command("continuous")
                        .withOptionalArguments(target("name"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.continuous())),
                command("stop")
                        .withOptionalArguments(target("name"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.stop())),
                command("interval")
                        .withOptionalArguments(
                                int32("interval", 1),
                                target("name"))
                        .executes((sender, args) -> {
                    int interval = (int) args.getOptional("interval").orElse(1);
                    ActionCommand.instance.action(sender, args, action, ActionSetting.interval(interval));
                })
        };
    }

    public static @NotNull Argument<Player> target(@NotNull String nodeName, @Nullable Predicate<Player> predicate) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var target = sender.isOp()
                    ? manager.get(info.currentInput())
                    : manager.get(sender, info.currentInput());
            if (predicate != null && target != null && !predicate.test(target)) {
                target = null;
            }
            return target;
        }).replaceSuggestions(ArgumentSuggestions.stringsAsync(info -> CompletableFuture.supplyAsync(() -> {
            var sender = info.sender();
            var arg = info.currentArg();

            var targets = sender.isOp()
                    ? manager.getAll(predicate)
                    : manager.getAll(sender, predicate);

            var names = targets.stream().map(Player::getName);
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }

            return names.toArray(String[]::new);
        })));
    }


    public static @NotNull Argument<Player> target(@NotNull String nodeName) {
        return target(nodeName, null);
    }

    public static @NotNull Argument<List<Player>> targets(@NotNull String nodeName) {
        return new CustomArgument<List<Player>, String>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var arg = info.currentInput();

            if (arg.equals("-a")) {
                return manager.getAll(sender);
            }

            var target = sender.isOp()
                    ? manager.get(arg)
                    : manager.get(sender, arg);

            return target == null ? Collections.emptyList() : Collections.singletonList(target);
        }).replaceSuggestions(ArgumentSuggestions.stringsAsync(info -> CompletableFuture.supplyAsync(() -> {
            var sender = info.sender();
            var arg = info.currentArg().toLowerCase();

            var fakes = sender.isOp()
                    ? manager.getAll()
                    : manager.getAll(sender);

            var names = Stream.concat(fakes.stream().map(Player::getName), Stream.of("-a"));
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }

            return names.toArray(String[]::new);
        })));
    }

    public static @NotNull Argument<Config<Object>> config(@NotNull String nodeName) {
        return config(nodeName, null);
    }

    public static @NotNull Argument<Config<Object>> config(@NotNull String nodeName, @Nullable Predicate<Config<Object>> predicate) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var arg = info.currentInput();
            Config<Object> config;
            try {
                config = Config.valueOf(arg);
            } catch (Exception e) {
                throw CustomArgument.CustomArgumentException.fromString(i18n.asString("fakeplayer.command.config.set.error.invalid-option"));
            }
            if (predicate != null && !predicate.test(config)) {
                throw CustomArgument.CustomArgumentException.fromString(i18n.asString("fakeplayer.command.config.set.error.invalid-option"));
            }
            return config;
        }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Config.values()).map(Config::key).toList()));
    }

    public static @NotNull Argument<Object> configValue(@NotNull String configNodeName, @NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            @SuppressWarnings("unchecked")
            var config = Objects.requireNonNull((Config<Object>) info.previousArgs().get(configNodeName));
            var arg = info.currentInput();
            if (!config.options().contains(arg)) {
                throw CustomArgument.CustomArgumentException.fromString(i18n.asString("fakeplayer.command.config.set.error.invalid-value"));
            }
            return config.converter().apply(arg);
        }).replaceSuggestions(ArgumentSuggestions.stringsAsync(info -> CompletableFuture.supplyAsync(() -> {
            var config = Objects.requireNonNull((Config<?>) info.previousArgs().get(configNodeName));
            var arg = info.currentArg().toLowerCase();
            var options = config.options().stream();
            if (!arg.isEmpty()) {
                options = options.filter(o -> o.toLowerCase().contains(arg));
            }
            return options.toArray(String[]::new);
        })));
    }

    public static boolean hasDeadTarget(@NotNull CommandSender sender) {
        if (config.isKickOnDead()) {
            return false;
        }
        return hasTarget(sender);
    }

    public static boolean needSelect(@NotNull CommandSender sender) {
        return sender.isOp() || (config.getPlayerLimit() > 1 && manager.countByCreator(sender) > 0);
    }

    public static boolean hasTarget(@NotNull CommandSender sender) {
        return sender.isOp() || manager.countByCreator(sender) > 0;
    }

}
