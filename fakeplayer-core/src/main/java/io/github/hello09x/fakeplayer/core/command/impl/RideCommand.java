package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RideCommand extends AbstractCommand {

    public final static RideCommand instance = new RideCommand();

    /**
     * 骑最近的实体
     */
    public void rideAnything(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var entities = target.getNearbyEntities(4.5, 4.5, 4.5);
        if (entities.isEmpty()) {
            return;
        }
        bridge.fromPlayer(target).startRiding(entities.get(0), true);
    }

    /**
     * 骑目标实体
     */
    public void rideTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var entity = target.getTargetEntity(5);
        if (entity == null) {
            return;
        }

        bridge.fromPlayer(target).startRiding(entity, true);
    }

    /**
     * 骑正常可以骑的附近实体
     */
    public void rideVehicle(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var entity = target.getNearbyEntities(4.5, 4.5, 4.5)
                .stream()
                .filter(e -> e instanceof Minecart || e instanceof Boat || e instanceof AbstractHorse)
                .findFirst()
                .orElse(null);

        if (entity == null) {
            return;
        }

        bridge.fromPlayer(target).startRiding(entity, true);
    }

    /**
     * 骑创建者
     */
    public void rideMe(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        if (!target.getWorld().equals(sender.getWorld())) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.ride.me.error.too-far"));
        }

        var distance = target.getLocation().distance(sender.getLocation());
        if (distance > Bukkit.getViewDistance()) {
            sender.sendMessage(i18n.translate(
                    "fakeplayer.command.ride.me.error.too-far", GRAY,
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
            return;
        }

        bridge.fromPlayer(target).startRiding(sender, true);
    }

    /**
     * 停止骑行
     */
    public void stopRiding(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        bridge.fromPlayer(getTarget(sender, args)).stopRiding();
    }

}
