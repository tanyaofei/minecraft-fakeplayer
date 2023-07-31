package io.github.hello09x.fakeplayer.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class Teleportor {

    public static void tryTeleport(@NotNull CommandSender sender, @NotNull Entity entity, @NotNull Location location) {
        entity.teleport(location);
        Tasker.nextTick(() -> {
            if (isTeleportSuccess(entity, location)) {
                location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            } else {
                sender.sendMessage(textOfChildren(
                        text("传送失败: ", RED),
                        text("可能由于第三方插件阻止", GRAY)
                ));
            }
        });
    }

    public static void teleportAndSound(@NotNull Entity entity, @NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        if (entity.teleport(location, cause)) {
            location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    public static void teleportAndSound(@NotNull Entity entity, @NotNull Location location) {
        teleportAndSound(entity, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public static boolean isTeleportSuccess(@NotNull Entity entity, @NotNull Location location) {
        if (!entity.getLocation().getWorld().equals(location.getWorld())) {
            return false;
        }
        return entity.getLocation().distance(location) < 16;
    }

}
