package io.github.hello09x.fakeplayer.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class Teleportor {

    public static void teleportAndSound(@NotNull Entity entity, @NotNull Location location) {
        entity.teleport(location);
        location.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

}
