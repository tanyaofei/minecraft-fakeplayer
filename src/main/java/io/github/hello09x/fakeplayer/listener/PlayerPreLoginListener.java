package io.github.hello09x.fakeplayer.listener;

import io.github.hello09x.fakeplayer.repository.UsedUUIDRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class PlayerPreLoginListener implements Listener {

    public final static PlayerPreLoginListener instance = new PlayerPreLoginListener();
    private final UsedUUIDRepository usedIdsRepository = UsedUUIDRepository.instance;

    @EventHandler(ignoreCancelled = true)
    public void handlePlayerPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if (usedIdsRepository.contains(event.getUniqueId())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, textOfChildren(
                    text("你的 UUID 被使用过, 不能登陆到服务器\n\n", RED),
                    text("<<---- fakeplayer ---->>", GRAY)
            ));
        }

    }

}
