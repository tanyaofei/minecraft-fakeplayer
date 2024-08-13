package io.github.hello09x.fakeplayer.core.manager.feature;

import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import org.jetbrains.annotations.NotNull;

/**
 * @author tanyaofei
 * @since 2024/8/13
 **/
public record FeatureInstance(

        @NotNull
        Feature key,

        @NotNull
        String value

) {

    public @NotNull String asString() {
        return value;
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(value);
    }

}
