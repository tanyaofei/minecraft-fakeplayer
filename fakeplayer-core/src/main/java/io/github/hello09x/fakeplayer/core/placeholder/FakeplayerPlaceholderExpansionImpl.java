package io.github.hello09x.fakeplayer.core.placeholder;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author tanyaofei
 * @since 2024/8/15
 **/
@Singleton
public class FakeplayerPlaceholderExpansionImpl extends PlaceholderExpansion implements FakeplayerPlaceholderExpansion, Listener {

    private final FakeplayerManager manager;

    @Inject
    public FakeplayerPlaceholderExpansionImpl(FakeplayerManager manager) {
        this.manager = manager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return Main.getInstance().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return Iterables.getFirst(Main.getInstance().getPluginMeta().getAuthors(), "hello09x");
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player, @NotNull String params) {
        // /papi parse --null fakeplayer_total
        if (params.equalsIgnoreCase("total")) {
            return String.valueOf(manager.getSize());
        }

        // /papi parse CONSOLE_1 fakeplayer_creator
        if (params.equalsIgnoreCase("creator") && player != null && manager.isFake(player)) {
            return Optional.ofNullable(manager.getCreatorName(player)).orElse(params);
        }

        return params;

    }

    @EventHandler
    public void unregister(@NotNull PluginDisableEvent event) {
        if (event.getPlugin() == Main.getInstance()) {
            this.unregister();
        }
    }
}
