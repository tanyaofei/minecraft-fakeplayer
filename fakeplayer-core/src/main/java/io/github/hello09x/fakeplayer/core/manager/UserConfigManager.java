package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class UserConfigManager {

    private final UserConfigRepository repository;

    @Inject
    public UserConfigManager(UserConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * 获取配置值
     * <p>
     * 如果玩家曾经设置过但随后没有了权限, 则返回默认值
     * </p>
     *
     * @param player 玩家
     * @param config 配置项
     * @return 配置值
     */
    public <T> T getConfig(@NotNull Player player, @NotNull Config<T> config) {
        if (!config.hasPermission(player)) {
            return config.defaultValue();
        }

        var value = repository.select(player.getUniqueId(), config);
        if (value == null) {
            return config.defaultValue();
        }
        return config.parser().apply(value);
    }

    /**
     * 获取玩家所有配置项
     * <p>
     * 如果玩家曾经设置过但随后没有了权限, 则不会包含在其中
     * </p>
     *
     * @param sender 玩家
     * @return 玩家有权限的配置项
     */
    public @NotNull Configs getConfigs(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return new Configs(Collections.emptyMap());
        }
        var configs = repository.selectList(player.getUniqueId());
        var values = new HashMap<Config<?>, Object>();
        for (var config : configs) {
            var key = Config.valueOfOpt(config.key()).orElse(null);
            if (key == null || !key.hasPermission(player)) {
                continue;
            }
            values.put(key, key.parser().apply(config.value()));
        }
        return new Configs(values);
    }

    /**
     * 获取配置值
     * <ul>
     *     <li>如果玩家曾经设置过但随后没有了权限, 则为默认值</li>
     *     <li>如果不是玩家, 则为默认值</li>
     * </ul>
     *
     * @param sender 玩家
     * @param config 配置项
     * @return 配置值
     */
    public <T> T getConfig(@NotNull CommandSender sender, @NotNull Config<T> config) {
        if (sender instanceof Player p) {
            return this.getConfig(p, config);
        }
        return config.defaultValue();
    }

    /**
     * 设置配置值
     *
     * @param player 玩家
     * @param config 配置
     * @param value  配置值
     */
    public <T> boolean setConfig(@NotNull Player player, @NotNull Config<T> config, @NotNull T value) {
        if (!config.hasPermission(player)) {
            return false;
        }
        repository.saveOrUpdate(player.getUniqueId(), config, value);
        return true;
    }

    public static class Configs {

        private final Map<Config<?>, Object> values;

        private Configs(@NotNull Map<Config<?>, Object> values) {
            this.values = values;
        }

        public <T> T getOrDefault(@NotNull Config<T> key) {
            var config = values.get(key);
            if (config == null) {
                return key.defaultValue();
            }
            return key.type().cast(config);
        }

    }

}
