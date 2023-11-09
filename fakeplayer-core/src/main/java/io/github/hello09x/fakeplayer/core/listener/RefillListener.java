package io.github.hello09x.fakeplayer.core.listener;


import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import io.github.hello09x.bedrock.util.Blocks;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.command.Permission;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RefillListener implements Listener {

    public final static RefillListener instance = new RefillListener();

    private final FakeplayerManager manager = FakeplayerManager.instance;
    private final FakeplayerConfig config = FakeplayerConfig.instance;

    /**
     * 消耗物品自动填装
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemUse(@NotNull PlayerItemConsumeEvent event) {
        var player = event.getPlayer();
        if (!manager.isRefillable(player)) {
            return;
        }

        var slot = event.getHand();
        var item = player.getInventory().getItem(slot);
        if (item.getAmount() != 1) {
            return;
        }

        this.refillLater(player, slot, item);
    }

    /**
     * 放置方块自动填装
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        var player = event.getPlayer();
        if (!manager.isRefillable(player)) {
            return;
        }

        var slot = event.getHand();
        var item = player.getInventory().getItem(slot);
        if (item.getAmount() != 1) {
            return;
        }

        this.refillLater(player, slot, item);
    }

    /**
     * 物品损坏自动填装
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemBreak(@NotNull PlayerItemBreakEvent event) {
        var player = event.getPlayer();
        if (!manager.isRefillable(player)) {
            return;
        }

        var item = event.getBrokenItem();
        var slot = this.getHoldingHand(player, item);
        if (slot == null) {
            return;
        }

        this.refillLater(player, slot, item);
    }

    /**
     * 发射投掷物, 如扔喷溅型药水 自动填装
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(@NotNull PlayerLaunchProjectileEvent event) {
        var player = event.getPlayer();
        if (!manager.isRefillable(event.getPlayer())) {
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
        this.refillLater(player, slot, item);
    }

    /**
     * 在下一 tick 填装物品
     *
     * @param target 玩家
     * @param slot   填充位置
     * @param item   要填充的物品
     */
    public void refillLater(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
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

            if (!this.refillFromInventory(target, slot, requires)) {
                if (Optional.ofNullable(manager.getCreator(target))
                        .filter(creator -> creator.hasPermission(Permission.refillFromChest))
                        .isPresent()
                ) {
                    this.refillFromNearbyChest(target, slot, requires);
                }
            }

        }, 1);
    }

    /**
     * 从背包里补货
     *
     * @param target 假人
     * @param slot   补充到哪只手
     * @param item   需要补货的物品
     * @return 是否补货了
     */
    private boolean refillFromInventory(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        var inv = target.getInventory();
        for (int i = inv.getSize() - 1; i >= 0; i--) {
            var replacement = inv.getItem(i);
            if (replacement != null && replacement.isSimilar(item)) {
                inv.setItem(slot, replacement);
                inv.setItem(i, new ItemStack(Material.AIR));
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
    public void refillFromNearbyChest(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        var blocks = Blocks.getNearbyBlocks(target.getLocation(), 4, Material.CHEST);
        for (var block : blocks) {
            var openEvent = new PlayerInteractEvent(
                    target,
                    Action.RIGHT_CLICK_BLOCK,
                    target.getInventory().getItemInOffHand(),
                    block,
                    BlockFace.NORTH
            );
            if (!openEvent.callEvent()) {
                // Could not open this inventory cause by other plugins
                continue;
            }

            if (target.openInventory(((Chest) block.getState()).getBlockInventory()) == null) {
                continue;
            }

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                var view = target.getOpenInventory();
                var inv = view.getTopInventory();
                if (inv.getType() != InventoryType.CHEST) {
                    // closed by other plugins
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
                            // canceled by other plugins
                            return;
                        }

                        target.getInventory().setItem(slot, replacement);
                        inv.setItem(i, new ItemStack(Material.AIR));
                        return;
                    }
                }
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
