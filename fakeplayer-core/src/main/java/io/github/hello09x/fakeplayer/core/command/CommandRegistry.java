package io.github.hello09x.fakeplayer.core.command;

import com.google.common.collect.Iterables;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import io.github.hello09x.bedrock.command.Usage;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.github.hello09x.bedrock.command.Commands.*;


@SuppressWarnings("SameParameterValue")
public class CommandRegistry {

    private final static I18n i18n = Main.i18n();

    public static void register() {
        command("fakeplayer")
                .withAliases("fp")
                .withHelp(
                        i18n.asString("fakeplayer.command.fp.short-description"),
                        i18n.asString("fakeplayer.command.fp.full-description")
                )
                .withUsage(
                        "type fp ? for more information",
                        "hello09x [汤姆]"
                )
                .withPermission(Permission.spawn)
                .withSubcommands(
                        helpCommand("/fp",
                                Usage.of("select", i18n.asString("fakeplayer.command.select.description"), null, CommandRegistry::selectRequirement),
                                Usage.of("selection", i18n.asString("fakeplayer.command.selection.description"), null, CommandRegistry::selectRequirement),
                                Usage.of("spawn", i18n.asString("fakeplayer.command.spawn.description"), Permission.spawn),
                                Usage.of("kill", i18n.asString("fakeplayer.command.kill.description"), Permission.kill),
                                Usage.of("list", i18n.asString("fakeplayer.command.list.description"), Permission.list),
                                Usage.of("distance", i18n.asString("fakeplayer.command.distance.description"), Permission.distance),
                                Usage.of("drop", i18n.asString("fakeplayer.command.drop.description"), Permission.drop),
                                Usage.of("dropstack", i18n.asString("fakeplayer.command.dropstack.description"), Permission.dropstack),
                                Usage.of("dropinv", i18n.asString("fakeplayer.command.dropinv.description"), Permission.dropinv),
                                Usage.of("skin", i18n.asString("fakeplayer.command.skin.description"), Permission.skin),
                                Usage.of("invsee", i18n.asString("fakeplayer.command.invsee.description"), Permission.invsee),
                                Usage.of("sleep", i18n.asString("fakeplayer.command.sleep.description"), Permission.sleep),
                                Usage.of("wakeup", i18n.asString("fakeplayer.command.wakeup.description"), Permission.wakeup),
                                Usage.of("health", i18n.asString("fakeplayer.command.health.description"), Permission.health),
                                Usage.of("exp", i18n.asString("fakeplayer.command.exp.description"), Permission.exp),
                                Usage.of("respawn", i18n.asString("fakeplayer.command.respawn.description"), Permission.respawn, CommandRegistry::respawnRequirement),
                                Usage.of("tp", i18n.asString("fakeplayer.command.tp.description"), Permission.tp),
                                Usage.of("tphere", i18n.asString("fakeplayer.command.tphere.description"), Permission.tphere),
                                Usage.of("tps", i18n.asString("fakeplayer.command.tps.description"), Permission.tps),
                                Usage.of("config", i18n.asString("fakeplayer.command.config.description"), Permission.config),
                                Usage.of("expme", i18n.asString("fakeplayer.command.expme.description"), Permission.exp),
                                Usage.of("attack", i18n.asString("fakeplayer.command.attack.description"), Permission.attack),
                                Usage.of("mine", i18n.asString("fakeplayer.command.mine.description"), Permission.mine),
                                Usage.of("use", i18n.asString("fakeplayer.command.use.description"), Permission.use),
                                Usage.of("refill", i18n.asString("fakeplayer.command.refill.description"), Permission.refill),
                                Usage.of("jump", i18n.asString("fakeplayer.command.jump.description"), Permission.jump),
                                Usage.of("look", i18n.asString("fakeplayer.command.look.description"), Permission.look),
                                Usage.of("turn", i18n.asString("fakeplayer.command.turn.description"), Permission.turn),
                                Usage.of("move", i18n.asString("fakeplayer.command.move.description"), Permission.move),
                                Usage.of("ride", i18n.asString("fakeplayer.command.ride.description"), Permission.ride),
                                Usage.of("sneak", i18n.asString("fakeplayer.command.sneak.description"), Permission.sneak),
                                Usage.of("swap", i18n.asString("fakeplayer.command.swap.description"), Permission.swap),
                                Usage.of("cmd", i18n.asString("fakeplayer.command.cmd.description"), Permission.cmd),
                                Usage.of("reload", i18n.asString("fakeplayer.command.reload.description"), "OP")
                        ),

                        command("select")
                                .withRequirement(CommandRegistry::selectRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(SelectCommand.instance::select),
                        command("selection")
                                .withRequirement(CommandRegistry::selectRequirement)
                                .executesPlayer(SelectCommand.instance::selection),

                        command("spawn")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        text("name").withPermission(Permission.spawnName),
                                        world("world").withPermission(Permission.spawnLocation),
                                        location("location").withPermission(Permission.spawnLocation))
                                .executes(SpawnCommand.instance::spawn),
                        command("kill")
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withPermission(Permission.kill)
                                .withOptionalArguments(fakeplayers("names"))
                                .executes(KillCommand.instance::kill),
                        command("list")
                                .withPermission(Permission.list)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(
                                        int32("page", 1),
                                        int32("size", 1))
                                .executes(ListCommand.instance::list),
                        command("distance")
                                .withPermission(Permission.distance)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(DistanceCommand.instance::distance),
                        command("drop")
                                .withPermission(Permission.drop)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(DropCommand.instance.drop()),
                        command("dropstack")
                                .withPermission(Permission.dropstack)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(DropCommand.instance.dropstack()),
                        command("dropinv")
                                .withPermission(Permission.dropinv)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(DropCommand.instance.dropinv()),
                        command("skin")
                                .withPermission(Permission.skin)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withArguments(offlinePlayer("player"))
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(SkinCommand.instance::skin),
                        command("invsee")
                                .withPermission(Permission.invsee)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(InvseeCommand.instance::invsee),
                        command("health")
                                .withPermission(Permission.health)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(HealthCommand.instance::health),
                        command("respawn")
                                .withRequirement(CommandRegistry::respawnRequirement)
                                .withPermission(Permission.respawn)
                                .withOptionalArguments(fakeplayer("name", Entity::isDead))
                                .executes(RespawnCommand.instance::respawn),
                        command("config")
                                .withPermission(Permission.config)
                                .withSubcommands(
                                        command("get")
                                                .withArguments(config("option"))
                                                .executesPlayer(ConfigCommand.instance::getConfig),
                                        command("set")
                                                .withArguments(
                                                        config("option"),
                                                        configValue("option", "value"))
                                                .executesPlayer(ConfigCommand.instance::setConfig),
                                        command("list")
                                                .executesPlayer(ConfigCommand.instance::listConfig)
                                ),

                        command("exp")
                                .withPermission(Permission.exp)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(ExpCommand.instance::exp),
                        command("expme")
                                .withPermission(Permission.expme)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(ExpCommand.instance::expme),

                        command("tp")
                                .withPermission(Permission.tp)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(TpCommand.instance::tp),
                        command("tphere")
                                .withPermission(Permission.tphere)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(TpCommand.instance::tphere),
                        command("tps")
                                .withPermission(Permission.tps)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(TpCommand.instance::tps),

                        command("attack")
                                .withPermission(Permission.attack)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withSubcommands(newActionCommands(ActionType.ATTACK)),
                        command("mine")
                                .withPermission(Permission.mine)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withSubcommands(newActionCommands(ActionType.MINE)),
                        command("use")
                                .withPermission(Permission.use)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withSubcommands(newActionCommands(ActionType.USE)),
                        command("refill")
                                .withPermission(RefillCommand.PERMISSION)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(
                                        literals("enabled", List.of("true", "false")),
                                        fakeplayer("name")
                                )
                                .executes(RefillCommand.instance::refill),
                        command("jump")
                                .withPermission(Permission.jump)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withSubcommands(newActionCommands(ActionType.JUMP)),
                        command("sneak")
                                .withPermission(Permission.sneak)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(
                                        literals("sneaking", List.of("true", "false")),
                                        fakeplayer("name"))
                                .executes(SneakCommand.instance::sneak),
                        command("look")
                                .withPermission(Permission.look)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withSubcommands(
                                        command("north")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.look(Direction.NORTH)),
                                        command("south")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.look(Direction.SOUTH)),
                                        command("west")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.look(Direction.WEST)),
                                        command("east")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.look(Direction.EAST)),
                                        command("up")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.look(Direction.UP)),
                                        command("down")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.look(Direction.DOWN)),
                                        command("at")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance::lookAt),
                                        command("entity")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .withSubcommands(newActionCommands(ActionType.LOOK_AT_NEAREST_ENTITY)),
                                        helpCommand(
                                                "/fp look",
                                                Usage.of("north", i18n.asString("fakeplayer.command.look.north.description")),
                                                Usage.of("south", i18n.asString("fakeplayer.command.look.south.description")),
                                                Usage.of("west", i18n.asString("fakeplayer.command.look.west.description")),
                                                Usage.of("east", i18n.asString("fakeplayer.command.look.east.description")),
                                                Usage.of("up", i18n.asString("fakeplayer.command.look.up.description")),
                                                Usage.of("down", i18n.asString("fakeplayer.command.look.down.description")),
                                                Usage.of("at", i18n.asString("fakeplayer.command.look.at.description")),
                                                Usage.of("entity (once | continuous | interval | stop)", i18n.asString("fakeplayer.command.look.entity.description"))
                                        )
                                ),
                        command("turn")
                                .withPermission(Permission.turn)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withSubcommands(
                                        command("left")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.turn(-90, 0)),
                                        command("right")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.turn(90, 0)),
                                        command("back")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance.turn(180, 0)),
                                        command("to")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RotationCommand.instance::turnTo),
                                        helpCommand(
                                                "/fp turn",
                                                Usage.of("left", i18n.asString("fakeplayer.command.turn.left.description")),
                                                Usage.of("right", i18n.asString("fakeplayer.command.turn.right.description")),
                                                Usage.of("back", i18n.asString("fakeplayer.command.turn.back.description")),
                                                Usage.of("to", i18n.asString("fakeplayer.command.turn.to.description"))
                                        )
                                ),
                        command("move")
                                .withPermission(Permission.move)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withSubcommands(
                                        command("forward")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(MoveCommand.instance.move(1, 0)),
                                        command("backward")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(MoveCommand.instance.move(-1, 0)),
                                        command("left")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(MoveCommand.instance.move(0, 1)),
                                        command("right")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(MoveCommand.instance.move(0, -1)),
                                        helpCommand(
                                                "/fp move",
                                                Usage.of("forward", i18n.asString("fakeplayer.command.move.forward.description")),
                                                Usage.of("backward", i18n.asString("fakeplayer.command.move.backward.description")),
                                                Usage.of("left", i18n.asString("fakeplayer.command.move.left.description")),
                                                Usage.of("right", i18n.asString("fakeplayer.command.move.right.description"))
                                        )
                                ),

                        command("ride")
                                .withPermission(Permission.ride)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withSubcommands(
                                        command("me")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executesPlayer(RideCommand.instance::rideMe),
                                        command("target")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RideCommand.instance::rideTarget),
                                        command("anything")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RideCommand.instance::rideAnything),
                                        command("vehicle")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RideCommand.instance::rideVehicle),
                                        command("stop")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(RideCommand.instance::stopRiding),
                                        helpCommand(
                                                "/fp ride",
                                                Usage.of("me", i18n.asString("fakeplayer.command.ride.me.description")),
                                                Usage.of("target", i18n.asString("fakeplayer.command.ride.target.description")),
                                                Usage.of("anything", i18n.asString("fakeplayer.command.ride.anything.description")),
                                                Usage.of("vehicle", i18n.asString("fakeplayer.command.ride.vehicle.description")),
                                                Usage.of("stop", i18n.asString("fakeplayer.command.ride.stop.description"))
                                        )
                                ),
                        command("swap")
                                .withPermission(Permission.swap)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(ActionCommand.instance::swap),
                        command("sleep")
                                .withPermission(Permission.sleep)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name", p -> !p.isSleeping()))
                                .executes(SleepCommand.instance::sleep),
                        command("wakeup")
                                .withPermission(Permission.wakeup)
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withOptionalArguments(fakeplayer("name", LivingEntity::isSleeping))
                                .executes(SleepCommand.instance::wakeup),

                        command("cmd")
                                .withRequirement(CommandRegistry::targetRequirement)
                                .withRequirement(sender -> sender.hasPermission(Permission.cmd) || !FakeplayerConfig.instance.getAllowCommands().isEmpty())
                                .withArguments(
                                        fakeplayer("name"),
                                        cmd("command").branchSuggestions(buildCmdSuggestion())
                                )
                                .executes(CmdCommand.instance::cmd),

                        command("reload")
                                .withPermission(CommandPermission.OP)
                                .executes(ReloadCommand.instance::reload)

                ).register();
    }

    private static @NotNull CommandAPICommand[] newActionCommands(@NotNull ActionType action) {
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

    private static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName, @Nullable Predicate<Player> predicate) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var target = sender.isOp()
                    ? FakeplayerManager.instance.get(info.currentInput())
                    : FakeplayerManager.instance.get(sender, info.currentInput());
            if (predicate != null && target != null && !predicate.test(target)) {
                target = null;
            }
            return target;
        }).replaceSuggestions(ArgumentSuggestions.stringsAsync(info -> CompletableFuture.supplyAsync(() -> {
            var sender = info.sender();
            var arg = info.currentArg();

            var targets = sender.isOp()
                    ? FakeplayerManager.instance.getAll(predicate)
                    : FakeplayerManager.instance.getAll(sender, predicate);

            var names = targets.stream().map(Player::getName);
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }

            return names.toArray(String[]::new);
        })));
    }


    private static @NotNull Argument<Player> fakeplayer(@NotNull String nodeName) {
        return fakeplayer(nodeName, null);
    }

    private static @NotNull Argument<List<Player>> fakeplayers(@NotNull String nodeName) {
        return new CustomArgument<List<Player>, String>(new StringArgument(nodeName), info -> {
            var sender = info.sender();
            var arg = info.currentInput();

            if (arg.equals("-a")) {
                return sender.isOp()
                        ? FakeplayerManager.instance.getAll()
                        : FakeplayerManager.instance.getAll(sender);
            }

            var target = sender.isOp()
                    ? FakeplayerManager.instance.get(arg)
                    : FakeplayerManager.instance.get(sender, arg);

            return target == null ? Collections.emptyList() : Collections.singletonList(target);
        }).replaceSuggestions(ArgumentSuggestions.stringsAsync(info -> CompletableFuture.supplyAsync(() -> {
            var sender = info.sender();
            var arg = info.currentArg().toLowerCase();

            var fakes = sender.isOp()
                    ? FakeplayerManager.instance.getAll()
                    : FakeplayerManager.instance.getAll(sender);

            var names = Stream.concat(fakes.stream().map(Player::getName), Stream.of("-a"));
            if (!arg.isEmpty()) {
                names = names.filter(n -> n.toLowerCase().contains(arg));
            }

            return names.toArray(String[]::new);
        })));
    }

    private static @NotNull Argument<Config<Object>> config(@NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var arg = info.currentInput();
            try {
                return Config.valueOf(arg);
            } catch (Exception e) {
                throw CustomArgumentException.fromString(i18n.asString("fakeplayer.command.config.set.error.invalid-option"));
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Config.values()).map(Config::name).toList()));
    }

    private static @NotNull Argument<Object> configValue(@NotNull String configNodeName, @NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            @SuppressWarnings("unchecked")
            var config = Objects.requireNonNull((Config<Object>) info.previousArgs().get(configNodeName));
            var arg = info.currentInput();
            if (!config.options().contains(arg)) {
                throw CustomArgumentException.fromString(i18n.asString("fakeplayer.command.config.set.error.invalid-value"));
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

    private static boolean respawnRequirement(@NotNull CommandSender sender) {
        if (FakeplayerConfig.instance.isKickOnDead()) {
            return false;
        }
        return sender.isOp() || FakeplayerManager.instance.countByCreator(sender) > 0;
    }

    private static boolean selectRequirement(@NotNull CommandSender sender) {
        return sender.isOp() || (FakeplayerConfig.instance.getPlayerLimit() > 1 && FakeplayerManager.instance.countByCreator(sender) > 0);
    }

    private static boolean targetRequirement(@NotNull CommandSender sender) {
        return sender.isOp() || FakeplayerManager.instance.countByCreator(sender) > 0;
    }

    @SuppressWarnings("unchecked")
    public static SuggestionsBranch<CommandSender>[] buildCmdSuggestion() {
        SuggestionsBranch<CommandSender> fromTarget = SuggestionsBranch.suggest(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> {
            var target = Iterables.getFirst(FakeplayerManager.instance.getAll(a -> true), null);
            if (target == null) {
                return Collections.emptyList();
            }
            var suggestion = CommandAPIBukkit.get().getSimpleCommandMap().tabComplete(target, info.currentArg());
            if (suggestion == null || suggestion.isEmpty()) {
                return Collections.emptyList();
            }
            return suggestion.stream().map(a -> a.substring(1)).toList();
        })));

        SuggestionsBranch<CommandSender> fromConfig = SuggestionsBranch.suggest(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> {
            var whitelist = FakeplayerConfig.instance.getAllowCommands();
            if (whitelist.isEmpty()) {
                return Collections.emptyList();
            }
            return new ArrayList<>(whitelist);
        })));

        return new SuggestionsBranch[]{fromTarget, fromConfig};
    }


}
