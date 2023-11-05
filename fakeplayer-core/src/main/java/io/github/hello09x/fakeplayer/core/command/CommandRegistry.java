package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.bedrock.command.Usage;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import io.github.hello09x.fakeplayer.core.repository.model.Configs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.github.hello09x.bedrock.command.Commands.*;


@SuppressWarnings("SameParameterValue")
public class CommandRegistry {

    public static void register() {
        command("fakeplayer")
                .withAliases("fp")
                .withHelp(
                        I18n.asString("fakeplayer.command.fp.short-description"),
                          I18n.asString("fakeplayer.command.fp.full-description")
                )
                .withUsage(
                        "fp ? to get more usage",
                        "hello09x [汤姆]"
                )
                .withSubcommands(
                        helpCommand("/fp",
                                Usage.of("spawn", I18n.asString("fakeplayer.command.spawn.description"), Permission.spawn),
                                Usage.of("kill", I18n.asString("fakeplayer.command.kill.description"), Permission.spawn),
                                Usage.of("list", I18n.asString("fakeplayer.command.list.description"), Permission.spawn),
                                Usage.of("distance", I18n.asString("fakeplayer.command.distance.description"), Permission.spawn),
                                Usage.of("drop", I18n.asString("fakeplayer.command.drop.description"), Permission.spawn),
                                Usage.of("dropinv", I18n.asString("fakeplayer.command.dropinv.description"), Permission.spawn),
                                Usage.of("tp", I18n.asString("fakeplayer.command.tp.description"), Permission.tp),
                                Usage.of("tphere", I18n.asString("fakeplayer.command.tphere.description"), Permission.tp),
                                Usage.of("tps", I18n.asString("fakeplayer.command.tps.description"), Permission.tp),
                                Usage.of("config get", I18n.asString("fakeplayer.command.config.get.description")),
                                Usage.of("config set", I18n.asString("fakeplayer.command.config.set.description")),
                                Usage.of("health", I18n.asString("fakeplayer.command.health.description"), Permission.profile),
                                Usage.of("exp", I18n.asString("fakeplayer.command.exp.description"), Permission.profile),
                                Usage.of("expme", I18n.asString("fakeplayer.command.expme.description"), Permission.exp),
                                Usage.of("attack", I18n.asString("fakeplayer.command.attack.description"), Permission.action),
                                Usage.of("mine", I18n.asString("fakeplayer.command.mine.description"), Permission.action),
                                Usage.of("use", I18n.asString("fakeplayer.command.use.description"), Permission.action),
                                Usage.of("jump", I18n.asString("fakeplayer.command.jump.description"), Permission.action),
                                Usage.of("look", I18n.asString("fakeplayer.command.look.description"), Permission.action),
                                Usage.of("turn", I18n.asString("fakeplayer.command.turn.description"), Permission.action),
                                Usage.of("move", I18n.asString("fakeplayer.command.move.description"), Permission.action),
                                Usage.of("ride", I18n.asString("fakeplayer.command.ride.description"), Permission.action),
                                Usage.of("sneak", I18n.asString("fakeplayer.command.sneak.description"), Permission.action),
                                Usage.of("swap", I18n.asString("fakeplayer.command.swap.description"), Permission.spawn),
                                Usage.of("cmd", I18n.asString("fakeplayer.command.cmd.description"), Permission.cmd),
                                Usage.of("reload", I18n.asString("fakeplayer.command.reload.description"), Permission.admin)
                        ),

                        command("spawn")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        text("name").withPermission(Permission.spawnName),
                                        world("world").withPermission(Permission.spawnLocation),
                                        location("location").withPermission(Permission.spawnLocation))
                                .executes(SpawnCommand.instance::spawn),
                        command("kill")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(fakeplayers("targets"))
                                .executes(SpawnCommand.instance::kill),
                        command("list")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        int32("page", 1),
                                        int32("size", 1))
                                .executes(SpawnCommand.instance::list),
                        command("distance")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(SpawnCommand.instance::distance),
                        command("drop")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        fakeplayer("target"),
                                        literals("all", List.of("-a", "--all")))
                                .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(
                                        sender,
                                        args,
                                        args.getOptional("all").isPresent() ? ActionType.DROP_STACK : ActionType.DROP_ITEM,
                                        ActionSetting.once())),
                        command("dropinv")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ActionCommand.instance.action(ActionType.DROP_INVENTORY, ActionSetting.once())),

                        command("exp")
                                .withPermission(Permission.profile)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ProfileCommand.instance::exp),
                        command("health")
                                .withPermission(Permission.profile)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ProfileCommand.instance::health),

                        command("tp")
                                .withPermission(Permission.tp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(TpCommand.instance::tp),
                        command("tphere")
                                .withPermission(Permission.tp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(TpCommand.instance::tphere),
                        command("tps")
                                .withPermission(Permission.tp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(TpCommand.instance::tps),

                        command("config")
                                .withSubcommands(
                                        command("get")
                                                .withArguments(config("config"))
                                                .executesPlayer(ConfigCommand.instance::getConfig),
                                        command("set")
                                                .withArguments(
                                                        config("config"),
                                                        configValue("config", "value"))
                                                .executesPlayer(ConfigCommand.instance::setConfig)
                                ),

                        command("attack")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(ActionType.ATTACK)),
                        command("mine")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(ActionType.MINE)),
                        command("use")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(ActionType.USE)),
                        command("jump")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(ActionType.JUMP)),
                        command("sneak")
                                .withPermission(Permission.action)
                                .withOptionalArguments(
                                        literals("sneaking", List.of("true", "false")),
                                        fakeplayer("target"))
                                .executes(ActionCommand.instance::sneak),
                        command("look")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        command("north")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.NORTH)),
                                        command("south")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.SOUTH)),
                                        command("west")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.WEST)),
                                        command("east")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.EAST)),
                                        command("up")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.UP)),
                                        command("down")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.DOWN)),
                                        command("at")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance::lookAt),
                                        command("entity")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .withSubcommands(newActionCommands(ActionType.LOOK_AT_NEAREST_ENTITY)),
                                        helpCommand(
                                                "/fp look",
                                                Usage.of("north", I18n.asString("fakeplayer.command.look.north.description")),
                                                Usage.of("south", I18n.asString("fakeplayer.command.look.south.description")),
                                                Usage.of("west", I18n.asString("fakeplayer.command.look.west.description")),
                                                Usage.of("east", I18n.asString("fakeplayer.command.look.east.description")),
                                                Usage.of("up", I18n.asString("fakeplayer.command.look.up.description")),
                                                Usage.of("down", I18n.asString("fakeplayer.command.look.down.description")),
                                                Usage.of("at", I18n.asString("fakeplayer.command.look.at.description")),
                                                Usage.of("entity (once | continuous | interval | stop)", I18n.asString("fakeplayer.command.look.entity.description"))
                                        )
                                ),
                        command("turn")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        command("left")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(-90, 0)),
                                        command("right")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(90, 0)),
                                        command("back")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(180, 0)),
                                        command("to")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance::turnTo),
                                        helpCommand(
                                                "/fp turn",
                                                Usage.of("left", I18n.asString("fakeplayer.command.turn.left.description")),
                                                Usage.of("right",  I18n.asString("fakeplayer.command.turn.right.description")),
                                                Usage.of("back",  I18n.asString("fakeplayer.command.turn.back.description")),
                                                Usage.of("to",  I18n.asString("fakeplayer.command.turn.to.description"))
                                        )
                                ),
                        command("move")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        command("forward")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(1, 0)),
                                        command("backward")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(-1, 0)),
                                        command("left")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(0, 1)),
                                        command("right")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(0, -1)),
                                        helpCommand(
                                                "/fp move",
                                                Usage.of("forward",  I18n.asString("fakeplayer.command.move.forward.description")),
                                                Usage.of("backward", I18n.asString("fakeplayer.command.move.backward.description")),
                                                Usage.of("left", I18n.asString("fakeplayer.command.move.left.description")),
                                                Usage.of("right", I18n.asString("fakeplayer.command.move.right.description"))
                                        )
                                ),

                        command("ride")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        command("me")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executesPlayer(RideCommand.instance::rideMe),
                                        command("target")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(RideCommand.instance::rideTarget),
                                        command("anything")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(RideCommand.instance::rideAnything),
                                        command("normal")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(RideCommand.instance::rideNormal),
                                        command("stop")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(RideCommand.instance::stopRiding),
                                        helpCommand(
                                                "/fp ride",
                                                Usage.of("me", I18n.asString("fakeplayer.command.ride.me.description")),
                                                Usage.of("target", I18n.asString("fakeplayer.command.ride.target.description")),
                                                Usage.of("anything", I18n.asString("fakeplayer.command.ride.anything.description")),
                                                Usage.of("normal", I18n.asString("fakeplayer.command.ride.normal.description")),
                                                Usage.of("stop", I18n.asString("fakeplayer.command.ride.stop.description"))
                                        )
                                ),
                        command("swap")
                                .withPermission(Permission.action)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ActionCommand.instance::swap),

                        command("expme")
                                .withPermission(Permission.exp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(ExpCommand.instance::expme),

                        command("cmd")
                                .withRequirement(sender -> sender.hasPermission(Permission.cmd) || !FakeplayerConfig.instance.getAllowCommands().isEmpty())
                                .withArguments(
                                        fakeplayer("target"),
                                        cmd("command")
                                )
                                .executes(CmdCommand.instance::cmd),

                        command("reload")
                                .withPermission(Permission.admin)
                                .executes(ReloadCommand.instance::reload)

                ).register();
    }

    private static CommandAPICommand[] newActionCommands(@NotNull ActionType action) {
        return new CommandAPICommand[]{
                command("once")
                        .withOptionalArguments(fakeplayer("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.once())),
                command("continuous")
                        .withOptionalArguments(fakeplayer("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.continuous())),
                command("stop")
                        .withOptionalArguments(fakeplayer("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.stop())),
                command("interval")
                        .withOptionalArguments(
                                int32("interval", 1),
                                fakeplayer("target"))
                        .executes((sender, args) -> {
                    int interval = (int) args.getOptional("interval").orElse(1);
                    ActionCommand.instance.action(sender, args, action, ActionSetting.interval(interval));
                })
        };
    }


    private static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            return sender.isOp()
                    ? FakeplayerManager.instance.get(info.currentInput())
                    : FakeplayerManager.instance.get(sender, info.currentInput());
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

    private static @NotNull Argument<List<Player>> fakeplayers(@NotNull String nodeName) {
        return new CustomArgument<List<Player>, String>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var arg = info.currentInput();

            if (arg.equals("--all") || arg.equals("-a")) {
                return sender.isOp()
                        ? FakeplayerManager.instance.getAll()
                        : FakeplayerManager.instance.getAll(sender);
            }

            var target = sender.isOp()
                    ? FakeplayerManager.instance.get(arg)
                    : FakeplayerManager.instance.get(sender, arg);

            return target == null ? Collections.emptyList() : Collections.singletonList(target);
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            var sender = info.sender();
            var arg = info.currentArg().toLowerCase();

            var fakes = sender.isOp()
                    ? FakeplayerManager.instance.getAll()
                    : FakeplayerManager.instance.getAll(sender);

            var names = Stream.concat(fakes.stream().map(Player::getName), Stream.of("-a", "--all"));
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }

            return names.toArray(String[]::new);
        }));
    }

    private static @NotNull Argument<Config<Object>> config(@NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var arg = info.currentInput();
            try {
                return Configs.valueOf(arg);
            } catch (Exception e) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("未知的配置项: ").appendArgInput());
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Configs.values()).map(Config::name).toList()));
    }

    private static @NotNull Argument<Object> configValue(@NotNull String configNodeName, @NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            @SuppressWarnings("unchecked")
            var config = Objects.requireNonNull((Config<Object>) info.previousArgs().get(configNodeName));
            var arg = info.currentInput();
            if (!config.options().contains(arg)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("未知的配置值: ").appendArgInput());
            }
            return config.mapper().apply(arg);
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            var config = Objects.requireNonNull((Config<?>) info.previousArgs().get(configNodeName));
            var arg = info.currentArg().toLowerCase();
            var options = config.options().stream();
            if (!arg.isEmpty()) {
                options = options.filter(o -> o.toLowerCase().contains(arg));
            }
            return options.toArray(String[]::new);
        }));
    }


}
