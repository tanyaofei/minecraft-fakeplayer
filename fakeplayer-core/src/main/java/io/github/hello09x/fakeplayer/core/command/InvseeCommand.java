package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InvseeCommand extends AbstractCommand {

    public final static InvseeCommand instance = new InvseeCommand();

    public void invsee(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        if (!Objects.equals(sender.getLocation().getWorld(), target.getLocation().getWorld())) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.invsee.error.not-the-same-world"));
        }
        fakeplayerManager.openInventory(sender, target);
    }

}
