package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class PlayerTeleportListener implements Listener {

    public final static PlayerTeleportListener instance = new PlayerTeleportListener();

    private final FakePlayerManager manager = FakePlayerManager.instance;

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    private final static Logger log = Main.getInstance().getLogger();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleTeleportEvent(@NotNull PlayerTeleportEvent event) {
        var player = event.getPlayer();
        if (!manager.isFake(player)) {
            return;
        }

        var fromWorld = event.getFrom().getWorld().getUID();
        var toWorld = event.getTo().getWorld().getUID();

        if (!properties.isTpAcrossWorlds() && fromWorld != toWorld) {
            log.info(String.format("已取消假人 %s 跨世界传送的操作", event.getPlayer().getName()));
            event.setCancelled(true);
        }
    }

}
