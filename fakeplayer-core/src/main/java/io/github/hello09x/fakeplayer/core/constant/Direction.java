package io.github.hello09x.fakeplayer.core.constant;

import lombok.AllArgsConstructor;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum Direction implements Translatable {

    DOWN("fakeplayer.direction.down"),

    UP("fakeplayer.direction.up"),

    NORTH("fakeplayer.direction.north"),

    SOUTH("fakeplayer.direction.south"),

    WEST("fakeplayer.direction.west"),

    EAST("fakeplayer.direction.east");

    final String translationKey;

    @Override
    public @NotNull String translationKey() {
        return this.translationKey;
    }
}
