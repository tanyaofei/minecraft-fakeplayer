package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class InvseeCommand extends AbstractCommand {


    /**
     * 查看背包
     */
    public void invsee(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        if (!Objects.equals(sender.getLocation().getWorld(), target.getLocation().getWorld())) {
            throw CommandAPI.failWithString(ComponentUtils.toString(
                    translatable("fakeplayer.command.invsee.error.not-the-same-world"),
                    TranslatorUtils.getLocale(sender)
            ));
        }
        manager.openInventory(sender, target);
    }

}
