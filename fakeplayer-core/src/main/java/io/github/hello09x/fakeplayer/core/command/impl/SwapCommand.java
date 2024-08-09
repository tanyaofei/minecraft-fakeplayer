package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Singleton
public class SwapCommand extends AbstractCommand {

    /**
     * 交换主副手物品
     */
    public void swap(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = super.getFakeplayer(sender, args);
        bridge.fromPlayer(fake).swapItemWithOffhand();
    }

}
