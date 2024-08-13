package io.github.hello09x.fakeplayer.core.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import io.github.hello09x.devtools.command.HelpCommand;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.command.impl.*;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.repository.model.FeatureKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;

import static io.github.hello09x.devtools.command.Commands.*;
import static io.github.hello09x.fakeplayer.core.command.CommandSupports.*;
import static net.kyori.adventure.text.Component.translatable;


@Singleton
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
    private SprintCommand sprintCommand;
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
    private FakeplayerConfig config;

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
                                .withOptionalArguments(fakeplayer("name"))
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
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayers("names"))
                                .executes(killCommand::kill),
                        command("list")
                                .withPermission(Permission.list)
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withShortDescription("fakeplayer.command.list.description")
                                .withOptionalArguments(
                                        int32("page", 1),
                                        int32("size", 1))
                                .executes(listCommand::list),
                        command("distance")
                                .withPermission(Permission.distance)
                                .withShortDescription("fakeplayer.command.distance.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(distanceCommand::distance),
                        command("skin")
                                .withPermission(Permission.skin)
                                .withShortDescription("fakeplayer.command.skin.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withArguments(offlinePlayer("player"))
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(skinCommand::skin),
                        command("invsee")
                                .withPermission(Permission.invsee)
                                .withShortDescription("fakeplayer.command.invsee.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(invseeCommand::invsee),
                        command("hold")
                                .withPermission(Permission.hold)
                                .withShortDescription("fakeplayer.command.hold.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withArguments(int32("slot", 1, 9))
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(holdCommand::hold),
                        command("status")
                                .withPermission(Permission.status)
                                .withShortDescription("fakeplayer.command.status.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(statusCommand::status),
                        command("respawn")
                                .withRequirement(CommandSupports::hasFakeplayerForRespawn)
                                .withShortDescription("fakeplayer.command.respawn.description")
                                .withPermission(Permission.respawn)
                                .withOptionalArguments(fakeplayer("name", Entity::isDead))
                                .executes(respawnCommand::respawn),
                        command("set")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withShortDescription("fakeplayer.command.set.description")
                                .withPermission(Permission.set)
                                .withArguments(
                                        configKey("feature", FeatureKey::hasModifier),
                                        configValue("feature", "option")
                                )
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(setCommand::set),
                        command("config")
                                .withPermission(Permission.config)
                                .withShortDescription("fakeplayer.command.config.description")
                                .withSubcommands(
                                        command("set")
                                                .withArguments(
                                                        configKey("feature"),
                                                        configValue("feature", "option"))
                                                .executesPlayer(configCommand::setConfig),
                                        command("list")
                                                .executesPlayer(configCommand::listConfig)
                                )
                                .executesPlayer(configCommand::listConfig),

                        command("expme")
                                .withPermission(Permission.expme)
                                .withShortDescription("fakeplayer.command.expme.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(expmeCommand::expme),

                        command("tp")
                                .withAliases("tpto")
                                .withPermission(Permission.tp)
                                .withShortDescription("fakeplayer.command.tp.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(teleportCommand::tp),
                        command("tphere")
                                .withPermission(Permission.tphere)
                                .withShortDescription("fakeplayer.command.tphere.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(teleportCommand::tphere),
                        command("tps")
                                .withPermission(Permission.tps)
                                .withShortDescription("fakeplayer.command.tphere.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executesPlayer(teleportCommand::tps),

                        command("attack")
                                .withPermission(Permission.attack)
                                .withShortDescription("fakeplayer.command.attack.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(newActionCommands(ActionType.ATTACK))
                                .executes(actionCommand.action(ActionType.ATTACK, ActionSetting.once())),
                        command("mine")
                                .withPermission(Permission.mine)
                                .withShortDescription("fakeplayer.command.mine.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(newActionCommands(ActionType.MINE))
                                .executes(actionCommand.action(ActionType.MINE, ActionSetting.once())),
                        command("use")
                                .withPermission(Permission.use)
                                .withShortDescription("fakeplayer.command.use.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(newActionCommands(ActionType.USE))
                                .executes(actionCommand.action(ActionType.USE, ActionSetting.once())),
                        command("jump")
                                .withPermission(Permission.jump)
                                .withShortDescription("fakeplayer.command.jump.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(newActionCommands(ActionType.JUMP))
                                .executes(actionCommand.action(ActionType.JUMP, ActionSetting.once())),
                        command("drop")
                                .withPermission(Permission.drop)
                                .withShortDescription("fakeplayer.command.drop.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(newActionCommands(ActionType.DROP_ITEM))
                                .executes(actionCommand.action(ActionType.DROP_ITEM, ActionSetting.once())),
                        command("dropstack")
                                .withPermission(Permission.dropstack)
                                .withShortDescription("fakeplayer.command.dropstack.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(newActionCommands(ActionType.DROP_STACK))
                                .executes(actionCommand.action(ActionType.DROP_STACK, ActionSetting.once())),
                        command("dropinv")
                                .withPermission(Permission.dropinv)
                                .withShortDescription("fakeplayer.command.dropinv.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(newActionCommands(ActionType.DROP_INVENTORY))
                                .executes(actionCommand.action(ActionType.DROP_INVENTORY, ActionSetting.once())),
                        command("sneak")
                                .withPermission(Permission.sneak)
                                .withShortDescription("fakeplayer.command.sneak.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(
                                        fakeplayer("name"),
                                        literals("sneaking", List.of("true", "false")))
                                .executes(sneakCommand::sneak),
                        command("sprint")
                                .withPermission(Permission.sprint)
                                .withShortDescription("fakeplayer.command.sprint.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(
                                        fakeplayer("name"),
                                        literals("sprinting", List.of("true", "false")))
                                .executes(sprintCommand::sprint),
                        command("look")
                                .withPermission(Permission.look)
                                .withShortDescription("fakeplayer.command.look.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(
                                        command("north")
                                                .withShortDescription("fakeplayer.command.look.north.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.look(Direction.NORTH)),
                                        command("south")
                                                .withShortDescription("fakeplayer.command.look.south.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.look(Direction.SOUTH)),
                                        command("west")
                                                .withShortDescription("fakeplayer.command.look.west.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.look(Direction.WEST)),
                                        command("east")
                                                .withShortDescription("fakeplayer.command.look.east.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.look(Direction.EAST)),
                                        command("up")
                                                .withShortDescription("fakeplayer.command.look.up.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.look(Direction.UP)),
                                        command("down")
                                                .withShortDescription("fakeplayer.command.look.down.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.look(Direction.DOWN)),
                                        command("at")
                                                .withShortDescription("fakeplayer.command.look.at.description")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand::lookAt),
                                        command("me")
                                                .withShortDescription("fakeplayer.command.look.me.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executesPlayer(rotationCommand::lookMe),
                                        command("entity")
                                                .withShortDescription("fakeplayer.command.look.entity.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .withSubcommands(newActionCommands(ActionType.LOOK_AT_NEAREST_ENTITY))
                                ),
                        command("turn")
                                .withPermission(Permission.turn)
                                .withShortDescription("fakeplayer.command.turn.description")
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(
                                        command("left")
                                                .withShortDescription("fakeplayer.command.turn.left.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.turn(-90, 0)),
                                        command("right")
                                                .withShortDescription("fakeplayer.command.turn.right.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.turn(90, 0)),
                                        command("back")
                                                .withShortDescription("fakeplayer.command.turn.back.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand.turn(180, 0)),
                                        command("to")
                                                .withShortDescription("fakeplayer.command.turn.to.description")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rotationCommand::turnTo)
                                ),
                        command("move")
                                .withShortDescription("fakeplayer.command.move.description")
                                .withPermission(Permission.move)
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(
                                        command("forward")
                                                .withShortDescription("fakeplayer.command.move.forward.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(moveCommand.move(1, 0)),
                                        command("backward")
                                                .withShortDescription("fakeplayer.command.move.backward.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(moveCommand.move(-1, 0)),
                                        command("left")
                                                .withShortDescription("fakeplayer.command.move.left.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(moveCommand.move(0, 1)),
                                        command("right")
                                                .withShortDescription("fakeplayer.command.move.right.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(moveCommand.move(0, -1))
                                )
                                .executes(moveCommand.move(1, 0)),

                        command("ride")
                                .withShortDescription("fakeplayer.command.ride.description")
                                .withPermission(Permission.ride)
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withSubcommands(
                                        command("me")
                                                .withShortDescription("fakeplayer.command.ride.me.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executesPlayer(rideCommand::rideMe),
                                        command("target")
                                                .withShortDescription("fakeplayer.command.ride.target.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rideCommand::rideTarget),
                                        command("anything")
                                                .withShortDescription("fakeplayer.command.ride.anything.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rideCommand::rideAnything),
                                        command("vehicle")
                                                .withShortDescription("fakeplayer.command.ride.vehicle.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rideCommand::rideVehicle),
                                        command("entity")
                                                .withShortDescription("fakeplayer.command.ride.entity.description")
                                                .withArguments(new EntitySelectorArgument.OneEntity("entity"))
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rideCommand::rideEntity),
                                        command("stop")
                                                .withShortDescription("fakeplayer.command.ride.stop.description")
                                                .withOptionalArguments(fakeplayer("name"))
                                                .executes(rideCommand::stopRiding)
                                ),
                        command("swap")
                                .withShortDescription("fakeplayer.command.swap.description")
                                .withPermission(Permission.swap)
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(swapCommand::swap),
                        command("sleep")
                                .withShortDescription("fakeplayer.command.sleep.description")
                                .withPermission(Permission.sleep)
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name", p -> !p.isSleeping()))
                                .executes(sleepCommand::sleep),
                        command("wakeup")
                                .withShortDescription("fakeplayer.command.wakeup.description")
                                .withPermission(Permission.wakeup)
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name", LivingEntity::isSleeping))
                                .executes(sleepCommand::wakeup),
                        command("stop")
                                .withShortDescription("fakeplayer.command.stop.description")
                                .withPermission(Permission.stop)
                                .withRequirement(CommandSupports::hasFakeplayer)
                                .withOptionalArguments(fakeplayer("name"))
                                .executes(stopCommand::stop),

                        command("cmd")
                                .withShortDescription("fakeplayer.command.cmd.description")
                                .withRequirement(CommandSupports::isCmdAvailable)
                                .withArguments(
                                        fakeplayer("name"),
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
