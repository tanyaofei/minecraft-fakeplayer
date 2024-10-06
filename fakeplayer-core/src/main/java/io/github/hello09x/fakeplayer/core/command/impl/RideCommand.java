package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

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
        var entity = entities.stream().filter(e -> e != fake).findAny().orElse(null);
        if (entity == null) {
            return;
        }
        bridge.fromPlayer(fake).startRiding(entity, true);
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

    public void rideEntity(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = getFakeplayer(sender, args);
        var entity = (Entity) args.get("entity");
        if (entity == fake) {
            sender.sendMessage(translatable("fakeplayer.command.ride.entity.error.ride-self").color(RED));
            return;
        }
        if (entity == null) {
            return;
        }
        if (entity.isDead()) {
            return;
        }

        if (entity.getWorld() != fake.getWorld() || entity.getLocation().distance(fake.getLocation()) > 24) {
            sender.sendMessage(translatable("fakeplayer.command.ride.entity.error.too-far", text(fake.getName(), WHITE)).color(RED));
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
                         .filter(e -> e instanceof RideableMinecart || e instanceof Boat || e instanceof AbstractHorse)
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
        if (!fake.getWorld().equals(sender.getWorld()) || fake.getLocation().distance(sender.getLocation()) > 20) {
            sender.sendMessage(translatable(
                    "fakeplayer.command.ride.me.error.too-far",
                    text(fake.getName(), WHITE)).color(RED)
            );
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
