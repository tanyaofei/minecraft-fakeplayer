package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPI;
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
        var command = Objects.requireNonNull((CommandResult) args.get("命令"));

        var name = command.command().getName();
        if (!sender.hasPermission(Permission.cmd) && !config.getAllowCommands().contains(name)) {
            throw CommandAPI.failWithString("没有权限执行此命令: " + name);
        }

        if (name.equals("fakeplayer") || name.equals("fp")) {
            throw CommandAPI.failWithString("禁止套娃");
        }

        if (!command.execute(target)) {
            sender.sendMessage(textOfChildren(
                    text(target.getName(), WHITE),
                    text(" 执行命令失败: ", GRAY),
                    text(toCommandString(command), RED),
                    text(" , 请检查命令是否正确以及假人是否有权限", GRAY)
            ));
            return;
        }

        sender.sendMessage(textOfChildren(
                text(target.getName(), WHITE),
                text(" 成功执行了命令: ", GRAY),
                text(toCommandString(command), YELLOW)
        ));
    }

}
