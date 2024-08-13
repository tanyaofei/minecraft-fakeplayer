package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.command.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author tanyaofei
 * @since 2024/8/13
 **/
@Getter
@AllArgsConstructor
public enum FeatureKey implements Translatable, Singletons {

    /**
     * 是否具有碰撞箱
     */
    collidable(
            "fakeplayer.config.collidable",
            List.of(Permission.config),
            List.of("true", "false"),
            "true",
            faker -> String.valueOf(faker.isCollidable()),
            (faker, value) -> faker.setCollidable(Boolean.parseBoolean(value))
    ),

    /**
     * 是否无敌
     */
    invulnerable(
            "fakeplayer.config.invulnerable",
            List.of(Permission.config),
            List.of("true", "false"),
            "true",
            faker -> String.valueOf(faker.isInvulnerable()),
            (faker, value) -> faker.setInvulnerable(Boolean.parseBoolean(value))
    ),

    /**
     * 是否自动看向实体
     */
    look_at_entity(
            "fakeplayer.config.look_at_entity",
            List.of(Permission.config),
            List.of("true", "false"),
            "false",
            faker -> String.valueOf(actionManager.get().hasActiveAction(faker, ActionType.LOOK_AT_NEAREST_ENTITY)),
            (faker, value) -> {
                if (Boolean.parseBoolean(value)) {
                    actionManager.get().setAction(faker, ActionType.LOOK_AT_NEAREST_ENTITY, ActionSetting.continuous());
                } else {
                    actionManager.get().setAction(faker, ActionType.LOOK_AT_NEAREST_ENTITY, ActionSetting.stop());
                }
            }
    ),

    /**
     * 是否能够拾取物品
     */
    pickup_items(
            "fakeplayer.config.pickup_items",
            List.of(Permission.config),
            List.of("true", "false"),
            "true",
            faker -> String.valueOf(faker.getCanPickupItems()),
            (faker, value) -> faker.setCanPickupItems(Boolean.parseBoolean(value))
    ),

    /**
     * 是否使用皮肤
     */
    skin(
            "fakeplayer.config.skin",
            List.of(Permission.config),
            List.of("true", "false"),
            "true",
            null,
            null
    ),

    /**
     * 是否自动补货
     */
    replenish(
            "fakeplayer.config.replenish",
            List.of(Permission.config, Permission.replenish),
            List.of("true", "false"),
            "false",
            faker -> String.valueOf(replenishManager.get().isReplenish(faker)),
            (faker, value) -> replenishManager.get().setReplenish(faker, Boolean.parseBoolean(value))
    ),

    /**
     * 是否自动钓鱼
     */
    autofish(
            "fakeplayer.config.autofish",
            List.of(Permission.config, Permission.autofish),
            List.of("true", "false"),
            "false",
            faker -> String.valueOf(autofishManager.get().isAutofish(faker)),
            (faker, value) -> autofishManager.get().setAutofish(faker, Boolean.parseBoolean(value))
    ),
    ;

    @NotNull
    final String translationKey;

    @NotNull
    final List<String> permissions;

    @NotNull
    final List<String> options;

    @NotNull
    final String defaultOption;

    @Nullable
    final Function<Player, String> detector;

    @Nullable
    final BiConsumer<Player, String> modifier;


    @Override
    public @NotNull String translationKey() {
        return this.translationKey;
    }

    public boolean hasDetector() {
        return this.detector != null;
    }

    public boolean hasModifier() {
        return this.modifier != null;
    }

    public boolean testPermissions(@NotNull CommandSender sender) {
        if (this.permissions.isEmpty()) {
            return true;
        }

        for (var permission : this.permissions) {
            if (!sender.hasPermission(permission)) {
                return false;
            }
        }

        return true;
    }


}
