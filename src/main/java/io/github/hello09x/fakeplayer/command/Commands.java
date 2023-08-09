package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.repository.model.Config;
import io.github.hello09x.fakeplayer.repository.model.Configs;
import net.minecraft.core.Direction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@SuppressWarnings("SameParameterValue")
public class Commands {

    public static void register() {
        newCommand("fakeplayer")
                .withAliases("fp")
                .withHelp(
                        "假人",
                        "可以创建模拟玩家的假人, 能保持附近区块的刷新、触发怪物生成。同时还提供了一些操作命令让你控制假人的物品、动作等等。"
                )
                .withUsage(
                        "§6/fp ? [页码] §7- §f查看帮助",
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
                        "§6/fp attack (once | continuous | interval | stop) [假人] §7- §f攻击/破坏",
                        "§6/fp use (once | continuous | interval | stop) [假人] §7- §f使用/交互/放置",
                        "§6/fp jump (once | continuous | interval | stop) [假人] §7- §f跳跃",
                        "§6/fp drop [假人] [-a|--all] §7- §f丢弃手上物品",
                        "§6/fp dropinv [假人] §7- §f丢弃背包物品",
                        "§6/fp look (north | south | east | west | up | down | at) [假人] §7- §f看向指定位置",
                        "§6/fp turn (left | right | back | to) [假人] §7- §f转身到指定位置",
                        "§6/fp move (forward | backward | left | right) [假人] §7- §f移动",
                        "§6/fp cmd <假人> <命令> §7- §f执行命令",
                        "§6/fp reload §7- §f重载配置文件"
                )
                .withSubcommands(
                        newCommand("help")
                                .withAliases("?")
                                .withOptionalArguments(integer("page", 1))
                                .executesPlayer(HelpCommand.instance::help),

                        newCommand("spawn")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        text("name").withPermission(Permission.spawnName),
                                        world("world").withPermission(Permission.spawnLocation),
                                        location("location").withPermission(Permission.spawnLocation))
                                .executes(SpawnCommand.instance::spawn),
                        newCommand("kill")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(fakeplayers("targets"))
                                .executes(SpawnCommand.instance::kill),
                        newCommand("list")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        integer("page", 1),
                                        integer("size", 1))
                                .executes(SpawnCommand.instance::list),
                        newCommand("distance")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(SpawnCommand.instance::distance),

