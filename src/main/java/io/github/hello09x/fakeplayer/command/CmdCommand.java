package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class CmdCommand extends AbstractCommand {

    public final static CmdCommand instance = new CmdCommand();

    public void cmd(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var cmd = Objects.requireNonNull((CommandResult) args.get("command"));

        var cmdName = cmd.command().getName();
        if (cmdName.equals("fakeplayer") || cmdName.equals("fp")) {
            sender.sendMessage(text("禁止套娃!", RED));
            return;
        }

        cmd.execute(target);
    }

}
