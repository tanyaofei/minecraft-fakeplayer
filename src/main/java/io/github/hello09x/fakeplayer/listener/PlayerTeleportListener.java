package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class PlayerTeleportListener implements Listener {

    public final static PlayerTeleportListener instance = new PlayerTeleportListener();

    private final FakePlayerManager manager = FakePlayerManager.instance;

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    private final static Logger log = Main.getInstance().getLogger();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleTeleportEvent(@NotNull PlayerTeleportEvent event) {
        if (manager.isFake(event.getPlayer())
                && !(properties.isTpAcrossWorlds()
                && !Objects.equals(event.getFrom().getWorld().getUID(), event.getTo().getWorld().getUID()))){
            event.setCancelled(true);
        }
    }



}
