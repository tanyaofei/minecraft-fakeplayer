package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractCommand {

    protected final FakeplayerManager fakeplayerManager = FakeplayerManager.instance;

    public static Argument<Player> targetArgument(@NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var name = info.currentInput();
            var target = sender.isOp()
                    ? FakeplayerManager.instance.get(name)
                    : FakeplayerManager.instance.get(sender, name);

            if (target == null) {
                var targets = FakeplayerManager.instance.getAll(sender);
                if (targets.size() != 1) {
                    throw CustomArgument.CustomArgumentException.fromString("你需要指定假人");
                }
                target = targets.get(0);
            }
            return target;
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            var sender = info.sender();
            var arg = info.currentArg();

            var fakes = sender.isOp()
                    ? FakeplayerManager.instance.getAll()
                    : FakeplayerManager.instance.getAll(sender);

            var names = fakes.stream().map(Player::getName);
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }

            return names.toArray(String[]::new);
        }));
    }

    public static Argument<List<Player>> multiTargetArgument(@NotNull String nodeName) {
        return new CustomArgument<List<Player>, String>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var name = info.currentInput();

            if (name.equals("--all")) {
                return sender.isOp()
                        ? FakeplayerManager.instance.getAll()
                        : FakeplayerManager.instance.getAll(sender);
            }

            var one = sender.isOp()
                    ? FakeplayerManager.instance.get(name)
                    : FakeplayerManager.instance.get(sender, name);

            if (one == null) {
                return Collections.emptyList();
            }

            return Collections.singletonList(one);
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            var sender = info.sender();
            var arg = info.currentArg().toLowerCase();

            var fakes = sender.isOp()
                    ? FakeplayerManager.instance.getAll()
                    : FakeplayerManager.instance.getAll(sender);

            var names = fakes.stream().map(Player::getName);
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }

            names = Stream.concat(Stream.of("--all"), names);
            return names.toArray(String[]::new);
        }));
    }

    protected @NotNull Player getTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        return Optional
                .ofNullable((Player) args.getUnchecked("target"))
                .or(() -> {
                    var all = FakeplayerManager.instance.getAll(sender);
                    if (all.size() != 1) {
                        return Optional.empty();
                    }
                    return Optional.of(all.get(0));
                })
                .orElseThrow(() -> CommandAPI.failWithString("你需要指定假人"));
    }

}
