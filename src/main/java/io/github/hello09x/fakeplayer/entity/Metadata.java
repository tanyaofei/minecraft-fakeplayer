package io.github.hello09x.fakeplayer.entity;

import io.github.hello09x.fakeplayer.Main;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public enum Metadata {

    CREATOR("creator"),
    CREATOR_IP("creator-ip"),
    NAME_SOURCE("name-source"),
    NAME_SEQUENCE("name-sequence"),
    ACTION_TASK_ID("action-task-id");

    public final String key;

    Metadata(@NotNull String key) {
        this.key = "fakeplayer:" + key;
    }

    public void set(@NotNull Player player, @NotNull Object value) {
        player.setMetadata(this.key, new FixedMetadataValue(Main.getInstance(), value));
    }

    public void remove(@NotNull Player player) {
        if (player.hasMetadata(this.key)) {
            player.removeMetadata(this.key, Main.getInstance());
        }
    }

    public @NotNull MetadataValue get(@NotNull Player player) {
        return player.getMetadata(this.key).get(0);
    }

    public @NotNull Optional<MetadataValue> getOptional(@NotNull Player player) {
        var metadata = player.getMetadata(this.key);
        if (metadata.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(metadata.get(0));
    }

}
