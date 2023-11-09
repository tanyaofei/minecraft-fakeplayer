package io.github.hello09x.fakeplayer.core.listener;

import io.github.hello09x.fakeplayer.api.constant.ConstantPool;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListeners implements Listener {

    public final static PlayerListeners instance = new PlayerListeners();
    private final FakeplayerManager manager = FakeplayerManager.instance;

    /**
     * 玩家蹲伏时取消假人骑乘
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSneak(@NotNull PlayerToggleSneakEvent event) {
        var player = event.getPlayer();
        for (var passenger : player.getPassengers()) {
            if (passenger instanceof Player target) {
                if (manager.isFake(target)) {
                    player.removePassenger(target);
                }
            }
        }
    }

    /**
     * 右键假人打开其背包
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void invsee(@NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target) || !manager.isFake(target)) {
            return;
        }

        manager.openInventory(event.getPlayer(), target);
    }

    /**
     * 如果打开背包的行为是默认实现, 则禁止玩家操作背包(因为默认实现有可能导致物品被异常挪到假人的装备栏从而看不到)
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onClickFakePlayerInventory(@NotNull InventoryClickEvent event) {
        var top = event.getView().getTopInventory();
        if (top.getType() == InventoryType.PLAYER && (top.getHolder() instanceof Player player && manager.isFake(player))) {
            if (event.getView().getTitle().startsWith(ConstantPool.UNMODIFIABLE_INVENTORY_TITLE_PREFIX)) {
                event.setCancelled(true);
            }
        }
    }

}
