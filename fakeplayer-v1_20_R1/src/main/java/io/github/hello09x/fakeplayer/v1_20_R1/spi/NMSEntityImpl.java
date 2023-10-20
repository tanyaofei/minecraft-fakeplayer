package io.github.hello09x.fakeplayer.v1_20_R1.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSEntity;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;

public class NMSEntityImpl implements NMSEntity {

    @Getter
    private final Entity handle;

    public NMSEntityImpl(@NotNull org.bukkit.entity.@NotNull Entity entity) {
        this.handle = ((CraftEntity) entity).getHandle();
    }


}
