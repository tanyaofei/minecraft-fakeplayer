package io.github.hello09x.fakeplayer.core.manager.invsee;

import com.google.common.base.Throwables;
import io.github.hello09x.fakeplayer.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public interface Invsee {


    static @NotNull Invsee getInstance() {
        return ImplHolder.instance;
    }


    void openInventory(@NotNull Player visitor, @NotNull Player visited);

    class ImplHolder {

        static Invsee instance;

        static {
            if (Bukkit.getPluginManager().getPlugin("OpenInv") != null) {
                try {
                    instance = new OpenInvInvseeImpl();
                } catch (Throwable e) {
                    Main.getInstance().getLogger().warning(Throwables.getStackTraceAsString(e));
                }
            } else {
                instance = new DefaultInvseeImpl();
            }
        }
    }


}
