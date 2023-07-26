package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT;

public class TpCommand extends AbstractCommand {

    public final static TpCommand instance = new TpCommand();

    public void tp(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        this.teleport(sender, target);
    }

    public void tphere(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        this.teleport(target, sender);
    }

    public void tps(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);

        var l1 = sender.getLocation();
        var l2 = target.getLocation();

        target.teleport(l1, PlayerTeleportEvent.TeleportCause.PLUGIN);
        l1.getWorld().playSound(l1, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        sender.teleport(l2, PlayerTeleportEvent.TeleportCause.PLUGIN);
        l2.getWorld().playSound(l2, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    private void teleport(@NotNull Player from, @NotNull Player to) {
        from.teleport(to.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        to.getLocation().getWorld().playSound(to.getLocation(), ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }


}
