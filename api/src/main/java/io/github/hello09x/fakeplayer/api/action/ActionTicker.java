package io.github.hello09x.fakeplayer.api.action;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ServiceLoader;

public interface ActionTicker {

    static @NotNull ActionTicker create(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        var instance = ServiceLoader.load(
                ActionTicker.class,
                ActionTicker.class.getClassLoader()
        ).findFirst().orElseThrow();
        instance.init(player, action, setting);
        return instance;
    }

    void tick();

    void inactiveTick();

    void stop();

    void init(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting);

}
