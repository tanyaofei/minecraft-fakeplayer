package io.github.hello09x.fakeplayer.core.manager.invsee;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultInvseeImpl extends AbstractInvsee {

    @Override
    public void openInventory(@NotNull Player visitor, @NotNull Player visited) {
        var view = visitor.openInventory(visited.getInventory());
        super.setTitle(view, visited);
    }

}
