package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.command.Permission;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

@Singleton
public class CmdCommand extends AbstractCommand {

    private final Logger log = Main.getInstance().getLogger();

    /**
     * 假人执行命令
     */
    public void cmd(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        var command = Objects.requireNonNull((CommandResult) args.get("command"));

        var name = command.command().getName();
        if (!sender.hasPermission(Permission.cmd) && !config.getAllowCommands().contains(name)) {
            sender.sendMessage(translatable("fakeplayer.command.cmd.error.no-permission", RED));
            return;
        }

        if (!sender.isOp() && (name.equals("fakeplayer") || name.equals("fp"))) {
            sender.sendMessage(translatable("fakeplayer.command.cmd.error.no-permission", RED));
            return;
        }

        if (!command.command().testPermission(target)) {
            sender.sendMessage(translatable("fakeplayer.command.cmd.error.fakeplayer-has-no-permission", RED, text(target.getName())));
            return;
        }

        if (!command.execute(target)) {
            sender.sendMessage(translatable("fakeplayer.command.cmd.error.execute-failed", RED));
            return;
        }

        sender.sendMessage(translatable(
                "fakeplayer.command.generic.success",
                GRAY
        ));

        log.info("%s issued server command: %s".formatted(target.getName(), toString(command)));
    }

    private static @NotNull String toString(@NotNull CommandResult command) {
        var builder = new StringBuilder("/");
        builder.append(command.command().getName());
        if (command.args() != null && command.args().length > 0) {
            builder.append(" ").append(String.join(" ", command.args()));
        }
        return builder.toString();
    }

}
