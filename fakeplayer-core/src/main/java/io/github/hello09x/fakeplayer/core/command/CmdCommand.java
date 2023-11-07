package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CmdCommand extends AbstractCommand {

    public final static CmdCommand instance = new CmdCommand();

    public void cmd(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var command = Objects.requireNonNull((CommandResult) args.get("command"));

        var name = command.command().getName();
        if (!sender.hasPermission(Permission.cmd) && !config.getAllowCommands().contains(name)) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.cmd.error.no-permission"));
        }

        if (name.equals("fakeplayer") || name.equals("fp")) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.cmd.error.no-permission"));
        }

        if (!command.execute(target)) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.cmd.error.execute-failed"));
        }

        sender.sendMessage(i18n.translate("fakeplayer.command.cmd.success.execute", GRAY));
    }

}
