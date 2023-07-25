package io.github.hello09x.fakeplayer.command.player.action;

import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import io.github.hello09x.fakeplayer.entity.action.PlayerActionManager;
import io.github.tanyaofei.plugin.toolkit.command.help.Helps;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class UseCommand extends AbstractActionCommand {


    public final static Component help = Helps.help(
            "控制假人点击鼠标右键",
            "与面前的实体/方块交互、使用手上的消耗品、防止手上的方块",
            new Helps.Content("用法", "/fp use [假人] [-n:次数] [-i:间隔] [-k|--keep] [-s|--stop]"),
            new Helps.Content("参数", List.of(
                    "-n:次数 - 指定次数, 只有成功攻击或破坏才计数",
                    "-i:间隔 - 指定间隔, 单位为 ticks",
                    "--keep - 永不停止",
                    "--stop - 立即停止"
            )),
            new Helps.Content("例子", List.of(
                    "/fp use hello09x -n:5 -i:20   - 点击每 20 ticks 触发鼠标右键, 共 5 次",
                    "/fp use hello09x -i:20 --keep - 永久(真的很久)地每 20 ticks 触发触发鼠标右键",
                    "/fp use hello09x --stop       - 取消触发鼠标右键"
            ))
    );

    public final static UseCommand instance = new UseCommand(
            "控制假人点击鼠标右键",
            "/fp use [假人] [-n:次数] [-i:间隔] [--keep] [--stop]",
            "fakeplayer.experimental.action"
    );
    private final PlayerActionManager manager = PlayerActionManager.instance;

    public UseCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    @Override
    public @NotNull Component getHelp(int page) {
        return help;
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }

        if (hasFlag(args, "--stop") || hasFlag(args, "-s")) {
            manager.setAction(target, Action.USE, ActionSetting.stop());
            sender.sendMessage(textOfChildren(
                    text(target.getName(), GRAY),
                    text(" 已停止右键", GRAY)
            ));
            return true;
        }

        var settings = getActionSettings(args);
        if (settings == null) {
            return false;
        }

        manager.setAction(target, Action.USE, settings);
        sender.sendMessage(textOfChildren(
                text(target.getName(), GRAY),
                text(" 开始右键", WHITE)
        ));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            return super.onTabComplete(sender, command, label, args);
        }

        var suggestion = Stream.of(
                "-n:",
                "-i:",
                "--keep",
                "--stop"
        );

        if (!args[args.length - 1].isEmpty()) {
            suggestion = suggestion.filter(s -> s.startsWith(args[args.length - 1]));
        }

        return suggestion.collect(Collectors.toList());
    }
}
