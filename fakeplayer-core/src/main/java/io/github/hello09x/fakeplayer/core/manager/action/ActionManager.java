package io.github.hello09x.fakeplayer.core.manager.action;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class ActionManager {

    private final static Logger log = Main.getInstance().getLogger();

    private final Map<UUID, Map<ActionType, ActionTicker>> managers = new HashMap<>();

    private final NMSBridge bridge;


    @Inject
    public ActionManager(NMSBridge bridge) {
        this.bridge = bridge;
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tick, 0, 1);
    }

    public boolean hasActiveAction(
            @NotNull Player player,
            @NotNull ActionType action
    ) {
        return Optional.ofNullable(this.managers.get(player.getUniqueId()))
                       .map(manager -> manager.get(action))
                       .filter(ac -> ac.getSetting().remains > 0)
                       .isPresent();
    }

    public @NotNull @Unmodifiable Set<ActionType> getActiveActions(@NotNull Player player) {
        var manager = this.managers.get(player.getUniqueId());
        if (manager == null || managers.isEmpty()) {
            return Collections.emptySet();
        }

        return manager.entrySet()
                      .stream()
                      .filter(actions -> actions.getValue().getSetting().remains > 0)
                      .map(Map.Entry::getKey)
                      .collect(Collectors.toSet());
    }

    public void setAction(
            @NotNull Player player,
            @NotNull ActionType action,
            @NotNull ActionSetting setting
    ) {
        var managers = this.managers.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
        managers.put(action, bridge.createAction(player, action, setting));
    }

    public void stop(@NotNull Player player) {
        var managers = this.managers.get(player.getUniqueId());
        if (managers == null || managers.isEmpty()) {
            return;
        }

        for (var entry : managers.entrySet()) {
            if (!entry.getValue().equals(ActionSetting.stop())) {
                entry.setValue(bridge.createAction(player, entry.getKey(), ActionSetting.stop()));
            }
        }
    }

    public void tick() {
        var itr = managers.entrySet().iterator();
        while (itr.hasNext()) {
            var entry = itr.next();
            var player = Bukkit.getPlayer(entry.getKey());

            if (player == null || !player.isValid()) {
                // 假人下线或者死亡
                itr.remove();
                for (var ticker : entry.getValue().values()) {
                    ticker.stop();
                }
                continue;
            }

            // do tick
            entry.getValue().values().removeIf(ticker -> {
                try {
                    return ticker.tick();
                } catch (Throwable e) {
                    log.warning(Throwables.getStackTraceAsString(e));
                    return false;
                }
            });
            if (entry.getValue().isEmpty()) {
                itr.remove();
            }
        }
    }

}
