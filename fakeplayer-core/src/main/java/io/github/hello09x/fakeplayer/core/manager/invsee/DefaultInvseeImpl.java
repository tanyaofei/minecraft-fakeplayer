package io.github.hello09x.fakeplayer.core.manager.invsee;

import io.github.hello09x.devtools.transaction.PluginTranslator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultInvseeImpl extends AbstractInvsee {

    public DefaultInvseeImpl(PluginTranslator translator) {
        super(translator);
    }

    @Override
    public void openInventory(@NotNull Player visitor, @NotNull Player visited) {
        var view = visitor.openInventory(visited.getInventory());
        super.setTitle(view, visited);
    }

}
