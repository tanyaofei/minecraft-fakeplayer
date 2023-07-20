package io.github.hello09x.fakeplayer.util;

import net.minecraft.world.level.ChunkPos;
import org.bukkit.World;

public record ChunkCoord(
        World world,

        ChunkPos pos
) {
}

