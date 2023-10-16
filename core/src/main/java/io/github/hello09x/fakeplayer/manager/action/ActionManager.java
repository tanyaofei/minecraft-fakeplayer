package io.github.hello09x.fakeplayer.manager.action;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionTicker;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ActionManager {

    public final static ActionManager instance = new ActionManager();

    private final ConcurrentMap<UUID, Map<ActionType, ActionTicker>> managers = new ConcurrentHashMap<>();

    public ActionManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }


    public void setAction(
            @NotNull Player player,
            @NotNull ActionType action,
            @NotNull ActionSetting setting
    ) {
        var managers = this.managers.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
        managers.computeIfAbsent(action, key -> ActionTicker.create(player, action, setting));
    }

    public void tick() {
        var itr = managers.entrySet().iterator();
        while (itr.hasNext()) {
            var entry = itr.next();
            var player = Bukkit.getPlayer(entry.getKey());

            if (player == null) {
                // 假人下线
                itr.remove();
                for (var ticker : entry.getValue().values()) {
                    ticker.stop();
                }
                continue;
            }

            // do tick
            for (var ticker : entry.getValue().values()) {
                ticker.tick();
            }
        }
    }

}
