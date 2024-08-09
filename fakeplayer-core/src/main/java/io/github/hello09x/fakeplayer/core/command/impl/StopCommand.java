package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author tanyaofei
 * @since 2024/7/28
 **/
@Singleton
public class StopCommand extends AbstractCommand {

    private final ActionManager actionManager;

    @Inject
    public StopCommand(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public void stop(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = super.getFakeplayer(sender, args);
        actionManager.stop(fake);
    }
}
