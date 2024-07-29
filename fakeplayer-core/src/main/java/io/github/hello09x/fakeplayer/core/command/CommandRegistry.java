package io.github.hello09x.fakeplayer.core.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandPermission;
import io.github.hello09x.devtools.command.exception.Usage;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.core.command.impl.*;
import io.github.hello09x.fakeplayer.core.config.Config;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;

import static io.github.hello09x.devtools.command.exception.Commands.*;
import static io.github.hello09x.fakeplayer.core.command.CommandSupports.*;
import static net.kyori.adventure.text.Component.translatable;


@Singleton
@SuppressWarnings("SameParameterValue")
public class CommandRegistry {

    @Inject
    private ActionCommand actionCommand;
    @Inject
    private CmdCommand cmdCommand;
    @Inject
    private ConfigCommand configCommand;
    @Inject
    private DistanceCommand distanceCommand;
    @Inject
    private ExpmeCommand expmeCommand;
    @Inject
    private HoldCommand holdCommand;
    @Inject
    private InvseeCommand invseeCommand;
    @Inject
    private KillCommand killCommand;
    @Inject
    private KillallCommand killallCommand;
    @Inject
    private ListCommand listCommand;
    @Inject
    private MoveCommand moveCommand;
    @Inject
    private ReloadCommand reloadCommand;
    @Inject
    private RespawnCommand respawnCommand;
    @Inject
    private RideCommand rideCommand;
    @Inject
    private RotationCommand rotationCommand;
    @Inject
    private SelectCommand selectCommand;
    @Inject
    private SetCommand setCommand;
    @Inject
    private SkinCommand skinCommand;
    @Inject
    private SleepCommand sleepCommand;
    @Inject
    private SneakCommand sneakCommand;
    @Inject
    private SpawnCommand spawnCommand;
    @Inject
    private StatusCommand statusCommand;
    @Inject
    private SwapCommand swapCommand;
    @Inject
    private TeleportCommand teleportCommand;
    @Inject
    private DebugCommand debugCommand;
    @Inject
    private StopCommand stopCommand;
    @Inject
    private TranslationCommand translationCommand;

    @Inject
    private Config config;

