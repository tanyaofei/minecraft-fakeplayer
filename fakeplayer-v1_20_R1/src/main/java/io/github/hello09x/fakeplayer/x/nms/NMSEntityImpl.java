package io.github.hello09x.fakeplayer.x.nms;

import io.github.hello09x.fakeplayer.api.nms.NMSEntity;
import lombok.Getter;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.jetbrains.annotations.NotNull;

public class NMSEntityImpl implements NMSEntity {

    @Getter
    private Entity handle;

    public NMSEntityImpl(@NotNull org.bukkit.entity.@NotNull Entity entity) {
        this.handle = ((CraftEntity) entity).getHandle();
    }


}
