package io.github.hello09x.fakeplayer.util;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CraftUtils {

    public static void setPlayBefore(@NotNull Player player) {
        ((CraftPlayer) player).readExtraData(new CompoundTag());
    }

}
