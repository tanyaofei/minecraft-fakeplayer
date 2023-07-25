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

public class AttackCommand extends AbstractActionCommand {


    public final static Component help = Helps.help(
            "控制假人点击鼠标左键",
            "攻击面前的实体、破坏面前的方块",
            new Helps.Content("用法", "/fp attack [假人] [-n:次数] [-i:间隔] [-k|--keep] [-s|--stop]"),
            new Helps.Content("参数", List.of(
                    "-n:次数 - 指定次数, 只有成功攻击或破坏才计数",
                    "-i:间隔 - 指定间隔, 单位为 ticks",
                    "--keep - 永不停止",
                    "--stop - 立即停止"
            )),
            new Helps.Content("例子", List.of(
                    "/fp attack hello09x -n:5 -i:20   - 点击每 20 ticks 触发鼠标左键, 共 5 次",
                    "/fp attach hello09x -i:20 --keep - 永久(真的很久)地每 20 ticks 触发鼠标左键",
                    "/fp attack hello09x --stop       - 取消触发鼠标左键"
            ))
    );

    public final static AttackCommand instance = new AttackCommand(
            "控制假人点击鼠标左键",
            "/fp attack [假人] [-n:次数] [-i:间隔] [-k|--keep] [-s|--stop]",
            "fakeplayer.experimental.action"
    );
    private final PlayerActionManager manager = PlayerActionManager.instance;

    public AttackCommand(
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
            manager.setAction(target, Action.ATTACK, ActionSetting.stop());
            sender.sendMessage(textOfChildren(
                    text(target.getName(), GRAY),
                    text(" 已停止左键", GRAY)
            ));
            return true;
        }

        var setting = getActionSettings(args);
        if (setting == null) {
            return false;
        }

        manager.setAction(target, Action.ATTACK, setting);
        sender.sendMessage(textOfChildren(
                text(target.getName(), GRAY),
                text(" 开始左键")
        ));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
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
