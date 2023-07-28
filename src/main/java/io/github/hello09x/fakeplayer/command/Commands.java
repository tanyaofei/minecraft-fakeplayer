package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static io.github.hello09x.fakeplayer.command.AbstractCommand.target;
import static io.github.hello09x.fakeplayer.command.AbstractCommand.targets;
import static io.github.hello09x.fakeplayer.command.ConfigCommand.config;
import static io.github.hello09x.fakeplayer.command.ConfigCommand.configValue;

public class Commands {

    private final static String PERMISSION_SPAWN = "fakeplayer.spawn";
    private final static String PERMISSION_SPAWN_LOCATION = "fakeplayer.spawn.location";
    private final static String PERMISSION_PROFILE = "fakeplayer.profile";
    private final static String PERMISSION_TP = "fakeplayer.tp";
    private final static String PERMISSION_EXP = "fakeplayer.exp";
    private final static String PERMISSION_ACTION = "fakeplayer.action";
    private final static String PERMISSION_EXPERIMENTAL_ACTION = "fakeplayer.experimental.action";
    private final static String PERMISSION_ADMIN = "fakeplayer.admin";
    private final static String PERMISSION_CMD = "fakeplayer.cmd";

    public static void register() {
        command("fakeplayer")
                .withAliases("fp")
                .withHelp(
                        "假人",
                        "可以创建模拟玩家的假人, 能保持附近区块的刷新、触发怪物生成。同时还提供了一些操作命令让你控制假人的物品、动作等等。"
                )
                .withUsage(
                        "§6/fp spawn [世界] [位置] §7- §f创建假人",
                        "§6/fp kill [假人] §7- §f移除假人",
                        "§6/fp list [页码] [数量] §7- §f查看所有假人",
                        "§6/fp distance §7- §f查看与假人的距离",
                        "§6/fp tp [假人] §7- §f传送到假人身边",
                        "§6/fp tphere [假人] §7- §f将假人传送到身边",
                        "§6/fp tps [假人] §7- §f与假人交换位置",
                        "§6/fp config get <配置项> §7- §f查看配置项",
                        "§6/fp config set <配置项> <配置值> §7- §f设置配置项",
                        "§6/fp health [假人] §7- §f查看生命值",
                        "§6/fp exp [假人] §7- §f查看经验值",
                        "§6/fp expme [假人] §7- §f转移经验值",
                        "§6/fp attack (once|continuous|interval|stop) [假人] §7- §f攻击/破坏",
                        "§6/fp use (once|continuous|interval|stop) [假人] §7- §f使用/交互/放置",
                        "§6/fp jump (once|continuous|interval|stop) [假人] §7- §f跳跃",
                        "§6/fp drop [假人] [-a|--all] §7- §f丢弃手上物品",
                        "§6/fp dropinv [假人] §7- §f丢弃背包物品",
                        "§6/fp look (north|south|east|west|up|down|at) [假人] §7- §f看向指定位置",
                        "§6/fp turn (left|right|back|to) [假人] §7- §f转身到指定位置",
                        "§6/fp move (forward|backward|left|right) [假人] §7- §f移动假人",
                        "§6/fp cmd <假人> <命令> §7- §f执行命令",
                        "§6/fp reload §7- §f重载配置文件"
                )
                .withSubcommands(
                        command("help")
                                .withAliases("?")
                                .withOptionalArguments(new IntegerArgument("page", 1))
                                .executesPlayer(HelpCommand.instance::help),

                        command("spawn")
                                .withPermission(PERMISSION_SPAWN)
                                .withOptionalArguments(
                                        world("world").withPermission(PERMISSION_SPAWN_LOCATION),
                                        location("location").withPermission(PERMISSION_SPAWN_LOCATION)
                                )
                                .executes(SpawnCommand.instance::spawn),
                        command("kill")
                                .withPermission(PERMISSION_SPAWN)
                                .withOptionalArguments(targets("targets"))
                                .executes(SpawnCommand.instance::kill),
                        command("list")
                                .withPermission(PERMISSION_SPAWN)
                                .withOptionalArguments(integer("page", 1), integer("size", 1))
                                .executes(SpawnCommand.instance::list),
                        command("distance")
                                .withPermission(PERMISSION_SPAWN)
                                .withOptionalArguments(target("target"))
                                .executesPlayer(SpawnCommand.instance::distance),

                        command("exp")
                                .withPermission(PERMISSION_PROFILE)
                                .withOptionalArguments(target("target"))
                                .executes(ProfileCommand.instance::exp),
                        command("health")
                                .withPermission(PERMISSION_PROFILE)
                                .withOptionalArguments(target("target"))
                                .executes(ProfileCommand.instance::health),

                        command("tp")
                                .withPermission(PERMISSION_TP)
                                .withOptionalArguments(target("target"))
                                .executesPlayer(TpCommand.instance::tp),
                        command("tphere")
                                .withPermission(PERMISSION_TP)
                                .withOptionalArguments(target("target"))
                                .executesPlayer(TpCommand.instance::tphere),
                        command("tps")
                                .withPermission(PERMISSION_TP)
                                .withOptionalArguments(target("target"))
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
                                .withPermission(PERMISSION_EXPERIMENTAL_ACTION)
                                .withSubcommands(action(Action.ATTACK)),
                        command("use")
                                .withPermission(PERMISSION_EXPERIMENTAL_ACTION)
                                .withSubcommands(action(Action.USE)),
                        command("jump")
                                .withPermission(PERMISSION_ACTION)
                                .withSubcommands(action(Action.JUMP)),
                        command("drop")
                                .withPermission(PERMISSION_ACTION)
                                .withOptionalArguments(
                                        target("target"),
                                        literals("all", "-a", "--all"))
                                .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(
                                        sender,
                                        args,
                                        args.getOptional("all").isPresent() ? Action.DROP_STACK : Action.DROP_ITEM,
                                        ActionSetting.once())),
                        command("dropinv")
                                .withPermission(PERMISSION_ACTION)
                                .withOptionalArguments(target("target"))
                                .executes(ActionCommand.instance.action(Action.DROP_INVENTORY, ActionSetting.once())),
                        command("sneak")
                                .withPermission(PERMISSION_ACTION)
                                .withOptionalArguments(target("target"))
                                .withOptionalArguments(literals("sneaking", "true", "false"))
                                .executes(ActionCommand.instance::sneak),
                        command("look")
                                .withPermission(PERMISSION_ACTION)
                                .withSubcommands(
                                        command("north")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.look(Direction.NORTH)),
                                        command("south")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.look(Direction.SOUTH)),
                                        command("west")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.look(Direction.WEST)),
                                        command("east")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.look(Direction.EAST)),
                                        command("up")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.look(Direction.UP)),
                                        command("down")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.look(Direction.DOWN)),
                                        command("at")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance::lookAt)
                                ),
                        command("turn")
                                .withPermission(PERMISSION_ACTION)
                                .withSubcommands(
                                        command("left")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.turn(-90, 0)),
                                        command("right")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.turn(90, 0)),
                                        command("back")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.turn(180, 0)),
                                        command("to")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance::turnTo)
                                )
                        ,
                        command("move")
                                .withPermission(PERMISSION_ACTION)
                                .withSubcommands(
                                        command("forward")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.move(1, 0)),
                                        command("backward")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.move(-1, 0)),
                                        command("left")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.move(0, 1)),
                                        command("right")
                                                .withOptionalArguments(target("target"))
                                                .executes(ActionCommand.instance.move(0, -1))
                                ),

                        command("expme")
                                .withPermission(PERMISSION_EXP)
                                .withOptionalArguments(target("target"))
                                .executesPlayer(ExpCommand.instance::expme),

                        command("cmd")
                                .withPermission(PERMISSION_CMD)
                                .withArguments(
                                        target("target"),
                                        new CommandArgument("command"))
                                .executes(CmdCommand.instance::cmd),

                        command("reload")
                                .withPermission(PERMISSION_ADMIN)
                                .executes(ReloadCommand.instance::reload)

                ).register();
    }

    private static CommandAPICommand[] action(@NotNull Action action) {
        return new CommandAPICommand[]{
                command("once")
                        .withOptionalArguments(target("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.once())
                ),
                command("continuous")
                        .withOptionalArguments(target("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.continuous())),
                command("stop")
                        .withOptionalArguments(target("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.stop())
                ),
                command("interval")
                        .withOptionalArguments(
                                integer("interval", 1),
                                target("target"))
                        .executes((sender, args) -> {
                    int interval = (int) args.getOptional("interval").orElse(1);
                    ActionCommand.instance.action(sender, args, action, ActionSetting.interval(interval));
                })
        };
    }

    private static CommandAPICommand command(@NotNull String name) {
        return new CommandAPICommand(name);
    }

    public static IntegerArgument integer(String name, int min) {
        return new IntegerArgument(name, min);
    }

    public static LocationArgument location(String name) {
        return new LocationArgument(name);
    }

    public static RotationArgument rotation(String name) {
        return new RotationArgument(name);
    }

    public static WorldArgument world(String name) {
        return new WorldArgument(name);
    }

    public static MultiLiteralArgument literals(String name, String... literals) {
        return new MultiLiteralArgument(name, Arrays.asList(literals));
    }


}
