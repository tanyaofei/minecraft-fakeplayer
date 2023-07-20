package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class PlayerQuitListener implements Listener {

    public final static PlayerQuitListener instance = new PlayerQuitListener();
    private final static Logger log = Main.getInstance().getLogger();
    private final FakePlayerManager manager = FakePlayerManager.instance;
    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    @EventHandler(ignoreCancelled = true)
    public void handlePlayerQuit(@NotNull PlayerQuitEvent event) {
        var player = event.getPlayer();
        if (manager.isFake(player)) {
            manager.cleanup(player);
            manager.dispatchCommands(player, properties.getDestroyCommands());
        } else {
            int removed;
            if (properties.isFollowQuiting() && (removed = manager.removeAll(player)) > 0) {
                log.info(String.format("玩家 %s 下线, 已清理 %d 个假人", event.getPlayer().getName(), removed));
            }
        }
    }

}
