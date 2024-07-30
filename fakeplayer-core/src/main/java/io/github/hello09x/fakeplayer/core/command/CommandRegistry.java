package io.github.hello09x.fakeplayer.core.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandPermission;
import io.github.hello09x.devtools.command.exception.HelpCommand;
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
    private Config config;

    public void register() {
        var root = command("fakeplayer")
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
                        command("select")
                                .withPermission(Permission.select)
                                .withShortDescription("fakeplayer.command.select.description")
                                .withRequirement(CommandSupports::needSelect)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(selectCommand::select),
                        command("selection")
                                .withPermission(Permission.select)
                                .withShortDescription("fakeplayer.command.selection.description")
                                .withRequirement(CommandSupports::needSelect)
                                .executesPlayer(selectCommand::selection),

                        command("spawn")
                                .withPermission(Permission.spawn)
                                .withShortDescription("fakeplayer.command.spawn.description")
                                .withOptionalArguments(
                                        text("name").withPermission(Permission.spawnName),
                                        world("world").withPermission(Permission.spawnLocation),
                                        location("location").withPermission(Permission.spawnLocation))
                                .executes(spawnCommand::spawn),
                        command("kill")
                                .withPermission(Permission.kill)
                                .withShortDescription("fakeplayer.command.kill.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(targets("names"))
                                .executes(killCommand::kill),
                        command("list")
                                .withPermission(Permission.list)
                                .withRequirement(CommandSupports::hasTarget)
                                .withShortDescription("fakeplayer.command.list.description")
                                .withOptionalArguments(
                                        int32("page", 1),
                                        int32("size", 1))
                                .executes(listCommand::list),
                        command("distance")
                                .withPermission(Permission.distance)
                                .withShortDescription("fakeplayer.command.distance.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(distanceCommand::distance),
                        command("skin")
                                .withPermission(Permission.skin)
                                .withShortDescription("fakeplayer.command.skin.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withArguments(offlinePlayer("player"))
                                .withOptionalArguments(target("name"))
                                .executes(skinCommand::skin),
                        command("invsee")
                                .withPermission(Permission.invsee)
                                .withShortDescription("fakeplayer.command.invsee.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(invseeCommand::invsee),
                        command("hold")
                                .withPermission(Permission.hold)
                                .withShortDescription("fakeplayer.command.hold.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withArguments(int32("slot", 1, 9))
                                .withOptionalArguments(target("name"))
                                .executes(holdCommand::hold),
                        command("status")
                                .withPermission(Permission.status)
                                .withShortDescription("fakeplayer.command.status.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executes(statusCommand::status),
                        command("respawn")
                                .withRequirement(CommandSupports::hasDeadTarget)
                                .withShortDescription("fakeplayer.command.respawn.description")
                                .withPermission(Permission.respawn)
                                .withOptionalArguments(target("name", Entity::isDead))
                                .executes(respawnCommand::respawn),
                        command("set")
                                .withRequirement(CommandSupports::hasTarget)
                                .withShortDescription("fakeplayer.command.set.description")
                                .withPermission(Permission.set)
                                .withArguments(
                                        config("config", io.github.hello09x.fakeplayer.core.repository.model.Config::hasAccessor),
                                        configValue("config", "value")
                                )
                                .withOptionalArguments(target("name"))
                                .executes(setCommand::set),
                        command("config")
                                .withPermission(Permission.config)
                                .withShortDescription("fakeplayer.command.config.description")
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
                                .withShortDescription("fakeplayer.command.expme.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(expmeCommand::expme),

                        command("tp")
                                .withPermission(Permission.tp)
                                .withShortDescription("fakeplayer.command.tp.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(teleportCommand::tp),
                        command("tphere")
                                .withPermission(Permission.tphere)
                                .withShortDescription("fakeplayer.command.tphere.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(teleportCommand::tphere),
                        command("tps")
                                .withPermission(Permission.tps)
                                .withShortDescription("fakeplayer.command.tphere.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executesPlayer(teleportCommand::tps),

                        command("attack")
                                .withPermission(Permission.attack)
                                .withShortDescription("fakeplayer.command.attack.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.ATTACK))
                                .executes(actionCommand.action(Action.ActionType.ATTACK, Action.ActionSetting.once())),
                        command("mine")
                                .withPermission(Permission.mine)
                                .withShortDescription("fakeplayer.command.mine.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.MINE))
                                .executes(actionCommand.action(Action.ActionType.MINE, Action.ActionSetting.once())),
                        command("use")
                                .withPermission(Permission.use)
                                .withShortDescription("fakeplayer.command.use.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.USE))
                                .executes(actionCommand.action(Action.ActionType.USE, Action.ActionSetting.once())),
                        command("jump")
                                .withPermission(Permission.jump)
                                .withShortDescription("fakeplayer.command.jump.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.JUMP))
                                .executes(actionCommand.action(Action.ActionType.JUMP, Action.ActionSetting.once())),
                        command("drop")
                                .withPermission(Permission.drop)
                                .withShortDescription("fakeplayer.command.drop.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_ITEM))
                                .executes(actionCommand.action(Action.ActionType.DROP_ITEM, Action.ActionSetting.once())),
                        command("dropstack")
                                .withPermission(Permission.dropstack)
                                .withShortDescription("fakeplayer.command.dropstack.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_STACK))
                                .executes(actionCommand.action(Action.ActionType.DROP_STACK, Action.ActionSetting.once())),
                        command("dropinv")
                                .withPermission(Permission.dropinv)
                                .withShortDescription("fakeplayer.command.dropinv.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(newActionCommands(Action.ActionType.DROP_INVENTORY))
                                .executes(actionCommand.action(Action.ActionType.DROP_INVENTORY, Action.ActionSetting.once())),
                        command("sneak")
                                .withPermission(Permission.sneak)
                                .withShortDescription("fakeplayer.command.sneak.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(
                                        target("name"),
                                        literals("sneaking", List.of("true", "false")))
                                .executes(sneakCommand::sneak),
                        command("look")
                                .withPermission(Permission.look)
                                .withShortDescription("fakeplayer.command.look.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("north")
                                                .withShortDescription("fakeplayer.command.look.north.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.NORTH)),
                                        command("south")
                                                .withShortDescription("fakeplayer.command.look.south.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.SOUTH)),
                                        command("west")
                                                .withShortDescription("fakeplayer.command.look.west.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.WEST)),
                                        command("east")
                                                .withShortDescription("fakeplayer.command.look.east.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.EAST)),
                                        command("up")
                                                .withShortDescription("fakeplayer.command.look.up.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.UP)),
                                        command("down")
                                                .withShortDescription("fakeplayer.command.look.down.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.look(Direction.DOWN)),
                                        command("at")
                                                .withShortDescription("fakeplayer.command.look.at.description")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand::lookAt),
                                        command("entity")
                                                .withShortDescription("fakeplayer.command.look.entity.description")
                                                .withOptionalArguments(target("name"))
                                                .withSubcommands(newActionCommands(Action.ActionType.LOOK_AT_NEAREST_ENTITY))
                                ),
                        command("turn")
                                .withPermission(Permission.turn)
                                .withShortDescription("fakeplayer.command.turn.description")
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("left")
                                                .withShortDescription("fakeplayer.command.turn.left.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.turn(-90, 0)),
                                        command("right")
                                                .withShortDescription("fakeplayer.command.turn.right.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.turn(90, 0)),
                                        command("back")
                                                .withShortDescription("fakeplayer.command.turn.back.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand.turn(180, 0)),
                                        command("to")
                                                .withShortDescription("fakeplayer.command.turn.to.description")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(target("name"))
                                                .executes(rotationCommand::turnTo)
                                ),
                        command("move")
                                .withShortDescription("fakeplayer.command.move.description")
                                .withPermission(Permission.move)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("forward")
                                                .withShortDescription("fakeplayer.command.move.forward.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(moveCommand.move(1, 0)),
                                        command("backward")
                                                .withShortDescription("fakeplayer.command.move.backward.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(moveCommand.move(-1, 0)),
                                        command("left")
                                                .withShortDescription("fakeplayer.command.move.left.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(moveCommand.move(0, 1)),
                                        command("right")
                                                .withShortDescription("fakeplayer.command.move.right.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(moveCommand.move(0, -1))
                                )
                                .executes(moveCommand.move(1, 0)),

                        command("ride")
                                .withShortDescription("fakeplayer.command.ride.description")
                                .withPermission(Permission.ride)
                                .withRequirement(CommandSupports::hasTarget)
                                .withSubcommands(
                                        command("me")
                                                .withShortDescription("fakeplayer.command.ride.me.description")
                                                .withOptionalArguments(target("name"))
                                                .executesPlayer(rideCommand::rideMe),
                                        command("target")
                                                .withShortDescription("fakeplayer.command.ride.target.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rideCommand::rideTarget),
                                        command("anything")
                                                .withShortDescription("fakeplayer.command.ride.anything.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rideCommand::rideAnything),
                                        command("vehicle")
                                                .withShortDescription("fakeplayer.command.ride.vehicle.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rideCommand::rideVehicle),
                                        command("stop")
                                                .withShortDescription("fakeplayer.command.ride.stop.description")
                                                .withOptionalArguments(target("name"))
                                                .executes(rideCommand::stopRiding)
                                ),
                        command("swap")
                                .withShortDescription("fakeplayer.command.swap.description")
                                .withPermission(Permission.swap)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executes(swapCommand::swap),
                        command("sleep")
                                .withShortDescription("fakeplayer.command.sleep.description")
                                .withPermission(Permission.sleep)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name", p -> !p.isSleeping()))
                                .executes(sleepCommand::sleep),
                        command("wakeup")
                                .withShortDescription("fakeplayer.command.wakeup.description")
                                .withPermission(Permission.wakeup)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name", LivingEntity::isSleeping))
                                .executes(sleepCommand::wakeup),
                        command("stop")
                                .withShortDescription("fakeplayer.command.stop.description")
                                .withPermission(Permission.stop)
                                .withRequirement(CommandSupports::hasTarget)
                                .withOptionalArguments(target("name"))
                                .executes(stopCommand::stop),

                        command("cmd")
                                .withShortDescription("fakeplayer.command.cmd.description")
                                .withRequirement(CommandSupports::isCmdAvailable)
                                .withArguments(
                                        target("name"),
                                        new FakePlayerCommandArgument("command")
                                )
                                .executes(cmdCommand::cmd),

                        command("killall")
                                .withShortDescription("fakeplayer.command.killall.description")
                                .withPermission(CommandPermission.OP)
                                .executes(killallCommand::killall),
                        command("reload")
                                .withShortDescription("fakeplayer.command.reload.description")
                                .withPermission(CommandPermission.OP)
                                .executes(reloadCommand::reload),
                        command("reload-translation")
                                .withShortDescription("fakeplayer.command.reload-translation.description")
                                .withPermission(CommandPermission.OP)
                                .executes(reloadCommand::reloadTranslation),

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

                );
        HelpCommand.generateHelpCommand(root, true);
        root.register();
    }


}
