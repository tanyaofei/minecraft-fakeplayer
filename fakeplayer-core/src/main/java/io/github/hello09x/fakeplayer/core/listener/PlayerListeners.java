package io.github.hello09x.fakeplayer.core.listener;

import com.google.common.base.Throwables;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.core.util.InternalAddressGenerator;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
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
    private final FakeplayerConfig config = FakeplayerConfig.instance;

    /**
     * 拒绝假人用过的 ID 上线
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        if (InternalAddressGenerator.canBeGenerate(event.getAddress())) {
            // 假人模拟登陆的 ip 地址
            return;
        }

        if (usedIdRepository.contains(event.getUniqueId())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, textOfChildren(
                    text("你的 UUID 被使用过, 不能登陆到服务器\n\n", RED),
                    text("<<---- fakeplayer ---->>", GRAY)
            ));
            log.info("玩家 %s '%s' 被假人使用过, 拒绝连接服务器".formatted(event.getName(), event.getUniqueId()));
        }
    }

    /**
     * 死亡退出游戏
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDead(@NotNull PlayerDeathEvent event) {
        var player = event.getPlayer();
        if (!manager.isFake(player)) {
            return;
        }

        // 有一些跨服同步插件会退出时同步生命值, 假人重新生成的时候同步为 0
        // 因此在死亡时将生命值设置恢复满血先
        Optional.ofNullable(player.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                .map(AttributeInstance::getValue)
                .ifPresent(player::setHealth);
        event.setCancelled(true);
        manager.remove(event.getPlayer().getName(), event.deathMessage());
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
            manager.dispatchCommands(player, config.getDestroyCommands());
        } catch (Throwable e) {
            log.warning("执行 destroy-commands 时发生错误: \n" + Throwables.getStackTraceAsString(e));
        } finally {
            manager.cleanup(player);
        }
    }

}
