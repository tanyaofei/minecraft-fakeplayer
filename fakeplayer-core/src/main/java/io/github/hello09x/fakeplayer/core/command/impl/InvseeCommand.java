package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Singleton
public class InvseeCommand extends AbstractCommand {


    /**
     * 查看背包
     */
    public void invsee(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        if (!Objects.equals(sender.getLocation().getWorld(), target.getLocation().getWorld())) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.invsee.error.not-the-same-world"));
        }
        manager.openInventory(sender, target);
    }

}
