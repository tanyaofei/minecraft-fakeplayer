package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RideCommand extends AbstractCommand {

    public final static RideCommand instance = new RideCommand();


    public void rideAnything(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var entities = target.getNearbyEntities(4.5, 4.5, 4.5);
        if (entities.isEmpty()) {
            return;
        }

        nms.getServerPlayer(target).startRiding(nms.getEntity(entities.get(0)), true);    // minecraft 会处理重复骑同一个实体
    }

    public void rideNormal(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var entity = target.getNearbyEntities(4.5, 4.5, 4.5)
                .stream()
                .filter(e -> e instanceof Minecart || e instanceof Boat || e instanceof AbstractHorse)
                .findFirst()
                .orElse(null);

        if (entity == null) {
            return;
        }

        nms.getServerPlayer(target).startRiding(nms.getEntity(entity), true);
    }

    public void rideMe(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        if (!target.getWorld().equals(sender.getWorld())) {
            throw CommandAPI.failWithString("你离假人太远了");
        }

        var distance = target.getLocation().distance(sender.getLocation());
        if (distance > Bukkit.getViewDistance()) {
            throw CommandAPI.failWithString("你离假人太远了");
        }

        nms.getServerPlayer(target).startRiding(nms.getServerPlayer(sender), true);     // minecraft 会处理重复骑同一个实体
    }

    public void stopRiding(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        nms.getServerPlayer(getTarget(sender, args)).stopRiding();
    }

}
