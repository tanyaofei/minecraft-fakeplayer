package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.hello09x.fakeplayer.command.AbstractCommand.multiTargetArgument;
import static io.github.hello09x.fakeplayer.command.AbstractCommand.targetArgument;

public class Commands {

    private final static String PERMISSION_SPAWN = "fakeplayer.spawn";
    private final static String PERMISSION_PROFILE = "fakeplayer.profile";
    private final static String PERMISSION_TP = "fakeplayer.tp";
    private final static String PERMISSION_EXP = "fakeplayer.exp";
    private final static String PERMISSION_ACTION = "fakeplayer.action";
    private final static String PERMISSION_EXPERIMENTAL_ACTION = "fakeplayer.experimental.action";
    private final static String PERMISSION_ADMIN = "fakeplayer.admin";

    public static void register() {
        new CommandAPICommand("fakeplayer")
                .withAliases("fp")
                .withHelp(
                        "假人相关命令",
                        "fakeplayer 可以用来创建一个模拟为玩家的假人, 能保持附近区块的刷新、触发怪物生成。同时还提供了一些操作命令让你控制假人的物品、动作等等。"
                )
                .withUsage(
                        "§6/fp create §7- §f创建假人",
                        "§6/fp kill [假人] §7- §f移除假人",
                        "§6/fp list [页码] [数量] §7- §f查看所有假人",
                        "§6/fp tp [假人] §7- §f传送到假人身边",
                        "§6/fp tphere [假人] §7- §f将假人传送到身边",
                        "§6/fp tps [假人] §7- §f与假人交换位置",
                        "§6/fp config get <配置项> §7- §f查看配置项",
                        "§6/fp config set <配置项> <配置值> §7- §f设置配置项",
                        "§6/fp health [假人] §7- §f查看生命值",
                        "§6/fp exp [假人] §7- §f查看经验值",
                        "§6/fp expme [假人] §7- §f转移经验值",
                        "§6/fp drop [假人] [-a|--all] §7- §f丢弃手上物品",
                        "§6/fp dropinv [假人] §7- §f丢弃背包物品",
                        "§6/fp sneak [假人] §7- §f开启/取消潜行",
                        "§6/fp attack <once|continuous|interval|stop> [假人] §7- §f鼠标左键",
                        "§6/fp use <once|continuous|interval|stop> [假人] §7- §f鼠标右键"
                )
                .withSubcommands(
                        new CommandAPICommand("help")
                                .withAliases("?")
                                .withOptionalArguments(new IntegerArgument("page", 1))
                                .executesPlayer(HelpCommand.instance::help),

                        new CommandAPICommand("create")
                                .withPermission(PERMISSION_SPAWN)
                                .executesPlayer(SpawnCommand.instance::create),
                        new CommandAPICommand("kill")
                                .withPermission(PERMISSION_SPAWN)
                                .withOptionalArguments(multiTargetArgument("targets"))
                                .executes(SpawnCommand.instance::kill),
                        new CommandAPICommand("list")
                                .withPermission(PERMISSION_SPAWN)
                                .withOptionalArguments(new IntegerArgument("page", 1), new IntegerArgument("size", 1))
                                .executes(SpawnCommand.instance::list),

                        new CommandAPICommand("exp")
                                .withPermission(PERMISSION_PROFILE)
                                .withOptionalArguments(targetArgument("target"))
                                .executes(ProfileCommand.instance::exp),
                        new CommandAPICommand("health")
                                .withPermission(PERMISSION_PROFILE)
                                .withOptionalArguments(targetArgument("target"))
                                .executes(ProfileCommand.instance::health),

                        new CommandAPICommand("tp")
                                .withPermission(PERMISSION_TP)
                                .withOptionalArguments(targetArgument("target"))
                                .executesPlayer(TpCommand.instance::tp),
                        new CommandAPICommand("tphere")
                                .withPermission(PERMISSION_TP)
                                .withOptionalArguments(targetArgument("target"))
                                .executesPlayer(TpCommand.instance::tphere),
                        new CommandAPICommand("tps")
                                .withPermission(PERMISSION_TP)
                                .withOptionalArguments(targetArgument("target"))
                                .executesPlayer(TpCommand.instance::tps),

                        new CommandAPICommand("config")
                                .withSubcommands(
                                        new CommandAPICommand("get")
                                                .withArguments(ConfigCommand.configArgument("config"))
                                                .executesPlayer(ConfigCommand.instance::getConfig),
                                        new CommandAPICommand("set")
                                                .withArguments(
                                                        ConfigCommand.configArgument("config"),
                                                        ConfigCommand.configValueArgument("config", "value")
                                                )
                                                .executesPlayer(ConfigCommand.instance::setConfig)
                                ),
                        new CommandAPICommand("attack")
                                .withPermission(PERMISSION_EXPERIMENTAL_ACTION)
                                .withOptionalArguments(targetArgument("target"))
                                .withSubcommands(buildActionCommand(Action.ATTACK)),
                        new CommandAPICommand("use")
                                .withPermission(PERMISSION_EXPERIMENTAL_ACTION)
                                .withOptionalArguments(targetArgument("target"))
                                .withSubcommands(buildActionCommand(Action.USE)),
                        new CommandAPICommand("drop")
                                .withPermission(PERMISSION_ACTION)
                                .withOptionalArguments(
                                        targetArgument("target"),
                                        new MultiLiteralArgument("all", List.of("-a", "--all"))
                                )
                                .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(
                                        sender,
                                        args,
                                        args.getOptional("all").isPresent() ? Action.DROP_STACK : Action.DROP_ITEM,
                                        ActionSetting.once())),
                        new CommandAPICommand("dropinv")
                                .withPermission(PERMISSION_ACTION)
                                .withOptionalArguments(targetArgument("target"))
                                .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(sender, args, Action.DROP_INVENTORY, ActionSetting.once())),
                        new CommandAPICommand("sneak")
                                .withPermission(PERMISSION_ACTION)
                                .withOptionalArguments(targetArgument("target"))
                                .executes(ActionCommand.instance::sneak),

                        new CommandAPICommand("expme")
                                .withPermission(PERMISSION_EXP)
                                .withOptionalArguments(targetArgument("target"))
                                .executesPlayer(ExpCommand.instance::expme),

                        new CommandAPICommand("reload")
                                .withPermission(PERMISSION_ADMIN)
                                .executes(ReloadCommand.instance::reload)

                ).register();
    }

    private static CommandAPICommand[] buildActionCommand(@NotNull Action action) {
        return new CommandAPICommand[]{
                new CommandAPICommand("once")
                        .withOptionalArguments(targetArgument("target"))
                        .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(sender, args, action, ActionSetting.once())
                ),
                new CommandAPICommand("continuous")
                        .withOptionalArguments(targetArgument("target"))
                        .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(sender, args, action, ActionSetting.continuous())
                ),
                new CommandAPICommand("stop")
                        .withOptionalArguments(targetArgument("target"))
                        .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(sender, args, action, ActionSetting.stop())
                ),
                new CommandAPICommand("interval")
                        .withOptionalArguments(
                                new IntegerArgument("interval", 1),
                                targetArgument("target")
                        )
                        .executes((sender, args) -> {
                    int interval = (int) args.getOptional("interval").orElse(1);
                    ActionCommand.instance.action(sender, args, action, ActionSetting.interval(interval));
                })
        };
    }


}
