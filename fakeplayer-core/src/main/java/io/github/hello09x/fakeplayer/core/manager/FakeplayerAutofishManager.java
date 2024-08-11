package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.constant.MetadataKeys;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

/**
 * @author tanyaofei
 * @since 2024/8/11
 **/
@Singleton
public class FakeplayerAutofishManager implements Listener {

    private final FakeplayerManager manager;
    private final ActionManager actionManager;

    @Inject
    public FakeplayerAutofishManager(FakeplayerManager manager, ActionManager actionManager) {
        this.manager = manager;
        this.actionManager = actionManager;
    }

    public boolean isAutofish(@NotNull Player fake) {
        return fake.hasMetadata(MetadataKeys.AUTOFISH);
    }

    public void setAutofish(@NotNull Player target, boolean autofish) {
        if (!autofish) {
            target.removeMetadata(MetadataKeys.AUTOFISH, Main.getInstance());
        } else {
            target.setMetadata(MetadataKeys.AUTOFISH, new FixedMetadataValue(Main.getInstance(), true));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void autoFishing(@NotNull PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.BITE) {
            return;
        }

        var player = event.getPlayer();
        if (manager.isNotFake(player)) {
            return;
        }

        if (!player.hasMetadata(MetadataKeys.AUTOFISH)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            actionManager.setAction(player, ActionType.USE, ActionSetting.once());
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                actionManager.setAction(player, ActionType.USE, ActionSetting.once());
            }, Ticks.TICKS_PER_SECOND);
        }, 1);
    }

}
