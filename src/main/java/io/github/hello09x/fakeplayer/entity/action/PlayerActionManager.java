package io.github.hello09x.fakeplayer.entity.action;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.util.UnwrapUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerActionManager {

    public final static PlayerActionManager instance = new PlayerActionManager();

    private final ConcurrentMap<UUID, Map<Action, ActionManager>> MANAGERS = new ConcurrentHashMap<>();

    public PlayerActionManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }


    public void setAction(
            @NotNull Player player,
            @NotNull Action action,
            @NotNull ActionSetting setting
    ) {
        var managers = MANAGERS.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
        var manager = managers.computeIfAbsent(action, key -> new ActionManager(
                UnwrapUtils.getServerPlayer(player),
                action,
                setting
        ));
        manager.setting = setting;
    }

    public void tick() {
        var itr = MANAGERS.entrySet().iterator();
        while (itr.hasNext()) {
            var entry = itr.next();
            var player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                itr.remove();
                for(var manager: entry.getValue().values()) {
                    manager.stop();
                }
                continue;
            }

            for (var manager : entry.getValue().values()) {
                manager.tick();
            }

        }

    }

}
