package io.github.hello09x.fakeplayer.util;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SeedUUID {

    private @NotNull UUID seed;

    public SeedUUID(@NotNull String seed) {
        this.seed = UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }

    public synchronized UUID uuid() {
        var next = UUID.nameUUIDFromBytes(seed.toString().getBytes(StandardCharsets.UTF_8));
        this.seed = next;
        return next;
    }

}
