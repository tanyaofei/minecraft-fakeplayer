package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.CommandResult;
import io.github.hello09x.fakeplayer.api.spi.NMSGamePacketListener;
import io.github.hello09x.fakeplayer.core.command.Permission;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CmdCommand extends AbstractCommand {

    public final static CmdCommand instance = new CmdCommand();

    public void cmd(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        var command = Objects.requireNonNull((CommandResult) args.get("command"));

        var name = command.command().getName();
        if (!sender.hasPermission(Permission.cmd) && !config.getAllowCommands().contains(name)) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.cmd.error.no-permission"));
        }

        if (!sender.isOp() && (name.equals("fakeplayer") || name.equals("fp"))) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.cmd.error.no-permission"));
        }

        var messageId = Optional.ofNullable(fakeplayerManager.getLastMessage(target))
                .map(NMSGamePacketListener.ReceivedMessage::id)
                .orElse(-1);
        if (!command.execute(target)) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.cmd.error.execute-failed"));
        }

        var message = fakeplayerManager.getLastMessage(target);
        if (message != null && message.id() != messageId) {
            sender.sendMessage(textOfChildren(
                    text(">> ", GRAY),
                    text(target.getName(), WHITE),
                    text(": ", GRAY),
                    message.content()
            ));
        } else {
            sender.sendMessage(i18n.translate("fakeplayer.command.cmd.success.execute", GRAY));
        }
    }

}
