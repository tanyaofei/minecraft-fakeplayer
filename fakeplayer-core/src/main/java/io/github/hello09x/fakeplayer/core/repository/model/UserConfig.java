package io.github.hello09x.fakeplayer.core.repository.model;


import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserConfig(

        Integer id,

        @NotNull
        UUID playerId,

        @NotNull
        FeatureKey key,

        @NotNull
        String value

) {

}
