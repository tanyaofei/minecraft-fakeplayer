package io.github.hello09x.fakeplayer.core.listener;


import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefillListener implements Listener {

    public final static RefillListener instance = new RefillListener();

    private final FakeplayerManager manager = FakeplayerManager.instance;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemBreak(@NotNull PlayerItemBreakEvent event) {
        var player = event.getPlayer();
        if (!manager.isRefillable(player)) {
            return;
        }

        var item = event.getBrokenItem();
        var slot = getHoldingHand(player, item);
        if (slot == null) {
            return;
        }

        this.refillLater(player, slot, item);
    }

    /**
     * 发射投掷物, 如扔喷溅型药水
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(@NotNull PlayerLaunchProjectileEvent event) {
        var player = event.getPlayer();
        if (!manager.isRefillable(event.getPlayer())) {
            return;
        }
        var item = event.getItemStack();
        if (item.getAmount() != 1) {
            return;
        }

        var slot = getHoldingHand(player, item);
        if (slot == null) {
            return;
        }
        this.refillLater(player, slot, item);
    }


    public void refillLater(@NotNull Player player, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        var requires = item.clone();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            var inv = player.getInventory();
            var current = inv.getItem(slot);
            for (int i = inv.getSize(); i >= 0; i--) {
                var replacement = inv.getItem(i);
                if (replacement != null && replacement.isSimilar(requires)) {
                    inv.setItem(slot, replacement);
                    inv.setItem(i, current);
                    break;
                }
            }
        }, 1);
    }

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
