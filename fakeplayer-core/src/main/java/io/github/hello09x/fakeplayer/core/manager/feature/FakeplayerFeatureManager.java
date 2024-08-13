package io.github.hello09x.fakeplayer.core.manager.feature;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import io.github.hello09x.fakeplayer.core.repository.model.UserConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class FakeplayerFeatureManager {

    private final UserConfigRepository repository;
    private final FakeplayerConfig config;

    @Inject
    public FakeplayerFeatureManager(UserConfigRepository repository, FakeplayerConfig config) {
        this.repository = repository;
        this.config = config;
    }

    private @NotNull String getDefaultOption(@NotNull Feature key) {
        return Optional.ofNullable(config.getDefaultFeatures().get(key)).filter(option -> key.getOptions().contains(option)).orElse(key.getDefaultOption());
    }

    public @NotNull FeatureInstance getFeature(@NotNull Player player, @NotNull Feature key) {
        if (!key.testPermissions(player)) {
            return new FeatureInstance(key, this.getDefaultOption(key));
        }

        String value = Optional.ofNullable(repository.selectByPlayerIdAndKey(player.getUniqueId(), key))
                               .map(UserConfig::value)
                               .orElseGet(() -> this.getDefaultOption(key));

        return new FeatureInstance(key, value);
    }

    public @NotNull Map<Feature, FeatureInstance> getFeatures(@NotNull CommandSender sender) {
        Map<Feature, UserConfig> userConfigs;
        if (sender instanceof Player player) {
            userConfigs = repository.selectByPlayerId(player.getUniqueId()).stream().collect(Collectors.toMap(UserConfig::key, Function.identity()));
        } else {
            userConfigs = Collections.emptyMap();
        }

        var configs = new LinkedHashMap<Feature, FeatureInstance>(Feature.values().length, 1.0F);
        for (var key : Feature.values()) {
            String value;
            if (!key.testPermissions(sender)) {
                value = this.getDefaultOption(key);
            } else {
                value = Optional.ofNullable(userConfigs.get(key)).map(UserConfig::value).orElseGet(() -> this.getDefaultOption(key));
            }
            configs.put(key, new FeatureInstance(key, value));
        }

        return configs;
    }

    public void setFeature(@NotNull Player player, @NotNull Feature key, @NotNull String value) {
        this.repository.saveOrUpdate(new UserConfig(
                null,
                player.getUniqueId(),
                key,
                value
        ));
    }

}
