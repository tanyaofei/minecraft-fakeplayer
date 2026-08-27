package io.github.hello09x.fakeplayer.core.translation;

import io.github.hello09x.devtools.core.translation.TranslationConfig;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.Translator;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginTranslator implements Translator {

    private final TranslationConfig config;
    private final Key name;
    private final ClassLoader[] classLoaders;
    private final Map<Locale, Map<String, MessageFormat>> translations = new ConcurrentHashMap<>();

    public PluginTranslator(@NotNull Plugin plugin, @NotNull TranslationConfig config) {
        this.config = config;
        this.name = new NamespacedKey(plugin.getName().toLowerCase(Locale.ROOT), "translator");
        this.classLoaders = new ClassLoader[]{
                TranslatorUtils.getDataFolderClassLoader(plugin),
                TranslatorUtils.getJarClassLoader(plugin)
        };
    }

    @Override
    public @NotNull Key name() {
        return name;
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @Nullable Locale locale) {
        var effectiveLocale = locale == null ? config.defaultLocale() : locale;
        var format = translations.computeIfAbsent(effectiveLocale, this::loadLocale).get(key);
        return format == null ? null : (MessageFormat) format.clone();
    }

    private @NotNull Map<String, MessageFormat> loadLocale(@NotNull Locale locale) {
        for (var classLoader : classLoaders) {
            try {
                var bundle = ResourceBundle.getBundle(config.baseName(), locale, classLoader);
                var formats = new HashMap<String, MessageFormat>();
                for (var key : bundle.keySet()) {
                    formats.put(key, new MessageFormat(bundle.getString(key), locale));
                }
                return Map.copyOf(formats);
            } catch (MissingResourceException ignored) {
            }
        }
        return Map.of();
    }

    public void reload() {
        for (var classLoader : classLoaders) {
            ResourceBundle.clearCache(classLoader);
        }
        translations.clear();
    }
}
