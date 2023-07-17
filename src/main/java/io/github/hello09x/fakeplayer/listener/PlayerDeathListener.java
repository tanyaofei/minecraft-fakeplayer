package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDeathListener implements Listener {

    public final static PlayerDeathListener instance = new PlayerDeathListener();
    private final FakePlayerManager manager = FakePlayerManager.instance;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handlePlayerDead(@NotNull PlayerDeathEvent event) {
        var player = event.getPlayer();
        if (!manager.isFakePlayer(player)) {
            return;
        }

        // 假人不会复活, 死掉了就踢掉
        player.kick();
    }

}