    public void register() {
        command("fakeplayer")
                .withAliases("fp")
                .withHelp(
                        ComponentUtils.toString(translatable("fakeplayer.command.fp.short-description"), null),
                        ComponentUtils.toString(translatable("fakeplayer.command.fp.full-description"), null)
                )
                .withUsage(
                        "type fp ? for more information",
                        "hello09x [汤姆]"
                )
                .withPermission(Permission.spawn)
                .withSubcommands(
                        helpCommand("/fp",
                                    Usage.of("select", translatable("fakeplayer.command.select.description"), Permission.select, CommandSupports::needSelect),
                                    Usage.of("selection", translatable("fakeplayer.command.selection.description"), Permission.selection, CommandSupports::needSelect),
                                    Usage.of("spawn", translatable("fakeplayer.command.spawn.description"), Permission.spawn),
                                    Usage.of("kill", translatable("fakeplayer.command.kill.description"), Permission.kill),
                                    Usage.of("killall", translatable("fakeplayer.command.killall.description"), Permission.op),
                                    Usage.of("list", translatable("fakeplayer.command.list.description"), Permission.list),
                                    Usage.of("distance", translatable("fakeplayer.command.distance.description"), Permission.distance),
                                    Usage.of("drop", translatable("fakeplayer.command.drop.description"), Permission.drop),
                                    Usage.of("dropstack", translatable("fakeplayer.command.dropstack.description"), Permission.dropstack),
                                    Usage.of("dropinv", translatable("fakeplayer.command.dropinv.description"), Permission.dropinv),
                                    Usage.of("skin", translatable("fakeplayer.command.skin.description"), Permission.skin),
                                    Usage.of("invsee", translatable("fakeplayer.command.invsee.description"), Permission.invsee),
                                    Usage.of("sleep", translatable("fakeplayer.command.sleep.description"), Permission.sleep),
                                    Usage.of("wakeup", translatable("fakeplayer.command.wakeup.description"), Permission.wakeup),
                                    Usage.of("status", translatable("fakeplayer.command.status.description"), Permission.status),
                                    Usage.of("respawn", translatable("fakeplayer.command.respawn.description"), Permission.respawn, CommandSupports::hasDeadTarget),
                                    Usage.of("tp", translatable("fakeplayer.command.tp.description"), Permission.tp),
                                    Usage.of("tphere", translatable("fakeplayer.command.tphere.description"), Permission.tphere),
                                    Usage.of("tps", translatable("fakeplayer.command.tps.description"), Permission.tps),
                                    Usage.of("set", translatable("fakeplayer.command.set.description"), Permission.set),
                                    Usage.of("config", translatable("fakeplayer.command.config.description"), Permission.config),
                                    Usage.of("expme", translatable("fakeplayer.command.expme.description"), Permission.expme),
                                    Usage.of("attack", translatable("fakeplayer.command.attack.description"), Permission.attack),
                                    Usage.of("mine", translatable("fakeplayer.command.mine.description"), Permission.mine),
                                    Usage.of("use", translatable("fakeplayer.command.use.description"), Permission.use),
                                    Usage.of("jump", translatable("fakeplayer.command.jump.description"), Permission.jump),
                                    Usage.of("look", translatable("fakeplayer.command.look.description"), Permission.look),
                                    Usage.of("turn", translatable("fakeplayer.command.turn.description"), Permission.turn),
                                    Usage.of("move", translatable("fakeplayer.command.move.description"), Permission.move),
                                    Usage.of("ride", translatable("fakeplayer.command.ride.description"), Permission.ride),
                                    Usage.of("sneak", translatable("fakeplayer.command.sneak.description"), Permission.sneak),
                                    Usage.of("swap", translatable("fakeplayer.command.swap.description"), Permission.swap),
                                    Usage.of("hold", translatable("fakeplayer.command.hold.description"), Permission.hold),
                                    Usage.of("stop", translatable("fakeplayer.command.stop.description"), Permission.stop),
                                    Usage.of("cmd", translatable("fakeplayer.command.cmd.description"), Permission.cmd),
                                    Usage.of("reload", translatable("fakeplayer.command.reload.description"), Permission.op),
                                    Usage.of("reload-translation", translatable("fakeplayer.command.reload-translation.description"), Permission.op)
                        ),

                        command("select")
                                .withPermission(Permission.select)
                                .withRequirement(CommandSupports::needSelect)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(selectCommand::select),
                        command("selection")
                                .withPermission(Permission.select)
                                .withRequirement(CommandSupports::needSelect)
                                .executesPlayer(selectCommand::selection),

                        command("spawn")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        text("name").withPermission(Permission.spawnName),
                                        world("world").withPermission(Permission.spawnLocation),
                                        location("location").withPermission(Permission.spawnLocation))
                                .executes(spawnCommand::spawn),
                        command("kill")
                                .withPermission(Permission.kill)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(targets("names"))
                                .executes(killCommand::kill),
                        command("list")
                                .withPermission(Permission.list)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(
                                        int32("page", 1),
                                        int32("size", 1))
                                .executes(listCommand::list),
                        command("distance")
                                .withPermission(Permission.distance)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(distanceCommand::distance),
                        command("skin")
                                .withPermission(Permission.skin)
                                .withRequirement(CommandSupports::hasTarget)
                                .withArguments(offlinePlayer("player"))
                                .withOptionalArguments(target("name"))
                                .executes(skinCommand::skin),
                        command("invsee")
                                .withPermission(Permission.invsee)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(invseeCommand::invsee),
                        command("hold")
                                .withPermission(Permission.hold)
                                .withRequirement(CommandSupports::hasTarget)
                                .withArguments(int32("slot", 1, 9))
                                .withOptionalArguments(target("name"))
                                .executes(holdCommand::hold),
                        command("status")
                                .withPermission(Permission.status)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executes(statusCommand::status),
                        command("respawn")
                                .withRequirement(CommandSupports::hasDeadTarget)
                                .withPermission(Permission.respawn)
                                .withOptionalArguments(target("name", Entity::isDead))
                                .executes(respawnCommand::respawn),
                        command("set")
                                .withRequirement(CommandSupports::hasTarget)
                                .withPermission(Permission.set)
                                .withArguments(
                                        config("config", io.github.hello09x.fakeplayer.core.repository.model.Config::hasAccessor),
                                        configValue("config", "value")
                                )
                                .withOptionalArguments(target("name"))
                                .executes(setCommand::set),
                        command("config")
                                .withPermission(Permission.config)
                                .withSubcommands(
                                        command("set")
                                                .withArguments(
                                                        config("config"),
                                                        configValue("config", "value"))
                                                .executesPlayer(configCommand::setConfig),
                                        command("list")
                                                .executes(configCommand::listConfig)
                                )
                                .executes(configCommand::listConfig),

                        command("expme")
                                .withPermission(Permission.expme)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(expmeCommand::expme),

                        command("tp")
                                .withPermission(Permission.tp)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(teleportCommand::tp),
                        command("tphere")
                                .withPermission(Permission.tphere)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(teleportCommand::tphere),
                        command("tps")
                                .withPermission(Permission.tps)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(teleportCommand::tps),

                        command("attack")
                                .withPermission(Permission.attack)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.ATTACK))
                                .executes(actionCommand.action(Action.ActionType.ATTACK, Action.ActionSetting.once())),
                        command("mine")
                                .withPermission(Permission.mine)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.MINE))
                                .executes(actionCommand.action(Action.ActionType.MINE, Action.ActionSetting.once())),
                        command("use")
                                .withPermission(Permission.use)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.USE))
                                .executes(actionCommand.action(Action.ActionType.USE, Action.ActionSetting.once())),
                        command("jump")
                                .withPermission(Permission.jump)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.JUMP))
                                .executes(actionCommand.action(Action.ActionType.JUMP, Action.ActionSetting.once())),
                        command("drop")
                                .withPermission(Permission.drop)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_ITEM))
                                .executes(actionCommand.action(Action.ActionType.DROP_ITEM, Action.ActionSetting.once())),
                        command("dropstack")
                                .withPermission(Permission.dropstack)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_STACK))
                                .executes(actionCommand.action(Action.ActionType.DROP_STACK, Action.ActionSetting.once())),
                        command("dropinv")
                                .withPermission(Permission.dropinv)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_INVENTORY))
                                .executes(actionCommand.action(Action.ActionType.DROP_INVENTORY, Action.ActionSetting.once())),
                        command("sneak")
                                .withPermission(Permission.sneak)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(
                                        target("name"),
                                        literals("sneaking", List.of("true", "false")))
                                .executes(sneakCommand::sneak),
                        command("look")
                                .withPermission(Permission.look)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("north")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.NORTH)),
                                        command("south")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.SOUTH)),
                                        command("west")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.WEST)),
                                        command("east")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.EAST)),
                                        command("up")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.UP)),
                                        command("down")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.DOWN)),
                                        command("at")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand::lookAt),
                                        command("entity")
                                                .withOptionalArguments(target("name"))
                                                .withSubcommands(newActionCommands(Action.ActionType.LOOK_AT_NEAREST_ENTITY)),
                                        helpCommand(
                                                "/fp look",
                                                Usage.of("north", translatable("fakeplayer.command.look.north.description")),
                                                Usage.of("south", translatable("fakeplayer.command.look.south.description")),
                                                Usage.of("west", translatable("fakeplayer.command.look.west.description")),
                                                Usage.of("east", translatable("fakeplayer.command.look.east.description")),
                                                Usage.of("up", translatable("fakeplayer.command.look.up.description")),
                                                Usage.of("down", translatable("fakeplayer.command.look.down.description")),
                                                Usage.of("at", translatable("fakeplayer.command.look.at.description")),
                                                Usage.of("entity (once | continuous | interval | stop)", translatable("fakeplayer.command.look.entity.description"))
                                        )
                                ),
                        command("turn")
                                .withPermission(Permission.turn)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("left")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.turn(-90, 0)),
                                        command("right")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.turn(90, 0)),
                                        command("back")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.turn(180, 0)),
                                        command("to")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand::turnTo),
                                        helpCommand(
                                                "/fp turn",
                                                Usage.of("left", translatable("fakeplayer.command.turn.left.description")),
                                                Usage.of("right", translatable("fakeplayer.command.turn.right.description")),
                                                Usage.of("back", translatable("fakeplayer.command.turn.back.description")),
                                                Usage.of("to", translatable("fakeplayer.command.turn.to.description"))
                                        )
                                ),
                        command("move")
                                .withPermission(Permission.move)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("forward")
                                                .withOptionalArguments(target("name"))
                                                .executes(moveCommand.move(1, 0)),
                                        command("backward")
                                                .withOptionalArguments(target("name"))
                                                .executes(moveCommand.move(-1, 0)),
                                        command("left")
                                                .withOptionalArguments(target("name"))
                                                .executes(moveCommand.move(0, 1)),
                                        command("right")
                                                .withOptionalArguments(target("name"))
                                                .executes(moveCommand.move(0, -1)),
                                        helpCommand(
                                                "/fp move",
                                                Usage.of("forward", translatable("fakeplayer.command.move.forward.description")),
                                                Usage.of("backward", translatable("fakeplayer.command.move.backward.description")),
                                                Usage.of("left", translatable("fakeplayer.command.move.left.description")),
                                                Usage.of("right", translatable("fakeplayer.command.move.right.description"))
                                        )
                                )
                                .executes(moveCommand.move(1, 0)),

                        command("ride")
                                .withPermission(Permission.ride)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("me")
                                                .withOptionalArguments(target("name"))
                                                .executesPlayer(rideCommand::rideMe),
                                        command("target")
                                                .withOptionalArguments(target("name"))
                                                .executes(rideCommand::rideTarget),
                                        command("anything")
                                                .withOptionalArguments(target("name"))
                                                .executes(rideCommand::rideAnything),
                                        command("vehicle")
                                                .withOptionalArguments(target("name"))
                                                .executes(rideCommand::rideVehicle),
                                        command("stop")
                                                .withOptionalArguments(target("name"))
                                                .executes(rideCommand::stopRiding),
                                        helpCommand(
                                                "/fp ride",
                                                Usage.of("me", translatable("fakeplayer.command.ride.me.description")),
                                                Usage.of("target", translatable("fakeplayer.command.ride.target.description")),
                                                Usage.of("anything", translatable("fakeplayer.command.ride.anything.description")),
                                                Usage.of("vehicle", translatable("fakeplayer.command.ride.vehicle.description")),
                                                Usage.of("stop", translatable("fakeplayer.command.ride.stop.description"))
                                        )
                                ),
                        command("swap")
                                .withPermission(Permission.swap)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executes(swapCommand::swap),
                        command("sleep")
                                .withPermission(Permission.sleep)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name", p -> !p.isSleeping()))
                                .executes(sleepCommand::sleep),
                        command("wakeup")
                                .withPermission(Permission.wakeup)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name", LivingEntity::isSleeping))
                                .executes(sleepCommand::wakeup),
                        command("stop")
                                .withPermission(Permission.stop)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executes(stopCommand::stop),

                        command("cmd")
                                .withRequirement(CommandSupports::isCmdAvailable)
                                .withArguments(
                                        target("name"),
                                        cmd("command")
                                )
                                .executes(cmdCommand::cmd),

                        command("killall")
                                .withPermission(CommandPermission.OP)
                                .executes(killallCommand::killall),
                        command("reload")
                                .withPermission(CommandPermission.OP)
                                .executes(reloadCommand::reload),
                        command("reload-translation")
                                .withPermission(CommandPermission.OP)
                                .executes(translationCommand::reloadTranslation),

                        // developer debug
                        command("debug")
                                .withPermission(CommandPermission.OP)
                                .withRequirement(sender -> config.isDebug())
                                .withSubcommands(
                                        command("send-plugin-message")
                                                .withArguments(
                                                        player("player"),
                                                        text("channel"),
                                                        text("message")
                                                )
                                                .executes(debugCommand::sendPluginMessage)
                                )

                ).register();
    }


}
