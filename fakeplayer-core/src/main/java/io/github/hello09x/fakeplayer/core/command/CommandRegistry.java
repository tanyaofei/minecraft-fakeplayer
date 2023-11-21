package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandPermission;
import io.github.hello09x.bedrock.command.Usage;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.command.impl.*;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;

import static io.github.hello09x.bedrock.command.Commands.*;
import static io.github.hello09x.fakeplayer.core.command.CommandSupports.*;


@SuppressWarnings("SameParameterValue")
public class CommandRegistry {

    private final static I18n i18n = Main.getI18n();


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
                                Usage.of("select", i18n.asString("fakeplayer.command.select.description"), Permission.select, CommandSupports::needSelect),
                                Usage.of("selection", i18n.asString("fakeplayer.command.selection.description"), Permission.selection, CommandSupports::needSelect),
                                Usage.of("spawn", i18n.asString("fakeplayer.command.spawn.description"), Permission.spawn),
                                Usage.of("kill", i18n.asString("fakeplayer.command.kill.description"), Permission.kill),
                                Usage.of("killall", i18n.asString("fakeplayer.command.killall.description"), Permission.op),
                                Usage.of("list", i18n.asString("fakeplayer.command.list.description"), Permission.list),
                                Usage.of("distance", i18n.asString("fakeplayer.command.distance.description"), Permission.distance),
                                Usage.of("drop", i18n.asString("fakeplayer.command.drop.description"), Permission.drop),
                                Usage.of("dropstack", i18n.asString("fakeplayer.command.dropstack.description"), Permission.dropstack),
                                Usage.of("dropinv", i18n.asString("fakeplayer.command.dropinv.description"), Permission.dropinv),
                                Usage.of("skin", i18n.asString("fakeplayer.command.skin.description"), Permission.skin),
                                Usage.of("invsee", i18n.asString("fakeplayer.command.invsee.description"), Permission.invsee),
                                Usage.of("sleep", i18n.asString("fakeplayer.command.sleep.description"), Permission.sleep),
                                Usage.of("wakeup", i18n.asString("fakeplayer.command.wakeup.description"), Permission.wakeup),
                                Usage.of("status", i18n.asString("fakeplayer.command.status.description"), Permission.status),
                                Usage.of("respawn", i18n.asString("fakeplayer.command.respawn.description"), Permission.respawn, CommandSupports::hasDeadTarget),
                                Usage.of("tp", i18n.asString("fakeplayer.command.tp.description"), Permission.tp),
                                Usage.of("tphere", i18n.asString("fakeplayer.command.tphere.description"), Permission.tphere),
                                Usage.of("tps", i18n.asString("fakeplayer.command.tps.description"), Permission.tps),
                                Usage.of("set", i18n.asString("fakeplayer.command.set.description"), Permission.set),
                                Usage.of("config", i18n.asString("fakeplayer.command.config.description"), Permission.config),
                                Usage.of("expme", i18n.asString("fakeplayer.command.expme.description"), Permission.expme),
                                Usage.of("attack", i18n.asString("fakeplayer.command.attack.description"), Permission.attack),
                                Usage.of("mine", i18n.asString("fakeplayer.command.mine.description"), Permission.mine),
                                Usage.of("use", i18n.asString("fakeplayer.command.use.description"), Permission.use),
                                Usage.of("jump", i18n.asString("fakeplayer.command.jump.description"), Permission.jump),
                                Usage.of("look", i18n.asString("fakeplayer.command.look.description"), Permission.look),
                                Usage.of("turn", i18n.asString("fakeplayer.command.turn.description"), Permission.turn),
                                Usage.of("move", i18n.asString("fakeplayer.command.move.description"), Permission.move),
                                Usage.of("ride", i18n.asString("fakeplayer.command.ride.description"), Permission.ride),
                                Usage.of("sneak", i18n.asString("fakeplayer.command.sneak.description"), Permission.sneak),
                                Usage.of("swap", i18n.asString("fakeplayer.command.swap.description"), Permission.swap),
                                Usage.of("hold", i18n.asString("fakeplayer.command.hold.description"), Permission.hold),
                                Usage.of("cmd", i18n.asString("fakeplayer.command.cmd.description"), Permission.cmd),
                                Usage.of("reload", i18n.asString("fakeplayer.command.reload.description"), Permission.op)
                        ),

                        command("select")
                                .withPermission(Permission.select)
                                .withRequirement(CommandSupports::needSelect)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(SelectCommand.instance::select),
                        command("selection")
                                .withPermission(Permission.select)
                                .withRequirement(CommandSupports::needSelect)
                                .executesPlayer(SelectCommand.instance::selection),

                        command("spawn")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        text("name").withPermission(Permission.spawnName),
                                        world("world").withPermission(Permission.spawnLocation),
                                        location("location").withPermission(Permission.spawnLocation))
                                .executes(SpawnCommand.instance::spawn),
                        command("kill")
                                .withPermission(Permission.kill)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(targets("names"))
                                .executes(KillCommand.instance::kill),
                        command("list")
                                .withPermission(Permission.list)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(
                                        int32("page", 1),
                                        int32("size", 1))
                                .executes(ListCommand.instance::list),
                        command("distance")
                                .withPermission(Permission.distance)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(DistanceCommand.instance::distance),
                        command("skin")
                                .withPermission(Permission.skin)
                                .withRequirement(CommandSupports::hasTarget)
                                .withArguments(offlinePlayer("player"))
                                .withOptionalArguments(target("name"))
                                .executes(SkinCommand.instance::skin),
                        command("invsee")
                                .withPermission(Permission.invsee)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(InvseeCommand.instance::invsee),
                        command("hold")
                                .withPermission(Permission.hold)
                                .withRequirement(CommandSupports::hasTarget)
                                .withArguments(int32("slot", 1, 9))
                                .withOptionalArguments(target("name"))
                                .executes(HoldCommand.instance::hold),
                        command("status")
                                .withPermission(Permission.status)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executes(StatusCommand.instance::status),
                        command("respawn")
                                .withRequirement(CommandSupports::hasDeadTarget)
                                .withPermission(Permission.respawn)
                                .withOptionalArguments(target("name", Entity::isDead))
                                .executes(RespawnCommand.instance::respawn),
                        command("set")
                                .withRequirement(CommandSupports::hasTarget)
                                .withPermission(Permission.set)
                                .withArguments(
                                        config("config", Config::hasConfigurer),
                                        configValue("config", "value")
                                )
                                .withOptionalArguments(target("name"))
                                .executes(SetCommand.instance::set),
                        command("config")
                                .withPermission(Permission.config)
                                .withSubcommands(
                                        command("set")
                                                .withArguments(
                                                        config("config"),
                                                        configValue("config", "value"))
                                                .executesPlayer(ConfigCommand.instance::setConfig),
                                        command("list")
                                                .executesPlayer(ConfigCommand.instance::listConfig)
                                )
                                .executesPlayer(ConfigCommand.instance::listConfig),

                        command("expme")
                                .withPermission(Permission.expme)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(ExpmeCommand.instance::expme),

                        command("tp")
                                .withPermission(Permission.tp)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(TeleportCommand.instance::tp),
                        command("tphere")
                                .withPermission(Permission.tphere)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(TeleportCommand.instance::tphere),
                        command("tps")
                                .withPermission(Permission.tps)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(TeleportCommand.instance::tps),

                        command("attack")
                                .withPermission(Permission.attack)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.ATTACK))
                                .executes(ActionCommand.instance.action(Action.ActionType.ATTACK, Action.ActionSetting.once())),
                        command("mine")
                                .withPermission(Permission.mine)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.MINE))
                                .executes(ActionCommand.instance.action(Action.ActionType.MINE, Action.ActionSetting.once())),
                        command("use")
                                .withPermission(Permission.use)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.USE))
                                .executes(ActionCommand.instance.action(Action.ActionType.USE, Action.ActionSetting.once())),
                        command("jump")
                                .withPermission(Permission.jump)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.JUMP))
                                .executes(ActionCommand.instance.action(Action.ActionType.JUMP, Action.ActionSetting.once())),
                        command("drop")
                                .withPermission(Permission.drop)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_ITEM))
                                .executes(ActionCommand.instance.action(Action.ActionType.DROP_ITEM, Action.ActionSetting.once())),
                        command("dropstack")
                                .withPermission(Permission.dropstack)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_STACK))
                                .executes(ActionCommand.instance.action(Action.ActionType.DROP_STACK, Action.ActionSetting.once())),
                        command("dropinv")
                                .withPermission(Permission.dropinv)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_INVENTORY))
                                .executes(ActionCommand.instance.action(Action.ActionType.DROP_INVENTORY, Action.ActionSetting.once())),
                        command("sneak")
                                .withPermission(Permission.sneak)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(
                                        target("name"),
                                        literals("sneaking", List.of("true", "false")))
                                .executes(SneakCommand.instance::sneak),
                        command("look")
                                .withPermission(Permission.look)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("north")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.look(Direction.NORTH)),
                                        command("south")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.look(Direction.SOUTH)),
                                        command("west")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.look(Direction.WEST)),
                                        command("east")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.look(Direction.EAST)),
                                        command("up")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.look(Direction.UP)),
                                        command("down")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.look(Direction.DOWN)),
                                        command("at")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance::lookAt),
                                        command("entity")
                                                .withOptionalArguments(target("name"))
                                                .withSubcommands(newActionCommands(Action.ActionType.LOOK_AT_NEAREST_ENTITY)),
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
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("left")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.turn(-90, 0)),
                                        command("right")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.turn(90, 0)),
                                        command("back")
                                                .withOptionalArguments(target("name"))
                                                .executes(RotationCommand.instance.turn(180, 0)),
                                        command("to")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(target("name"))
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
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("forward")
                                                .withOptionalArguments(target("name"))
                                                .executes(MoveCommand.instance.move(1, 0)),
                                        command("backward")
                                                .withOptionalArguments(target("name"))
                                                .executes(MoveCommand.instance.move(-1, 0)),
                                        command("left")
                                                .withOptionalArguments(target("name"))
                                                .executes(MoveCommand.instance.move(0, 1)),
                                        command("right")
                                                .withOptionalArguments(target("name"))
                                                .executes(MoveCommand.instance.move(0, -1)),
                                        helpCommand(
                                                "/fp move",
                                                Usage.of("forward", i18n.asString("fakeplayer.command.move.forward.description")),
                                                Usage.of("backward", i18n.asString("fakeplayer.command.move.backward.description")),
                                                Usage.of("left", i18n.asString("fakeplayer.command.move.left.description")),
                                                Usage.of("right", i18n.asString("fakeplayer.command.move.right.description"))
                                        )
                                )
                                .executes(MoveCommand.instance.move(1, 0)),

                        command("ride")
                                .withPermission(Permission.ride)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("me")
                                                .withOptionalArguments(target("name"))
                                                .executesPlayer(RideCommand.instance::rideMe),
                                        command("target")
                                                .withOptionalArguments(target("name"))
                                                .executes(RideCommand.instance::rideTarget),
                                        command("anything")
                                                .withOptionalArguments(target("name"))
                                                .executes(RideCommand.instance::rideAnything),
                                        command("vehicle")
                                                .withOptionalArguments(target("name"))
                                                .executes(RideCommand.instance::rideVehicle),
                                        command("stop")
                                                .withOptionalArguments(target("name"))
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
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executes(SwapCommand.instance::swap),
                        command("sleep")
                                .withPermission(Permission.sleep)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name", p -> !p.isSleeping()))
                                .executes(SleepCommand.instance::sleep),
                        command("wakeup")
                                .withPermission(Permission.wakeup)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name", LivingEntity::isSleeping))
                                .executes(SleepCommand.instance::wakeup),

                        command("cmd")
                                .withRequirement(CommandSupports::isCmdAvailable)
                                .withArguments(
                                        target("name"),
                                        cmd("command")
                                )
                                .executes(CmdCommand.instance::cmd),

                        command("killall")
                                .withPermission(CommandPermission.OP)
                                .executes(KillallCommand.instance::killall),
                        command("reload")
                                .withPermission(CommandPermission.OP)
                                .executes(ReloadCommand.instance::reload)

                ).register();
    }


}
