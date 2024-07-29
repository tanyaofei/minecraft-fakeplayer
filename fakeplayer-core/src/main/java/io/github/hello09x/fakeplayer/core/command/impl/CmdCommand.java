package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.command.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@Singleton
public class CmdCommand extends AbstractCommand {

    /**
     * 假人执行命令
     */
    public void cmd(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        var command = Objects.requireNonNull((CommandResult) args.get("command"));

        var name = command.command().getName();
        if (!sender.hasPermission(Permission.cmd) && !config.getAllowCommands().contains(name)) {
            throw CommandAPI.failWithString(ComponentUtils.toString(
                    translatable("fakeplayer.command.cmd.error.no-permission"),
                    TranslatorUtils.getLocale(sender)
            ));
        }

        if (!sender.isOp() && (name.equals("fakeplayer") || name.equals("fp"))) {
            throw CommandAPI.failWithString(ComponentUtils.toString(
                    translatable("fakeplayer.command.cmd.error.no-permission"),
                    TranslatorUtils.getLocale(sender)
            ));
        }

        if (!command.execute(target)) {
            throw CommandAPI.failWithString(ComponentUtils.toString(
                    translatable("fakeplayer.command.cmd.error.execute-failed"),
                    TranslatorUtils.getLocale(sender)
            ));
        }

        sender.sendMessage(translatable(
                "fakeplayer.command.cmd.success.execute",
                GRAY
        ));
    }

}
