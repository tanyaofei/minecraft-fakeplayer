package io.github.hello09x.fakeplayer.core.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandPermission;
import io.github.hello09x.bedrock.command.Usage;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.core.command.impl.*;
import io.github.hello09x.fakeplayer.core.config.Config;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;

import static io.github.hello09x.bedrock.command.Commands.*;
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
                                    Usage.of("select", ComponentUtils.toString(translatable("fakeplayer.command.select.description"), null), Permission.select, CommandSupports::needSelect),
                                    Usage.of("selection", ComponentUtils.toString(translatable("fakeplayer.command.selection.description"), null), Permission.selection, CommandSupports::needSelect),
                                    Usage.of("spawn", ComponentUtils.toString(translatable("fakeplayer.command.spawn.description"), null), Permission.spawn),
                                    Usage.of("kill", ComponentUtils.toString(translatable("fakeplayer.command.kill.description"), null), Permission.kill),
                                    Usage.of("killall", ComponentUtils.toString(translatable("fakeplayer.command.killall.description"), null), Permission.op),
                                    Usage.of("list", ComponentUtils.toString(translatable("fakeplayer.command.list.description"), null), Permission.list),
                                    Usage.of("distance", ComponentUtils.toString(translatable("fakeplayer.command.distance.description"), null), Permission.distance),
                                    Usage.of("drop", ComponentUtils.toString(translatable("fakeplayer.command.drop.description"), null), Permission.drop),
                                    Usage.of("dropstack", ComponentUtils.toString(translatable("fakeplayer.command.dropstack.description"), null), Permission.dropstack),
                                    Usage.of("dropinv", ComponentUtils.toString(translatable("fakeplayer.command.dropinv.description"), null), Permission.dropinv),
                                    Usage.of("skin", ComponentUtils.toString(translatable("fakeplayer.command.skin.description"), null), Permission.skin),
                                    Usage.of("invsee", ComponentUtils.toString(translatable("fakeplayer.command.invsee.description"), null), Permission.invsee),
                                    Usage.of("sleep", ComponentUtils.toString(translatable("fakeplayer.command.sleep.description"), null), Permission.sleep),
                                    Usage.of("wakeup", ComponentUtils.toString(translatable("fakeplayer.command.wakeup.description"), null), Permission.wakeup),
                                    Usage.of("status", ComponentUtils.toString(translatable("fakeplayer.command.status.description"), null), Permission.status),
                                    Usage.of("respawn", ComponentUtils.toString(translatable("fakeplayer.command.respawn.description"), null), Permission.respawn, CommandSupports::hasDeadTarget),
                                    Usage.of("tp", ComponentUtils.toString(translatable("fakeplayer.command.tp.description"), null), Permission.tp),
                                    Usage.of("tphere", ComponentUtils.toString(translatable("fakeplayer.command.tphere.description"), null), Permission.tphere),
                                    Usage.of("tps", ComponentUtils.toString(translatable("fakeplayer.command.tps.description"), null), Permission.tps),
                                    Usage.of("set", ComponentUtils.toString(translatable("fakeplayer.command.set.description"), null), Permission.set),
                                    Usage.of("config", ComponentUtils.toString(translatable("fakeplayer.command.config.description"), null), Permission.config),
                                    Usage.of("expme", ComponentUtils.toString(translatable("fakeplayer.command.expme.description"), null), Permission.expme),
                                    Usage.of("attack", ComponentUtils.toString(translatable("fakeplayer.command.attack.description"), null), Permission.attack),
                                    Usage.of("mine", ComponentUtils.toString(translatable("fakeplayer.command.mine.description"), null), Permission.mine),
                                    Usage.of("use", ComponentUtils.toString(translatable("fakeplayer.command.use.description"), null), Permission.use),
                                    Usage.of("jump", ComponentUtils.toString(translatable("fakeplayer.command.jump.description"), null), Permission.jump),
                                    Usage.of("look", ComponentUtils.toString(translatable("fakeplayer.command.look.description"), null), Permission.look),
                                    Usage.of("turn", ComponentUtils.toString(translatable("fakeplayer.command.turn.description"), null), Permission.turn),
                                    Usage.of("move", ComponentUtils.toString(translatable("fakeplayer.command.move.description"), null), Permission.move),
                                    Usage.of("ride", ComponentUtils.toString(translatable("fakeplayer.command.ride.description"), null), Permission.ride),
                                    Usage.of("sneak", ComponentUtils.toString(translatable("fakeplayer.command.sneak.description"), null), Permission.sneak),
                                    Usage.of("swap", ComponentUtils.toString(translatable("fakeplayer.command.swap.description"), null), Permission.swap),
                                    Usage.of("hold", ComponentUtils.toString(translatable("fakeplayer.command.hold.description"), null), Permission.hold),
                                    Usage.of("stop", ComponentUtils.toString(translatable("fakeplayer.command.stop.description"), null), Permission.stop),
                                    Usage.of("cmd", ComponentUtils.toString(translatable("fakeplayer.command.cmd.description"), null), Permission.cmd),
                                    Usage.of("reload", ComponentUtils.toString(translatable("fakeplayer.command.reload.description"), null), Permission.op)
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
                                                Usage.of("north", ComponentUtils.toString(translatable("fakeplayer.command.look.north.description"), null)),
                                                Usage.of("south", ComponentUtils.toString(translatable("fakeplayer.command.look.south.description"), null)),
                                                Usage.of("west", ComponentUtils.toString(translatable("fakeplayer.command.look.west.description"), null)),
                                                Usage.of("east", ComponentUtils.toString(translatable("fakeplayer.command.look.east.description"), null)),
                                                Usage.of("up", ComponentUtils.toString(translatable("fakeplayer.command.look.up.description"), null)),
                                                Usage.of("down", ComponentUtils.toString(translatable("fakeplayer.command.look.down.description"), null)),
                                                Usage.of("at", ComponentUtils.toString(translatable("fakeplayer.command.look.at.description"), null)),
                                                Usage.of("entity (once | continuous | interval | stop)", ComponentUtils.toString(translatable("fakeplayer.command.look.entity.description"), null))
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
                                                Usage.of("left", ComponentUtils.toString(translatable("fakeplayer.command.turn.left.description"), null)),
                                                Usage.of("right", ComponentUtils.toString(translatable("fakeplayer.command.turn.right.description"), null)),
                                                Usage.of("back", ComponentUtils.toString(translatable("fakeplayer.command.turn.back.description"), null)),
                                                Usage.of("to", ComponentUtils.toString(translatable("fakeplayer.command.turn.to.description"), null))
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
                                                Usage.of("forward", ComponentUtils.toString(translatable("fakeplayer.command.move.forward.description"), null)),
                                                Usage.of("backward", ComponentUtils.toString(translatable("fakeplayer.command.move.backward.description"), null)),
                                                Usage.of("left", ComponentUtils.toString(translatable("fakeplayer.command.move.left.description"), null)),
                                                Usage.of("right", ComponentUtils.toString(translatable("fakeplayer.command.move.right.description"), null))
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
                                                Usage.of("me", ComponentUtils.toString(translatable("fakeplayer.command.ride.me.description"), null)),
                                                Usage.of("target", ComponentUtils.toString(translatable("fakeplayer.command.ride.target.description"), null)),
                                                Usage.of("anything", ComponentUtils.toString(translatable("fakeplayer.command.ride.anything.description"), null)),
                                                Usage.of("vehicle", ComponentUtils.toString(translatable("fakeplayer.command.ride.vehicle.description"), null)),
                                                Usage.of("stop", ComponentUtils.toString(translatable("fakeplayer.command.ride.stop.description"), null))
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
