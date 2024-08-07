package io.github.hello09x.fakeplayer.core.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.devtools.core.utils.MetadataUtils;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.constant.MetadataKeys;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.repository.FakePlayerProfileRepository;
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
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

@Singleton
public class FakeplayerListener implements Listener {

    private final static Logger log = Main.getInstance().getLogger();

    private final FakeplayerManager manager;
    private final ActionManager actionManager;
    private final UsedIdRepository usedIdRepository;
    private final FakePlayerProfileRepository profileRepository;
    private final FakeplayerConfig config;

    @Inject
    public FakeplayerListener(FakeplayerManager manager, ActionManager actionManager, UsedIdRepository usedIdRepository, FakePlayerProfileRepository profileRepository, FakeplayerConfig config) {
        this.manager = manager;
        this.actionManager = actionManager;
        this.usedIdRepository = usedIdRepository;
        this.profileRepository = profileRepository;
        this.config = config;
    }

    /**
     * 拒绝真实玩家使用假人用过的 ID 登陆
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void rejectForUsedUUID(@NotNull PlayerLoginEvent event) {
        var player = event.getPlayer();

        if (InternalAddressGenerator.canBeGenerated(event.getAddress())) {
            return;
        }

        if (usedIdRepository.contains(player.getUniqueId()) || profileRepository.existsByUUID(player.getUniqueId())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, textOfChildren(
                    translatable("fakeplayer.listener.login.deny-used-uuid", RED),
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

        if (manager.isNotFake(event.getPlayer())) {
            return;
        }

        switch (config.getPreventKicking()) {
            case ON_SPAWNING -> {
                var spawnAt = MetadataUtils
                        .find(Main.getInstance(), player, MetadataKeys.SPAWNED_AT, Integer.class)
                        .map(MetadataValue::asInt)
                        .orElse(null);
                if (spawnAt != null && Bukkit.getCurrentTick() - spawnAt < 20) {
                    event.setCancelled(true);
                }
                log.warning(String.format(
                        "Canceled kicking fake player '%s' on spawning due to your configuration",
                        player.getName()
                ));
            }
            case ALWAYS -> {
                if (!ComponentUtils.toString(event.reason()).startsWith("[fakeplayer]")) {
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
    public void kickOrNotifyOnDead(@NotNull PlayerDeathEvent event) {
        var player = event.getPlayer();
        if (manager.isNotFake(player)) {
            return;
        }
        if (!config.isKickOnDead()) {
            var creator = manager.getCreator(player);
            if (creator != null) {
                creator.sendMessage(translatable(
                        "fakeplayer.listener.death.notify",
                        text(player.getName(), GOLD),
                        text("/fp respawn", DARK_GREEN, UNDERLINED).clickEvent(runCommand("/fp respawn " + player.getName()))
                ).color(RED));
            }
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
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void cleanup(@NotNull PlayerQuitEvent event) {
        var target = event.getPlayer();
        if (manager.isNotFake(target)) {
            return;
        }

        try {
            if (manager.getCreator(target) instanceof Player creator && manager.countByCreator(creator) == 1) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), creator::updateCommands, 1); // 需要下 1 tick 移除后才正确刷新
            }
        } finally {
            manager.cleanup(target);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void autoFishing(@NotNull PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.BITE) {
            return;
        }

        var player = event.getPlayer();
        if (manager.isNotFake(player)) {
            return;
        }

        if (!player.hasMetadata(MetadataKeys.AUTOFISH)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            actionManager.setAction(player, Action.ActionType.USE, Action.ActionSetting.once());
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                actionManager.setAction(player, Action.ActionType.USE, Action.ActionSetting.once());
            }, 10); // 3 ticks CD
        }, 1);
    }

}
