package io.github.hello09x.fakeplayer.manager;

import io.github.hello09x.fakeplayer.entity.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FakeplayerList {

    public final static FakeplayerList instance = new FakeplayerList();

    private final Map<String, FakePlayer> playersByName = new HashMap<>();

    private final Map<UUID, FakePlayer> playersByUUID = new HashMap<>();

    private final Map<String, List<FakePlayer>> playersByCreator = new HashMap<>();

    public void add(@NotNull FakePlayer player) {
        this.playersByName.put(player.getName(), player);
        this.playersByUUID.put(player.getUniqueId(), player);
        this.playersByCreator.computeIfAbsent(player.getCreator(), key -> new LinkedList<>());
        this.playersByCreator.get(player.getCreator()).add(player);
    }

    public @Nullable FakePlayer getByName(@NotNull String name) {
        return Optional.ofNullable(this.playersByName.get(name)).map(this::checkOnline).orElse(null);
    }

    public @Nullable FakePlayer getByUUID(@NotNull UUID uuid) {
        return Optional.ofNullable(this.playersByUUID.get(uuid)).map(this::checkOnline).orElse(null);
    }

    public @NotNull List<FakePlayer> getByCreator(@NotNull String creator) {
        return Optional.ofNullable(this.playersByCreator.get(creator)).map(Collections::unmodifiableList).orElse(Collections.emptyList());
    }

    public void remove(@NotNull FakePlayer player) {
        this.playersByName.remove(player.getName());
        this.playersByUUID.remove(player.getUniqueId());
        Optional.ofNullable(this.playersByCreator.get(player.getCreator())).map(players -> players.remove(player));
    }

    public @Nullable FakePlayer removeByUUID(@NotNull UUID uniqueId) {
        var player = getByUUID(uniqueId);
        if (player == null) {
            return null;
        }
        remove(player);
        return player;
    }

    public List<FakePlayer> getAll() {
        return List.copyOf(this.playersByUUID.values());
    }

    private @Nullable FakePlayer checkOnline(@NotNull FakePlayer player) {
        if (!player.isOnline()) {
            this.playersByName.remove(player.getName());
            this.playersByUUID.remove(player.getUniqueId());
            return null;
        }

        return player;
    }

}
