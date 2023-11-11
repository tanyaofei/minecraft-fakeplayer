package io.github.hello09x.fakeplayer.v1_20_R1.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSServerLevel;
import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NMSServerLevelImpl implements NMSServerLevel {

    public final static NMSServerLevelImpl OVERWORLD = new NMSServerLevelImpl(
            Objects.requireNonNull(Bukkit.getWorld("world"))
    );

    @Getter
    private final ServerLevel handle;

    public NMSServerLevelImpl(@NotNull World world) {
        this.handle = ((CraftWorld) world).getHandle();
    }

}
