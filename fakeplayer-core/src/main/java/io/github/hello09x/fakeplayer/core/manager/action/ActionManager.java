package io.github.hello09x.fakeplayer.core.manager.action;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class ActionManager {

    private final Map<UUID, Map<Action.ActionType, ActionTicker>> managers = new HashMap<>();

    private final NMSBridge bridge;

    @Inject
    public ActionManager(NMSBridge bridge) {
        this.bridge = bridge;
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 1);
    }

    public void setAction(
            @NotNull Player player,
            @NotNull Action.ActionType action,
            @NotNull Action.ActionSetting setting
    ) {
        var managers = this.managers.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
        managers.put(action, bridge.createAction(player, action, setting));
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
            entry.getValue().values().removeIf(ActionTicker::tick);
            if (entry.getValue().isEmpty()) {
                itr.remove();
            }
        }
    }

}
