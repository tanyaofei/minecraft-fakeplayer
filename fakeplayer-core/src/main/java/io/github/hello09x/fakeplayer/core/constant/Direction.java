package io.github.hello09x.fakeplayer.core.constant;

import io.github.hello09x.bedrock.i18n.TranslateKey;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum Direction implements TranslateKey {

    DOWN("fakeplayer.direction.down"),

    UP("fakeplayer.direction.up"),

    NORTH("fakeplayer.direction.north"),

    SOUTH("fakeplayer.direction.south"),

    WEST("fakeplayer.direction.west"),

    EAST("fakeplayer.direction.east");

    final String translateKey;

    @Override
    public @NotNull String translateKey() {
        return this.translateKey;
    }

}
