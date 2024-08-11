package io.github.hello09x.fakeplayer.core.manager;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.utils.BlockUtils;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.command.Permission;
import io.github.hello09x.fakeplayer.core.constant.MetadataKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author tanyaofei
 * @since 2024/8/11
 **/
@Singleton
public class FakeplayerReplenishManager implements Listener {

    private final FakeplayerManager manager;

    @Inject
    public FakeplayerReplenishManager(FakeplayerManager manager) {
        this.manager = manager;
    }

    /**
     * 设置假人是否自动填装
     *
     * @param target    假人
     * @param replenish 是否自动补货
     */
    public void setReplenish(@NotNull Player target, boolean replenish) {
        if (!replenish) {
            target.removeMetadata(MetadataKeys.REPLENISH, Main.getInstance());
        } else {
            target.setMetadata(MetadataKeys.REPLENISH, new FixedMetadataValue(Main.getInstance(), true));
        }
    }

    /**
     * 判断假人是否自动补货
     *
     * @param target 假人
     * @return 是否自动补货
     */
    public boolean isReplenish(@NotNull Player target) {
        return target.hasMetadata(MetadataKeys.REPLENISH);
    }

    /**
     * 消耗物品自动填装
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemUse(@NotNull PlayerItemConsumeEvent event) {
        var player = event.getPlayer();
        if (!this.isReplenish(player)) {
            return;
        }

        var slot = event.getHand();
        var item = player.getInventory().getItem(slot);
        if (item.getAmount() != 1) {
            return;
        }

        this.replenishLater(player, slot, item);
    }

    /**
     * 放置方块自动填装
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        var player = event.getPlayer();
        if (!this.isReplenish(player)) {
            return;
        }

        var slot = event.getHand();
        var item = player.getInventory().getItem(slot);
        if (item.getAmount() != 1) {
            return;
        }

        this.replenishLater(player, slot, item);
    }

    /**
     * 物品损坏自动填装
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemBreak(@NotNull PlayerItemBreakEvent event) {
        var player = event.getPlayer();
        if (!this.isReplenish(player)) {
            return;
        }

        var item = event.getBrokenItem();
        var slot = this.getHoldingHand(player, item);
        if (slot == null) {
            return;
        }

        this.replenishLater(player, slot, item);
    }

    /**
     * 发射投掷物, 如扔喷溅型药水 自动填装
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(@NotNull PlayerLaunchProjectileEvent event) {
        var player = event.getPlayer();
        if (!this.isReplenish(event.getPlayer())) {
            return;
        }
        var item = event.getItemStack();
        if (item.getAmount() != 1) {
            return;
        }

        var slot = this.getHoldingHand(player, item);
        if (slot == null) {
            return;
        }
        this.replenishLater(player, slot, item);
    }

    /**
     * 在下一 tick 填装物品
     *
     * @param target 玩家
     * @param slot   填充位置
     * @param item   要填充的物品
     */
    public void replenishLater(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        var requires = item.clone();
        item = null;    // 以防下面的代码用到了这个值

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (!target.isOnline()) {
                return;
            }
            var held = target.getInventory().getItem(slot);
            if (!held.getType().isAir() && held.getAmount() != 0) {
                return;
            }

            if (!this.replenishFromInventory(target, slot, requires)) {
                if (Optional.ofNullable(manager.getCreator(target))
                            .filter(creator -> creator.hasPermission(Permission.replenishFromChest))
                            .isPresent()
                ) {
                    this.replenishFromNearbyChest(target, slot, requires);
                }
            }

        }, 1);  // delay 1 是因为要等手上的物品在此 tick 消耗完
    }

    /**
     * 从背包里补货
     *
     * @param target 假人
     * @param slot   补充到哪只手
     * @param item   需要补货的物品
     * @return 是否补货了
     */
    private boolean replenishFromInventory(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        var inv = target.getInventory();
        for (int i = inv.getSize() - 1; i >= 0; i--) {
            var replacement = inv.getItem(i);
            if (replacement != null && replacement.isSimilar(item)) {
                inv.setItem(slot, replacement);
                inv.setItem(i, null);
                return true;
            }
        }
        return false;
    }

    /**
     * 从附近的箱子里补货
     *
     * @param target 假人
     * @param slot   补充到哪只手
     * @param item   需要补货的物品
     */
    public void replenishFromNearbyChest(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        var blocks = BlockUtils.getNearbyBlocks(target.getLocation(), 4, Material.CHEST);
        for (var block : blocks) {
            var openEvent = new PlayerInteractEvent(
                    target,
                    Action.RIGHT_CLICK_BLOCK,
                    target.getInventory().getItemInOffHand(),
                    block,
                    BlockFace.NORTH
            );
            if (!openEvent.callEvent()) {
                // 无法打开箱子
                continue;
            }

            if (target.openInventory(((Chest) block.getState()).getBlockInventory()) == null) {
                continue;
            }

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                var view = target.getOpenInventory();
                var inv = view.getTopInventory();
                if (inv.getType() != InventoryType.CHEST) {
                    // 被其他插件取消了, 变成打开自己的背包了
                    return;
                }
                for (int i = inv.getSize() - 1; i >= 0; i--) {
                    var replacement = inv.getItem(i);
                    if (replacement != null && replacement.isSimilar(item)) {
                        var event = new InventoryClickEvent(
                                view,
                                InventoryType.SlotType.CONTAINER,
                                i,
                                ClickType.SHIFT_LEFT,
                                InventoryAction.MOVE_TO_OTHER_INVENTORY
                        );
                        if (!event.callEvent()) {
                            // 无法操作箱子
                            break;
                        }

                        target.getInventory().setItem(slot, replacement);
                        inv.setItem(i, null);
                        break;
                    }
                }
                target.closeInventory(InventoryCloseEvent.Reason.PLAYER);
            }, 20);
            return;
        }
    }

    /**
     * 获取玩家哪只手持有对应的物品
     *
     * @param player 玩家
     * @param item   对应的物品
     * @return 哪只手
     */
    private @Nullable EquipmentSlot getHoldingHand(@NotNull Player player, @NotNull ItemStack item) {
        var inv = player.getInventory();
        if (item.equals(inv.getItemInMainHand())) {
            return EquipmentSlot.HAND;
        } else if (item.equals(inv.getItemInOffHand())) {
            return EquipmentSlot.OFF_HAND;
        } else {
            return null;
        }
    }

}
