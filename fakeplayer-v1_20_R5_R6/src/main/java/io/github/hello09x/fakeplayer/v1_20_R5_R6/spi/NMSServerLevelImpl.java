package io.github.hello09x.fakeplayer.v1_20_R5_R6.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSServerLevel;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.jetbrains.annotations.NotNull;

public class NMSServerLevelImpl implements NMSServerLevel {

    @Getter
    private final ServerLevel handle;

    public NMSServerLevelImpl(@NotNull World world) {
        this.handle = ((CraftWorld) world).getHandle();
    }

}
