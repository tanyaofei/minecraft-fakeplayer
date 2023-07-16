package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class PlayerQuitListener implements Listener {

    public final static PlayerQuitListener instance = new PlayerQuitListener();
    private final static Logger log = Main.getInstance().getLogger();
    private final FakePlayerManager manager = FakePlayerManager.instance;

    @EventHandler
    public void handlePlayerQuit(@NotNull PlayerQuitEvent event) {
        var player = event.getPlayer();
        if (manager.isFakePlayer(player)) {
            return;
        }

        int count = manager.removeFakePlayers(player);
        if (count > 0) {
            log.info(String.format("玩家 %s 下线, 已清理 %d 个假人", event.getPlayer().getName(), count));
        }
    }

}
