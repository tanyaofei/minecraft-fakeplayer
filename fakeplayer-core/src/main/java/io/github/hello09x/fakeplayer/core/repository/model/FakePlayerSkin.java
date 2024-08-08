package io.github.hello09x.fakeplayer.core.repository.model;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author tanyaofei
 * @since 2024/8/8
 **/
public record FakePlayerSkin(

        @NotNull
        UUID playerId,

        @NotNull
        UUID creatorId,

        @NotNull
        UUID targetId

) {


}