                        newCommand("exp")
                                .withPermission(Permission.profile)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ProfileCommand.instance::exp),
                        newCommand("health")
                                .withPermission(Permission.profile)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ProfileCommand.instance::health),

                        newCommand("tp")
                                .withPermission(Permission.tp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(TpCommand.instance::tp),
                        newCommand("tphere")
                                .withPermission(Permission.tp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(TpCommand.instance::tphere),
                        newCommand("tps")
                                .withPermission(Permission.tp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(TpCommand.instance::tps),

                        newCommand("config")
                                .withSubcommands(
                                        newCommand("get")
                                                .withArguments(config("config"))
                                                .executesPlayer(ConfigCommand.instance::getConfig),
                                        newCommand("set")
                                                .withArguments(
                                                        config("config"),
                                                        configValue("config", "value"))
                                                .executesPlayer(ConfigCommand.instance::setConfig)
                                ),
                        newCommand("attack")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(Action.ATTACK)),
                        newCommand("use")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(Action.USE)),
                        newCommand("jump")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(Action.JUMP)),
                        newCommand("drop")
                                .withPermission(Permission.action)
                                .withOptionalArguments(
                                        fakeplayer("target"),
                                        literals("all", "-a", "--all"))
                                .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(
                                        sender,
                                        args,
                                        args.getOptional("all").isPresent() ? Action.DROP_STACK : Action.DROP_ITEM,
                                        ActionSetting.once())),
                        newCommand("dropinv")
                                .withPermission(Permission.action)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ActionCommand.instance.action(Action.DROP_INVENTORY, ActionSetting.once())),
                        newCommand("sneak")
                                .withPermission(Permission.action)
                                .withOptionalArguments(
                                        literals("sneaking", "true", "false"),
                                        fakeplayer("target"))
                                .executes(ActionCommand.instance::sneak),
                        newCommand("look")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        newCommand("north")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.NORTH)),
                                        newCommand("south")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.SOUTH)),
                                        newCommand("west")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.WEST)),
                                        newCommand("east")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.EAST)),
                                        newCommand("up")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.UP)),
                                        newCommand("down")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.DOWN)),
                                        newCommand("at")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance::lookAt)
                                ),
                        newCommand("turn")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        newCommand("left")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(-90, 0)),
                                        newCommand("right")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(90, 0)),
                                        newCommand("back")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(180, 0)),
                                        newCommand("to")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance::turnTo)
                                ),
                        newCommand("move")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        newCommand("forward")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(1, 0)),
                                        newCommand("backward")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(-1, 0)),
                                        newCommand("left")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(0, 1)),
                                        newCommand("right")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(0, -1))
                                ),

                        newCommand("expme")
                                .withPermission(Permission.exp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(ExpCommand.instance::expme),

                        newCommand("cmd")
                                .withPermission(Permission.cmd)
                                .withArguments(
                                        fakeplayer("target"),
                                        command("command"))
                                .executes(CmdCommand.instance::cmd),

                        newCommand("reload")
                                .withPermission(Permission.admin)
                                .executes(ReloadCommand.instance::reload)

                ).register();
    }

    private static CommandAPICommand[] newActionCommands(@NotNull Action action) {
        return new CommandAPICommand[]{
                newCommand("once")
                        .withOptionalArguments(fakeplayer("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.once())),
                newCommand("continuous")
                        .withOptionalArguments(fakeplayer("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.continuous())),
                newCommand("stop")
                        .withOptionalArguments(fakeplayer("target"))
                        .executes(ActionCommand.instance.action(action, ActionSetting.stop())),
                newCommand("interval")
                        .withOptionalArguments(
                                integer("interval", 1),
                                fakeplayer("target"))
                        .executes((sender, args) -> {
                    int interval = (int) args.getOptional("interval").orElse(1);
                    ActionCommand.instance.action(sender, args, action, ActionSetting.interval(interval));
                })
        };
    }

    private static CommandAPICommand newCommand(@NotNull String name) {
        return new CommandAPICommand(name);
    }

    private static IntegerArgument integer(@NotNull String nodeName, int min) {
        return new IntegerArgument(nodeName, min);
    }

    private static LocationArgument location(@NotNull String nodeName) {
        return new LocationArgument(nodeName);
    }

    private static RotationArgument rotation(@NotNull String nodeName) {
        return new RotationArgument(nodeName);
    }

    private static WorldArgument world(@NotNull String nodeName) {
        return new WorldArgument(nodeName);
    }

    private static MultiLiteralArgument literals(@NotNull String nodeName, @NotNull String @NotNull ... literals) {
        return new MultiLiteralArgument(nodeName, Arrays.asList(literals));
    }

    private static TextArgument text(@NotNull String nodeName) {
        return new TextArgument(nodeName);
    }

    private static CommandArgument command(@NotNull String nodeName) {
        return new CommandArgument(nodeName);
    }

    private static Argument<Player> fakeplayer(@NotNull String nodeName) {
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

    private static Argument<List<Player>> fakeplayers(@NotNull String nodeName) {
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

    private static Argument<Config<Object>> config(@NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var arg = info.currentInput();
            try {
                return Configs.valueOf(arg);
            } catch (Exception e) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("未知的配置项: ").appendArgInput());
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Configs.values()).map(Config::name).toList()));
    }

    private static Argument<Object> configValue(@NotNull String configNodeName, @NotNull String nodeName) {
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
