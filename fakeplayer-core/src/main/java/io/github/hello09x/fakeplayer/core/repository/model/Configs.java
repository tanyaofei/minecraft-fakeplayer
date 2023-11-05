package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.bedrock.i18n.I18n;
import net.kyori.adventure.text.Component;
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
            "config.collidable",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    /**
     * 无敌
     */
    Config<Boolean> invulnerable = build(
            "invulnerable",
            "config.invulnerable",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    /**
     * 看向实体
     */
    Config<Boolean> look_at_entity = build(
            "look_at_entity",
            "config.look_at_entity",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    /**
     * 拾取物品
     */
    Config<Boolean> pickup_items = build(
            "pickup_items",
            "config.pickup_items",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    /**
     * 使用皮肤
     */
    Config<Boolean> skin = build(
            "skin",
            "config.skin",
            true,
            List.of("true", "false"),
            Boolean::valueOf
    );

    @SuppressWarnings("SameParameterValue")
    private static <T> Config<T> build(
            @NotNull String name,
            @NotNull String translateKey,
            @NotNull T defaultValue,
            @NotNull List<String> options,
            @NotNull Function<String, T> mapper
    ) {
        var config = new Config<>(name, translateKey, defaultValue, options, mapper);
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
