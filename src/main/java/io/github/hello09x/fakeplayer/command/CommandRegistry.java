package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.bedrock.command.Usage;
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
                        "输入 /fp ? 查看命令帮助"
                )
                .withSubcommands(
                        helpCommand("/fp",
                                Usage.of("spawn [名称] [世界] [坐标]", "创建假人", Permission.spawn),
                                Usage.of("kill", "移除假人", Permission.spawn),
                                Usage.of("list [页码] [数量]", "查看所有假人", Permission.spawn),
                                Usage.of("distance", "查看与假人的距离", Permission.spawn),
                                Usage.of("drop [-a|--all]", "丢弃手上物品", Permission.spawn),
                                Usage.of("dropinv", "丢弃背包物品", Permission.spawn),
                                Usage.of("tp", "传送到假人身边", Permission.tp),
                                Usage.of("tphere", "将假人传送到身边", Permission.tp),
                                Usage.of("tps", "与假人交换位置", Permission.tp),
                                Usage.of("config get <配置项>", "设置个性化配置"),
                                Usage.of("config set <配置项>", "查看个性化配置"),
                                Usage.of("health", "查看生命值", Permission.profile),
                                Usage.of("exp", "查看经验值", Permission.profile),
                                Usage.of("expme", "转移经验值(补魔)", Permission.exp),
                                Usage.of("attack (once | continuous | interval | stop)", "攻击/破坏", Permission.action),
                                Usage.of("use (once | continuous | interval | stop)", "交互/放置", Permission.action),
                                Usage.of("jump (once | continuous | interval | stop)", "跳", Permission.action),
                                Usage.of("look (north | south | east | west | up | down | at | entity)", "看向指定位置", Permission.action),
                                Usage.of("turn (left | right | back | to)", "转身", Permission.action),
                                Usage.of("move (forward | backward | left | right)", "移动", Permission.action),
                                Usage.of("ride (me | anything | stop)", "骑", Permission.action),
                                Usage.of("sneak [true | false]", "潜行", Permission.action),
                                Usage.of("cmd <命令>", "执行命令", Permission.cmd),
                                Usage.of("reload", "重新加载配置文件", Permission.admin)
                        ),

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
                        command("drop")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(
                                        fakeplayer("target"),
                                        literals("all", List.of("-a", "--all")))
                                .executes((CommandExecutor) (sender, args) -> ActionCommand.instance.action(
                                        sender,
                                        args,
                                        args.getOptional("all").isPresent() ? Action.DROP_STACK : Action.DROP_ITEM,
                                        ActionSetting.once())),
                        command("dropinv")
                                .withPermission(Permission.spawn)
                                .withOptionalArguments(fakeplayer("target"))
                                .executes(ActionCommand.instance.action(Action.DROP_INVENTORY, ActionSetting.once())),

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

                        command("ride")
                                .withPermission(Permission.action)
                                .withSubcommands(
                                        command("me")
                                                .executesPlayer(RideCommand.instance::rideMe),
                                        command("anything")
                                                .executes(RideCommand.instance::rideAnything),
                                        command("normal")
                                                .executes(RideCommand.instance::rideNormal),
                                        command("stop")
                                                .executes(RideCommand.instance::stopRiding)
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
