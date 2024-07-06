package io.github.hello09x.fakeplayer.core.manager.invsee;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public interface Invsee {

    void openInventory(@NotNull Player visitor, @NotNull Player visited);

}
