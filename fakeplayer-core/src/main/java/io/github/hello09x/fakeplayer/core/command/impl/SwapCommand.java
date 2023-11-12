package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class SwapCommand extends AbstractCommand {

    public final static SwapCommand instance = new SwapCommand();

    public void swap(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        bridge.player(target).swapItemWithOffhand();
    }

}
