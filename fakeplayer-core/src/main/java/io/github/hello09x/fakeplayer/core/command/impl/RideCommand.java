package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
public class RideCommand extends AbstractCommand {

    /**
     * 骑最近的实体
     */
    public void rideAnything(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = getFakeplayer(sender, args);
        var entities = fake.getNearbyEntities(4.5, 4.5, 4.5);
        if (entities.isEmpty()) {
            return;
        }
        bridge.fromPlayer(fake).startRiding(entities.get(0), true);
    }

    /**
     * 骑目标实体
     */
    public void rideTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = getFakeplayer(sender, args);
        var entity = fake.getTargetEntity(5);
        if (entity == null) {
            return;
        }

        bridge.fromPlayer(fake).startRiding(entity, true);
    }

    /**
     * 骑正常可以骑的附近实体
     */
    public void rideVehicle(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = getFakeplayer(sender, args);
        var entity = fake.getNearbyEntities(4.5, 4.5, 4.5)
                           .stream()
                           .filter(e -> e instanceof Minecart || e instanceof Boat || e instanceof AbstractHorse)
                           .findFirst()
                           .orElse(null);

        if (entity == null) {
            return;
        }

        bridge.fromPlayer(fake).startRiding(entity, true);
    }

    /**
     * 骑创建者
     */
    public void rideMe(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = getFakeplayer(sender, args);
        if (!fake.getWorld().equals(sender.getWorld())) {
            sender.sendMessage(translatable("fakeplayer.command.ride.me.error.too-far", RED));
            return;
        }

        var distance = fake.getLocation().distance(sender.getLocation());
        if (distance > Bukkit.getViewDistance()) {
            sender.sendMessage(translatable(
                    "fakeplayer.command.ride.me.error.too-far",
                    text(fake.getName(), WHITE)
            ).color(GRAY));
            return;
        }

        bridge.fromPlayer(fake).startRiding(sender, true);
    }

    /**
     * 停止骑行
     */
    public void stopRiding(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        bridge.fromPlayer(getFakeplayer(sender, args)).stopRiding();
    }

}
