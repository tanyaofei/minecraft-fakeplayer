package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import io.github.hello09x.bedrock.i18n.I18n;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class CmdCommand extends AbstractCommand {

    public final static CmdCommand instance = new CmdCommand();

    public void cmd(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var command = Objects.requireNonNull((CommandResult) args.get("command"));

        var name = command.command().getName();
        if (!sender.hasPermission(Permission.cmd) && !config.getAllowCommands().contains(name)) {
            throw CommandAPI.failWithString(I18n.asString("fakeplayer.command.cmd.error.no-permission"));
        }

        if (name.equals("fakeplayer") || name.equals("fp")) {
            throw CommandAPI.failWithString(I18n.asString("fakeplayer.command.cmd.error.no-permission"));
        }

        if (!command.execute(target)) {
            sender.sendMessage(I18n.translate(translatable("fakeplayer.command.cmd.error.execute-failed", RED)));
            return;
        }

        sender.sendMessage(I18n.translate(translatable("fakeplayer.command.cmd.success.execute", GRAY)));
    }

}
