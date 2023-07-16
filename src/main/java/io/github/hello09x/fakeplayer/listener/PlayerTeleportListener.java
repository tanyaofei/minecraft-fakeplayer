package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerTeleportListener implements Listener {

    public final static PlayerTeleportListener instance = new PlayerTeleportListener();
    private final FakePlayerManager manager = FakePlayerManager.instance;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handlePlayerTeleport(@NotNull PlayerTeleportEvent event) {
        if (!manager.isFakePlayer(event.getPlayer())) {
            return;
        }

        // 创建玩家时会使用这个传送原因
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            return;
        }

        var from = event.getFrom().getWorld().getUID();
        var to = event.getTo().getWorld().getUID();
        if (from.equals(to)) {
            return;
        }

        event.setCancelled(true);
    }

}
