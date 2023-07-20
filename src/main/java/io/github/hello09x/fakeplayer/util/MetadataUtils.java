package io.github.hello09x.fakeplayer.util;

import net.minecraft.data.DataProvider;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableRowSorter;

public class MetadataUtils {

    public static @Nullable MetadataValue getFirst(@NotNull Metadatable keeper, @NotNull String key) {
        var meta = keeper.getMetadata(key);
        if (meta.isEmpty()) {
            return null;
        }
        return meta.get(0);
    }

    public static @NotNull MetadataValue[] get(@NotNull Metadatable keeper, String... keys) {
        if (keys.length == 0) {
            return new MetadataValue[0];
        }

        var ret = new MetadataValue[keys.length];
        int i = 0;
        for(var key: keys) {
            var meta = keeper.getMetadata(key);
            if (meta.isEmpty()) {
                throw new IllegalArgumentException(String.format("Could not find metadata key named '%s' from '%s'", key, keeper));
            }
            ret[i++] = meta.get(0);
        }

        return ret;
    }

}
