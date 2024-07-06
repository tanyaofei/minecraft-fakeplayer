package io.github.hello09x.fakeplayer.core.listener;

import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
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
        var passengers = player.getPassengers();
        if (passengers.isEmpty()) {
            return;
        }

        for (var passenger : passengers) {
            if (!(passenger instanceof Player target)) {
                continue;
            }
            if (manager.isFake(target)) {
                player.removePassenger(target);
            }
        }
    }

    /**
     * 右键假人打开其背包
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void invsee(@NotNull PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target) || manager.isNotFake(target)) {
            return;
        }

        manager.openInventory(event.getPlayer(), target);
    }

    /**
     * 客户端操作假人背包时(将假人的背包物品移动到玩家背包时)的时候，如果是拖拽的, 会被处理成放置到假人的盔甲栏上
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDragInventory(@NotNull InventoryDragEvent event) {
        var top = event.getView().getTopInventory();
        if (top.getType() == InventoryType.PLAYER && top.getHolder() instanceof Player player && manager.isFake(player)) {
            if (event.getNewItems().keySet().stream().anyMatch(slot -> slot > 35)) {   // > 35 表示从假人背包移动到玩家背包
                event.setCancelled(true);
            }
        }
    }

}
