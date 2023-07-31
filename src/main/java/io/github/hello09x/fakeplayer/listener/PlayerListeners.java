package io.github.hello09x.fakeplayer.listener;

import com.google.common.base.Throwables;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import io.github.hello09x.fakeplayer.repository.UsedIdRepository;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

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
    public void onPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
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
    public void onDead(@NotNull PlayerDeathEvent event) {
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
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        var player = event.getPlayer();
        if (!manager.isFake(player)) {
            return;
        }

        try {
            manager.dispatchCommands(player, properties.getDestroyCommands());
        } catch (Throwable e) {
            log.warning("执行 destroy-commands 时发生错误: \n" + Throwables.getStackTraceAsString(e));
        } finally {
            manager.cleanup(player);
        }
    }

    /**
     * 检测传送失败
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(@NotNull PlayerTeleportEvent event) {
        if (!event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        var creatorName = manager.getCreator(player);
        if (creatorName == null) {
            return;
        }

        if (creatorName.equals(Bukkit.getServer().getConsoleSender().getName())) {
            log.info(String.format("假人 %s 传送失败: 可能由于第三方插件阻止", player.getName()));
            return;
        }

        var creator = Bukkit.getServer().getPlayerExact(creatorName);
        if (creator != null) {
            creator.sendMessage(textOfChildren(
                    text(player.getName(), WHITE),
                    text(" 传送失败: ", GRAY),
                    text("可能由于第三方插件阻止").style(Style.style(GRAY, ITALIC))
            ));
        }
    }
}
