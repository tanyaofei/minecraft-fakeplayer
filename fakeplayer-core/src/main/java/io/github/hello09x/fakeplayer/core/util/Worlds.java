package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Worlds {

    private final static String WORLD_OVERWORLD = "world";

    private final static String WORLD_NETHER = "world_nether";

    private final static String WORLD_END = "world_the_end";

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
        return Objects.requireNonNull(Bukkit.getWorld("world"), "No world named '%s' on this server".formatted(WORLD_OVERWORLD));
    }

    /**
     * 获取地狱世界
     *
     * @return 地狱世界
     */
    public static @NotNull World getNetherWorld() {
        return Objects.requireNonNull(Bukkit.getWorld(WORLD_NETHER), "No world named '%s' on this server".formatted(WORLD_NETHER));
    }

    /**
     * 获取末地世界
     *
     * @return 末地世界
     */
    public static @NotNull World getTheEndWorld() {
        return Objects.requireNonNull(Bukkit.getWorld(WORLD_END), "No world named '%s' on this server".formatted(WORLD_END));
    }

    /**
     * @return 任意一个非主世界的世界
     */
    public static @Nullable World getNonOverworld() {
        return Bukkit.getWorlds().stream().filter(w -> !w.getName().equals(WORLD_OVERWORLD)).findAny().orElse(null);
    }

}
