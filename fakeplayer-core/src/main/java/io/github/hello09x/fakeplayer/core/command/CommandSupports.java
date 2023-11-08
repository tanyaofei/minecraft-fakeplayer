package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.github.hello09x.bedrock.command.Commands.command;
import static io.github.hello09x.bedrock.command.Commands.int32;

public abstract class CommandSupports {

    private final static FakeplayerManager manager = FakeplayerManager.instance;

    private final static FakeplayerConfig config = FakeplayerConfig.instance;

    private final static I18n i18n = Main.i18n();

    public static @NotNull CommandAPICommand[] newActionCommands(@NotNull ActionType action) {
        return new CommandAPICommand[]{
                command("once")
                        .withOptionalArguments(fakeplayer("name"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.once())),
                command("continuous")
                        .withOptionalArguments(fakeplayer("name"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.continuous())),
                command("stop")
                        .withOptionalArguments(fakeplayer("name"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.stop())),
                command("interval")
                        .withOptionalArguments(
                                int32("interval", 1),
                                fakeplayer("name"))
                        .executes((sender, args) -> {
                    int interval = (int) args.getOptional("interval").orElse(1);
                    ActionCommand.instance.action(sender, args, action, ActionSetting.interval(interval));
                })
        };
    }

    public static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName, @Nullable Predicate<Player> predicate) {
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


    public static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName) {
        return fakeplayer(nodeName, null);
    }

    public static @NotNull Argument<List<Player>> fakeplayers(@NotNull String nodeName) {
        return new CustomArgument<List<Player>, String>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var arg = info.currentInput();

            if (arg.equals("-a")) {
                return sender.isOp()
                        ? manager.getAll()
                        : manager.getAll(sender);
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
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var arg = info.currentInput();
            try {
                return Config.valueOf(arg);
            } catch (Exception e) {
                throw CustomArgument.CustomArgumentException.fromString(i18n.asString("fakeplayer.command.config.set.error.invalid-option"));
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Config.values()).map(Config::name).toList()));
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

    public static boolean respawnRequirement(@NotNull CommandSender sender) {
        if (config.isKickOnDead()) {
            return false;
        }
        return sender.isOp() || manager.countByCreator(sender) > 0;
    }

    public static boolean selectRequirement(@NotNull CommandSender sender) {
        return sender.isOp() || (config.getPlayerLimit() > 1 && manager.countByCreator(sender) > 0);
    }

    public static boolean targetRequirement(@NotNull CommandSender sender) {
        return sender.isOp() || manager.countByCreator(sender) > 0;
    }

}
