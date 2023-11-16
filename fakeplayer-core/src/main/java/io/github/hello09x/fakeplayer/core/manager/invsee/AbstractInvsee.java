package io.github.hello09x.fakeplayer.core.manager.invsee;

import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.bedrock.util.Components;
import io.github.hello09x.fakeplayer.core.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

public abstract class AbstractInvsee implements Invsee {

    protected final MiniMessage miniMessage = MiniMessage.miniMessage();
    protected final I18n i18n = Main.getI18n();

    protected void setTitle(@Nullable InventoryView view, @NotNull Player owner) {
        if (view == null) {
            return;
        }

        view.setTitle(Components.asString(miniMessage.deserialize(
                i18n.asString("fakeplayer.manager.inventory.title"),
                Placeholder.component("name", text(owner.getName()))
        )));
    }


}
