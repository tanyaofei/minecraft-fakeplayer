package io.github.hello09x.fakeplayer.util;

import io.github.hello09x.fakeplayer.Main;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class Tasker {

    public static @NotNull BukkitTask nextTick(@NotNull Runnable runnable) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(Main.getInstance());
    }

    public static @NotNull BukkitTask later(@NotNull Runnable runnable, long delay) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(Main.getInstance(), delay);
    }

}
