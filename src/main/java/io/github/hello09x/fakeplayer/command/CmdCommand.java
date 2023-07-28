package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class CmdCommand extends AbstractCommand {

    public final static CmdCommand instance = new CmdCommand();

    private static String toCommandString(@NotNull CommandResult command) {
        return "/" + command.command().getName() + String.join(" ", command.args());
    }

    public void cmd(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var cmd = Objects.requireNonNull((CommandResult) args.get("command"));

        var cmdName = cmd.command().getName();
        if (cmdName.equals("fakeplayer") || cmdName.equals("fp")) {
            sender.sendMessage(text("禁止套娃!", RED));
            return;
        }

        if (!cmd.execute(target)) {
            sender.sendMessage(textOfChildren(
                    text(target.getName(), WHITE),
                    text(" 执行命令失败: ", GRAY),
                    text(toCommandString(cmd), RED),
                    text(" , 请检查命令是否正确以及假人是否有权限", GRAY)
            ));
        }

        sender.sendMessage(textOfChildren(
                text(target.getName(), WHITE),
                text(" 成功执行了命令: ", GRAY),
                text(toCommandString(cmd), YELLOW)
        ));
    }

}
