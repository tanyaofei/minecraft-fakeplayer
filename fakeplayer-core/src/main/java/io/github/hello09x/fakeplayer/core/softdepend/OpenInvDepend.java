package io.github.hello09x.fakeplayer.core.softdepend;

import com.lishid.openinv.IOpenInv;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OpenInvDepend {

    public final static OpenInvDepend instance = new OpenInvDepend();
    private Bridge bridge = null;

    public OpenInvDepend() {
        try {
            if (Bukkit.getPluginManager().getPlugin("OpenInv") != null) {
                bridge = new Bridge();
            }
        } catch (Throwable ignored) {

        }
    }

    public boolean openInventory(@NotNull Player player, @NotNull Player target) {
        if (bridge == null) {
            return false;
        }
        try {
            bridge.openInventory(player, target);
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    private final static class Bridge {
        private IOpenInv iOpenInv = (IOpenInv) Bukkit.getPluginManager().getPlugin("OpenInv");

        public void openInventory(@NotNull Player player, @NotNull Player target) throws Throwable {
            iOpenInv.openInventory(player, iOpenInv.getSpecialInventory(target, true));
        }

    }


}
