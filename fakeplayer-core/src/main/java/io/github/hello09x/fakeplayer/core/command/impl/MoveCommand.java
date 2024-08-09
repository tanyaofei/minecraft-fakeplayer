package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.core.Main;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class MoveCommand extends AbstractCommand {

    private final Map<UUID, BukkitTask> stopTasks = new HashMap<>();

    /**
     * 假人移动
     */
    public CommandExecutor move(float forward, float strafing) {
        return (sender, args) -> {
            var fake = getFakeplayer(sender, args);
            var handle = bridge.fromPlayer(fake);
            float vel = fake.isSneaking() ? 0.3F : 1.0F;
            if (forward != 0.0F) {
                handle.setZza(vel * forward);
            }
            if (strafing != 0.0F) {
                handle.setXxa(vel * strafing);
            }

            var task = stopTasks.remove(fake.getUniqueId());
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }

            // 只移动 1 秒
            this.stopTasks.put(fake.getUniqueId(), Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                handle.setXxa(0);
                handle.setZza(0);
                this.stopTasks.remove(fake.getUniqueId());
            }, Ticks.TICKS_PER_SECOND));
        };
    }


}
