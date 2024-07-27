package io.github.hello09x.fakeplayer.core.listener;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.bedrock.util.Components;
import io.github.hello09x.devtools.transaction.PluginTranslator;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.constant.FakePlayerStatus;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.repository.UsedIdRepository;
import io.github.hello09x.fakeplayer.core.util.InternalAddressGenerator;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

@Singleton
public class FakeplayerListener implements Listener {

    private final static Logger log = Main.getInstance().getLogger();

    private final FakeplayerManager manager;
    private final UsedIdRepository usedIdRepository;
    private final FakeplayerConfig config;
    private final PluginTranslator translator;

    @Inject
    public FakeplayerListener(FakeplayerManager manager, UsedIdRepository usedIdRepository, FakeplayerConfig config, PluginTranslator translator) {
        this.manager = manager;
        this.usedIdRepository = usedIdRepository;
        this.config = config;
        this.translator = translator;
    }

    /**
     * 拒绝真实玩家使用假人用过的 ID 登陆
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLogin(@NotNull PlayerLoginEvent event) {
        var player = event.getPlayer();

        if (InternalAddressGenerator.canBeGenerated(event.getAddress())) {
            return;
        }

        if (usedIdRepository.contains(player.getUniqueId())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, textOfChildren(
                    translator.translate("fakeplayer.listener.login.deny-used-uuid", null, RED),
                    newline(),
                    newline(),
                    text("<<---- fakeplayer ---->>", GRAY)
            ));
            log.info("%s(%s) was refused to login cause his UUID was used by [Fakeplayer]".formatted(
                    player.getName(),
                    player.getUniqueId()
            ));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void preventKicking(@NotNull PlayerKickEvent event) {
        var player = event.getPlayer();

        switch (config.getPreventKicking()) {
            case ON_SPAWNING -> {
                if (player.getMetadata(FakePlayerStatus.METADATA_KEY)
                          .stream()
                          .anyMatch(metadata -> metadata.value() == FakePlayerStatus.SPAWNING)
                ) {
                    event.setCancelled(true);
                    log.warning(String.format(
                            "Canceled kicking fake player '%s' on spawning due to your configuration",
                            player.getName()
                    ));
                }
            }
            case ALWAYS -> {
                if (!Components.asString(event.reason()).startsWith("[fakeplayer]")) {
                    event.setCancelled(true);
                    log.warning(String.format(
                            "Canceled kicking fake player '%s' due to your configuration",
                            player.getName()
                    ));
                }
            }
        }
    }

    /**
     * 死亡退出游戏
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDead(@NotNull PlayerDeathEvent event) {
        var player = event.getPlayer();
        if (manager.isNotFake(player)) {
            return;
        }
        if (!config.isKickOnDead()) {
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
        var target = event.getPlayer();
        if (manager.isNotFake(target)) {
            return;
        }

        try {
            manager.dispatchCommands(target, config.getDestroyCommands());
            // 如果移除玩家后没有假人, 则更新命令列表
            // 这个方法需要在 cleanup 之前执行, 不然无法获取假人的创建者
            if (manager.getCreator(target) instanceof Player creator && manager.countByCreator(creator) == 1) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), creator::updateCommands, 1); // 需要下 1 tick 移除后才正确刷新
            }
        } catch (Throwable e) {
            log.warning("执行 destroy-commands 时发生错误: \n" + Throwables.getStackTraceAsString(e));
        } finally {
            manager.cleanup(target);
        }
    }

}
