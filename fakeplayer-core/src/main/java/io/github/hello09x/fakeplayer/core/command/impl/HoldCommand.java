package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@NoArgsConstructor
public class HoldCommand extends AbstractCommand {

    public final static HoldCommand instance = new HoldCommand();

    public void hold(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        var slot = (int) Objects.requireNonNull(args.get("slot"));
        target.getInventory().setHeldItemSlot(slot - 1);
    }

}
