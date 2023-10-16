package io.github.hello09x.fakeplayer.api.nms;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface NMSServer {

    @NotNull NMSServerPlayer newPlayer(@NotNull UUID uuid, @NotNull String name);


}
