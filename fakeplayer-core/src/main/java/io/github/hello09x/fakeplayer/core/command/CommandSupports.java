package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.command.impl.ActionCommand;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.github.hello09x.devtools.command.Commands.command;
import static io.github.hello09x.devtools.command.Commands.int32;
import static net.kyori.adventure.text.Component.translatable;

public abstract class CommandSupports {

    private final static FakeplayerManager manager = Main.getInjector().getInstance(FakeplayerManager.class);

    private final static FakeplayerConfig config = Main.getInjector().getInstance(FakeplayerConfig.class);

    private static final ActionCommand actionCommand = Main.getInjector().getInstance(ActionCommand.class);

    public static @NotNull CommandAPICommand[] newActionCommands(@NotNull ActionType action) {
        return new CommandAPICommand[]{
                command("once")
                        .withShortDescription("fakeplayer.command.action.once")
                        .withOptionalArguments(fakeplayer("name"))
                        .executes(actionCommand.action(action, ActionSetting.once())),
                command("continuous")
                        .withShortDescription("fakeplayer.command.action.continuous")
                        .withOptionalArguments(fakeplayer("name"))
                        .executes(actionCommand.action(action, ActionSetting.continuous())),
                command("interval")
                        .withShortDescription("fakeplayer.command.action.interval")
                        .withOptionalArguments(
                                int32("ticks", 1),
                                fakeplayer("name"))
                        .executes((sender, args) -> {
                    int interval = (int) args.getOptional("ticks").orElse(1);
                    actionCommand.action(sender, args, action, ActionSetting.interval(interval));
                }),
                command("stop")
                        .withShortDescription("fakeplayer.command.action.stop")
                        .withOptionalArguments(fakeplayer("name"))
                        .executes(actionCommand.action(action, ActionSetting.stop()))
        };
    }

    public static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName, @Nullable Predicate<Player> predicate) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var input = info.currentInput();
            Player target = null;
            if (input.startsWith("*")) {
                // 标签选择，返回第一个匹配的假人
                var tag = input.substring(1);
                var list = manager.getByTag(sender, tag);
                if (!list.isEmpty()) {
                    target = list.get(0).getPlayer();
                }
            } else {
                target = sender.isOp()
                        ? manager.get(input)
                        : manager.get(sender, input);
            }
            if (predicate != null && target != null && !predicate.test(target)) {
                target = null;
            }
            return target;
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            var sender = info.sender();
            var arg = info.currentArg();
            var targets = sender.isOp()
                    ? manager.getAll(predicate)
                    : manager.getAll(sender, predicate);
            var names = targets.stream().map(Player::getName);
            // 标签补全
            var tagNames = manager.getByTag(sender, arg.startsWith("*") ? arg.substring(1) : arg)
                    .stream().map(fp -> "*" + arg).distinct();
            names = Stream.concat(names, tagNames);
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }
            return names.toArray(String[]::new);
        }));
    }


    public static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName) {
        return fakeplayer(nodeName, null);
    }

    public static @NotNull Argument<List<Player>> fakeplayers(@NotNull String nodeName) {
        return new CustomArgument<List<Player>, String>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var arg = info.currentInput();
            if (arg.equals("-a")) {
                return manager.getAll(sender);
            }
            if (arg.startsWith("*")) {
                var tag = arg.substring(1);
                return manager.getByTag(sender, tag).stream().map(Fakeplayer::getPlayer).toList();
            }
            var target = sender.isOp()
                    ? manager.get(arg)
                    : manager.get(sender, arg);
            return target == null ? Collections.emptyList() : Collections.singletonList(target);
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            var sender = info.sender();
            var arg = info.currentArg().toLowerCase();
            var fakes = sender.isOp()
                    ? manager.getAll()
                    : manager.getAll(sender);
            var names = Stream.concat(fakes.stream().map(Player::getName), Stream.of("-a"));
            // 标签补全
            var tagNames = manager.getByTag(sender, arg.startsWith("*") ? arg.substring(1) : arg)
                    .stream().map(fp -> "*" + arg).distinct();
            names = Stream.concat(names, tagNames);
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }
            return names.toArray(String[]::new);
        }));
    }

    public static @NotNull Argument<Feature> configKey(@NotNull String nodeName) {
        return configKey(nodeName, ignored -> true);
    }

    public static @NotNull Argument<Feature> configKey(@NotNull String nodeName, @NotNull Predicate<Feature> predicate) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var arg = info.currentInput();
            Feature key;
            try {
                key = Feature.valueOf(arg);
            } catch (Exception e) {
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(translatable("fakeplayer.command.config.set.error.invalid-key"));
            }

            if (!predicate.test(key)) {
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(translatable("fakeplayer.command.config.set.error.invalid-key"));
            }

            if (!key.testPermissions(info.sender())) {
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(translatable("fakeplayer.command.config.set.error.no-permission"));
            }
            return key;
        }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Feature.values()).filter(predicate).map(Enum::name).toArray(String[]::new)));
    }


    public static @NotNull Argument<String> configValue(@NotNull String configKeyNodeName, @NotNull String nodeName) {
        return new CustomArgument<String, String>(new StringArgument(nodeName), info -> {
            var key = (Feature) info.previousArgs().get(configKeyNodeName);
            if (key == null) {
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(translatable("fakeplayer.command.config.set.error.invalid-key"));
            }
            var arg = info.currentInput();
            if (!key.getOptions().contains(arg)) {
                throw CustomArgument.CustomArgumentException.fromAdventureComponent(translatable("fakeplayer.command.config.set.error.invalid-value"));
            }

            return arg;
        }).replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> {
            var key = (Feature) info.previousArgs().get(configKeyNodeName);
            if (key == null) {
                return Collections.emptyList();
            }

            var arg = info.currentArg().toLowerCase(Locale.ENGLISH);
            if (arg.isEmpty()) {
                return key.getOptions();
            }

            return key.getOptions().stream().filter(option -> option.contains(arg)).toList();
        })));
    }

    public static boolean hasFakeplayerForRespawn(@NotNull CommandSender sender) {
        if (config.isKickOnDead()) {
            return false;
        }
        return hasFakeplayer(sender);
    }

    public static boolean needSelect(@NotNull CommandSender sender) {
        return sender.isOp() || (config.getPlayerLimit() > 1 && manager.countByCreator(sender) > 0);
    }

    public static boolean hasFakeplayer(@NotNull CommandSender sender) {
        return sender.isOp() || manager.countByCreator(sender) > 0;
    }

    public static boolean isCmdAvailable(@NotNull CommandSender sender) {
        return (sender.hasPermission(Permission.cmd) || !config.getAllowCommands().isEmpty()) && hasFakeplayer(sender);
    }

}
