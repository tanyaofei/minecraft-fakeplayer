package io.github.hello09x.fakeplayer.core.manager.invsee;

import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.bedrock.util.Components;
import io.github.hello09x.fakeplayer.core.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class DefaultInvseeImpl implements Invsee {


    private final I18n i18n = Main.getI18n();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();


    @Override
    public void openInventory(@NotNull Player visitor, @NotNull Player visited) {
        var view = visitor.openInventory(visited.getInventory());
        if (view != null) {
            view.setTitle(Components.asString(miniMessage.deserialize(
                    i18n.asString("fakeplayer.manager.inventory.title"),
                    Placeholder.component("name", text(visited.getName()))
            )));
        }
    }
}
