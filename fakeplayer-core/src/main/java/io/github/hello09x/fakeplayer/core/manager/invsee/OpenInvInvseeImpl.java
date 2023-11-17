package io.github.hello09x.fakeplayer.core.manager.invsee;

import com.lishid.openinv.IOpenInv;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OpenInvInvseeImpl extends AbstractInvsee {

    private final IOpenInv iOpenInv = (IOpenInv) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("OpenInv"));

    @Override
    public void openInventory(@NotNull Player visitor, @NotNull Player visited) {
        try {
            var view = iOpenInv.openInventory(visitor, iOpenInv.getSpecialInventory(visited, true));
            super.setTitle(view, visited);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

}
