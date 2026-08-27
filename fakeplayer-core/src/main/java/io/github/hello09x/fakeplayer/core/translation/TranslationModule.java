package io.github.hello09x.fakeplayer.core.translation;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.translation.TranslationConfig;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class TranslationModule extends AbstractModule {

    private final TranslationConfig config;

    public TranslationModule(@NotNull TranslationConfig config) {
        this.config = config;
    }

    @Provides
    @Singleton
    public PluginTranslator pluginTranslator(@NotNull Plugin plugin) {
        var translator = new PluginTranslator(plugin, config);
        GlobalTranslator.translator().addSource(translator);
        return translator;
    }
}
