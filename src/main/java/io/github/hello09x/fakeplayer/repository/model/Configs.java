package io.github.hello09x.fakeplayer.repository.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Configs {

    /**
     * 开启碰撞箱
     */
    Config<Boolean> collidable = build(
            "collidable",
            "碰撞箱",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    /**
     * 无敌
     */
    Config<Boolean> invulnerable = build(
            "invulnerable",
            "无敌模式",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    /**
     * 看向实体
     */
    Config<Boolean> look_at_entity = build(
            "look_at_entity",
            "目视实体",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    /**
     * 拾取物品
     */
    Config<Boolean> pickup_items = build(
            "pickup_items",
            "拾取物品",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    @SuppressWarnings("SameParameterValue")
    private static <T> Config<T> build(
            @NotNull String name,
            @NotNull String label,
            @NotNull T defaultValue,
            @NotNull List<String> options,
            @NotNull Function<String, T> mapper
    ) {
        var config = new Config<>(name, label, defaultValue, options, mapper);
        Constants.values.put(name, config);
        return config;
    }

    @SuppressWarnings("unchecked")
    static <T> @NotNull Config<T> valueOf(@NotNull String name) {
        var config = Constants.values.get(name);
        if (config == null) {
            throw new IllegalStateException(String.format("No config named '%s'", name));
        }
        return (Config<T>) config;
    }

    static Config<?>[] values() {
        return Constants.values.values().toArray(new Config[0]);
    }

    final class Constants {
        private final static Map<String, Config<?>> values = new HashMap<>();
    }

}
