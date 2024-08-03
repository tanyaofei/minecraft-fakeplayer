package io.github.hello09x.fakeplayer.core.manager.invsee;

import io.github.hello09x.devtools.core.translation.PluginTranslator;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.devtools.core.utils.SingletonSupplier;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public abstract class AbstractInvsee implements Invsee {

    protected final PluginTranslator translator;

    protected final static SingletonSupplier<FakeplayerManager> manager = new SingletonSupplier<>(() -> Main.getInjector().getInstance(FakeplayerManager.class));

    protected AbstractInvsee(PluginTranslator translator) {
        this.translator = translator;
    }

    protected void setTitle(@Nullable InventoryView view, @NotNull Player owner) {
        if (view == null) {
            return;
        }

        var locale = Optional.ofNullable(manager.get().getCreator(owner)).map(TranslatorUtils::getLocale).orElseGet(() -> TranslatorUtils.getDefaultLocale(Main.getInstance()));
        view.setTitle(ComponentUtils.toString(translatable(
                "fakeplayer.manager.inventory.title",
                text(owner.getName())
        ), locale));
    }


}
