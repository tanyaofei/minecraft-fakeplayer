package io.github.hello09x.fakeplayer.core.constant;

import com.google.common.collect.Iterables;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MetadataKeys {


    String SELECTION = "fakeplayer:selection";

    String REPLENISH = "fakeplayer:replenish";

    String SPAWNED_AT = "fakeplayer:spawned_at";

    static @Nullable Integer getSpawnedAt(@NotNull Player player) {
        var value = Iterables.getFirst(player.getMetadata(SPAWNED_AT), null);
        if (value == null) {
            return null;
        }
        return value.asInt();
    }

}
