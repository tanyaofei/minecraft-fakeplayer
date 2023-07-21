package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerInteractAtEntityListener implements Listener {

    public final static PlayerInteractAtEntityListener instance = new PlayerInteractAtEntityListener();

    private final FakePlayerManager manager = FakePlayerManager.instance;

    @EventHandler(ignoreCancelled = true)
    public void handleInteractAtEntity(@NotNull PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        if (!player.hasPermission("fakeplayer.control")) {
            return;
        }

        if (!(event.getRightClicked() instanceof Player target) || !manager.isFake(target)) {
            return;
        }

        if (player.isOp() || Objects.equals(manager.getCreator(target), player.getName())) {
            manager.openInventory(player, target);
        }

    }
}
