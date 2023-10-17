package io.github.hello09x.fakeplayer.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class Teleportor {

    public static boolean teleportAndSound(@NotNull Entity entity, @NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        if (entity.teleport(location, cause)) {
            location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    public static boolean teleportAndSound(@NotNull Entity entity, @NotNull Location location) {
        return teleportAndSound(entity, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

}
