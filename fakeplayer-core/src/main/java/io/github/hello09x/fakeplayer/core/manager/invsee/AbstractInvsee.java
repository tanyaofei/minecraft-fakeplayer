package io.github.hello09x.fakeplayer.core.manager.invsee;

import io.github.hello09x.devtools.core.transaction.PluginTranslator;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

public abstract class AbstractInvsee implements Invsee {

    protected final PluginTranslator translator;

    protected AbstractInvsee(PluginTranslator translator) {
        this.translator = translator;
    }

    protected void setTitle(@Nullable InventoryView view, @NotNull Player owner) {
        if (view == null) {
            return;
        }

        view.setTitle(ComponentUtils.asString(translator.translate(
                "fakeplayer.manager.inventory.title",
                null,
                Placeholder.component("name", text(owner.getName()))
        )));
    }


}
