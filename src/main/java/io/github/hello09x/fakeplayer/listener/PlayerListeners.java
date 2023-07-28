package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PlayerListeners implements Listener {

    public final static PlayerListeners instance = new PlayerListeners();
    private final static Logger log = Main.getInstance().getLogger();
    private final FakeplayerManager manager = FakeplayerManager.instance;

    private final UsedIdRepository usedIdRepository = UsedIdRepository.instance;
    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    /**
     * 拒绝假人用过的 ID 上线
     */
    @EventHandler(ignoreCancelled = true)
    public void handleUsedIdLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if (usedIdRepository.contains(event.getUniqueId())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, textOfChildren(
                    text("你的 UUID 被使用过, 不能登陆到服务器\n\n", RED),
                    text("<<---- fakeplayer ---->>", GRAY)
            ));
        }
    }

    /**
     * 死亡退出游戏
     */
    @EventHandler(ignoreCancelled = true)
    public void handlePlayerDeath(@NotNull PlayerDeathEvent event) {
        var player = event.getPlayer();
        if (!manager.isFake(player)) {
            return;
        }

        // 假人不会复活, 死掉了就踢掉
        player.kick();
    }

    /**
     * 退出游戏掉落背包
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handlePlayerQuit(@NotNull PlayerQuitEvent event) {
        var player = event.getPlayer();
        if (!manager.isFake(player)) {
            return;
        }

        try {
            manager.dispatchCommands(player, properties.getDestroyCommands());
        } finally {
            manager.cleanup(player);
        }
    }
}
