package io.github.hello09x.fakeplayer.manager.action;

import io.github.hello09x.fakeplayer.Main;
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

    private final ConcurrentMap<UUID, Map<Action, ActionTicker>> MANAGERS = new ConcurrentHashMap<>();

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
            @NotNull Action action,
            @NotNull ActionSetting setting
    ) {
        var managers = MANAGERS.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
        var ticker = managers.computeIfAbsent(action, key -> new ActionTicker(
                Main.getNms().getServerPlayer(player),
                action,
                setting
        ));
        ticker.setting = setting;
    }

    public void tick() {
        var itr = MANAGERS.entrySet().iterator();
        while (itr.hasNext()) {
            var entry = itr.next();
            var player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                itr.remove();
                for (var ticker : entry.getValue().values()) {
                    ticker.stop();
                }
                continue;
            }

            for (var ticker : entry.getValue().values()) {
                ticker.tick();
            }
        }
    }

}
