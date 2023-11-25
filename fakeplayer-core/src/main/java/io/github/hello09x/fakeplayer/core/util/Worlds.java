package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Worlds {

    private final static String WORLD_OVERWORLD = "world";

    /**
     * 判断一个世界是否是主世界
     *
     * @param world 世界
     * @return 该世界是否是主世界
     */
    public static boolean isOverworld(@NotNull World world) {
        return world.getName().equals(WORLD_OVERWORLD);
    }

    /**
     * 获取主世界
     *
     * @return 主世界
     */
    public static @NotNull World getOverworld() {
        return Objects.requireNonNull(Bukkit.getWorld("world"), "No world named 'world' on this server");
    }

    /**
     * @return 任意一个非主世界的世界
     */
    public static @Nullable World getNonOverworld() {
        return Bukkit.getWorlds().stream().filter(w -> !w.getName().equals(WORLD_OVERWORLD)).findAny().orElse(null);
    }

}
