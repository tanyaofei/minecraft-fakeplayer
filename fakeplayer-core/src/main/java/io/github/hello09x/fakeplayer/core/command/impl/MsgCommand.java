package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MsgCommand extends AbstractCommand {

    public final static MsgCommand instance = new MsgCommand();

    public void msg(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        int skip = (int) args.getOptional("skip").orElse(0);
        int size = (int) args.getOptional("size").orElse(10);

        var messages = manager.getMessages(target, skip, size);
        for (var message : messages) {
            sender.sendMessage(message.content());
        }
    }

}
