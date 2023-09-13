package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.manager.action.Action;
import io.github.hello09x.fakeplayer.manager.action.ActionSetting;
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

import static io.github.hello09x.bedrock.command.Commands.*;


@SuppressWarnings("SameParameterValue")
public class CommandRegistry {

    public static void register() {
        command("fakeplayer")
                .withAliases("fp")
                .withHelp(
                        "假人",
                        "可以创建模拟玩家的假人, 能保持附近区块的刷新、触发怪物生成。同时还提供了一些操作命令让你控制假人的物品、动作等等。"
                )
                .withUsage(
                        usage("spawn [名称] [世界] [坐标]", "创建假人"),
                        usage("kill", "移除假人"),
                        usage("list [页码] [数量]", "查看所有假人"),
                        usage("distance", "查看与假人的距离"),
                        usage("tp", "传送到假人身边"),
                        usage("tphere", "将假人传送到身边"),
                        usage("tps", "与假人交换位置"),
                        usage("config get <配置项>", "查看配置项"),
                        usage("config set <配置项> <值>", "设置配置项"),
                        usage("health", "查看生命值"),
                        usage("exp", "查看经验值"),
                        usage("expme", "转移经验值"),
                        usage("attack (once | continuous | interval | stop)", "攻击/破坏"),
                        usage("use (once | continuous | interval | stop)", "使用/交互/放置"),
                        usage("jump (once | continuous | interval | stop)", "跳"),
                        usage("drop [-a|--all]", "丢弃手上物品"),
                        usage("dropinv", "丢弃背包物品"),
                        usage("look (north | south | east | west | up | down | at | entity)", "看向指定位置"),
                        usage("turn (left | right | back | to)", "转身"),
                        usage("move (forward | backward | left |right)", "移动"),
                        usage("cmd", "执行命令"),
                        usage("reload", "重新加载配置文件")
                )
                .withSubcommands(
                        command("help")
                                .withAliases("?")
                                .withOptionalArguments(int32("page", 1))
                                .executesPlayer(HelpCommand.instance::help),

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
                                .withSubcommands(newActionCommands(Action.ATTACK)),
                        command("use")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(Action.USE)),
                        command("jump")
                                .withPermission(Permission.action)
                                .withSubcommands(newActionCommands(Action.JUMP)),
                        command("drop")
                                .withPermission(Permission.action)
                                .withOptionalArguments(
                                        fakeplayer("target"),
                                        literals("all", List.of("-a", "--all")))
                                .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(
                                        sender,
                                        args,
                                        args.getOptional("all").isPresent() ? Action.DROP_STACK : Action.DROP_ITEM,
                                        ActionSetting.once())),
                        command("dropinv")
                                .withPermission(Permission.action)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ActionCommand.instance.action(Action.DROP_INVENTORY, ActionSetting.once())),
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
                                                .withAliases("n")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.NORTH)),
                                        command("south")
                                                .withAliases("s")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.SOUTH)),
                                        command("west")
                                                .withAliases("w")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.WEST)),
                                        command("east")
                                                .withAliases("e")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.EAST)),
                                        command("up")
                                                .withAliases("u")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.UP)),
                                        command("down")
                                                .withAliases("d")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.look(Direction.DOWN)),
                                        command("at")
                                                .withArguments(location("location"))
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance::lookAt),
                                        command("entity")
                                                .withSubcommands(newActionCommands(Action.LOOK_AT_NEAREST_ENTITY))
                                ),
                        command("turn")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        command("left")
                                                .withAliases("l")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(-90, 0)),
                                        command("right")
                                                .withAliases("r")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(90, 0)),
                                        command("back")
                                                .withAliases("b")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.turn(180, 0)),
                                        command("to")
                                                .withArguments(rotation("rotation"))
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance::turnTo)
                                ),
                        command("move")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        command("forward")
                                                .withAliases("f")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(1, 0)),
                                        command("backward")
                                                .withAliases("b")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(-1, 0)),
                                        command("left")
                                                .withAliases("l")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(0, 1)),
                                        command("right")
                                                .withAliases("r")
                                                .withOptionalArguments(fakeplayer("target"))
                                                .executes(ActionCommand.instance.move(0, -1))
                                ),

                        command("expme")
                                .withPermission(Permission.exp)
                                .withOptionalArguments(fakeplayer("target"))
                                .executesPlayer(ExpCommand.instance::expme),

                        command("cmd")
                                .withPermission(Permission.cmd)
                                .withArguments(
                                        fakeplayer("target"),
                                        cmd("command"))
                                .executes(CmdCommand.instance::cmd),

                        command("reload")
                                .withPermission(Permission.admin)
                                .executes(ReloadCommand.instance::reload)

                ).register();
    }

    private static CommandAPICommand[] newActionCommands(@NotNull Action action) {
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
