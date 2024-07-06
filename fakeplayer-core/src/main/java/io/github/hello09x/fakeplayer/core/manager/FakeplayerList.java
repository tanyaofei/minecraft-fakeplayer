package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.entity.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Stream;

@Singleton
public class FakeplayerList {

    private final Map<String, FakePlayer> playersByName = new HashMap<>();

    private final Map<UUID, FakePlayer> playersByUUID = new HashMap<>();

    private final Map<String, List<FakePlayer>> playersByCreator = new HashMap<>();

    /**
     * 添加一个假人到假人清单
     *
     * @param player 假人
     */
    public void add(@NotNull FakePlayer player) {
        this.playersByName.put(player.getName(), player);
        this.playersByUUID.put(player.getUUID(), player);
        this.playersByCreator.computeIfAbsent(player.getCreator().getName(), key -> new LinkedList<>()).add(player);
    }

    /**
     * 通过假人的名称获取假人
     *
     * @param name 名称
     * @return 假人
     */
    public @Nullable FakePlayer getByName(@NotNull String name) {
        return Optional.ofNullable(this.playersByName.get(name)).map(this::checkOnline).orElse(null);
    }

    /**
     * 通过 UUID 获取假人
     *
     * @param uuid UUID
     * @return 假人
     */
    public @Nullable FakePlayer getByUUID(@NotNull UUID uuid) {
        return Optional.ofNullable(this.playersByUUID.get(uuid)).map(this::checkOnline).orElse(null);
    }

    /**
     * 获取创建者创建的所有假人
     *
     * @param creator 创建者
     * @return 假人
     */
    public @NotNull @Unmodifiable List<FakePlayer> getByCreator(@NotNull String creator) {
        return Optional.ofNullable(this.playersByCreator.get(creator)).map(Collections::unmodifiableList).orElse(Collections.emptyList());
    }

    /**
     * 移除一个假人
     *
     * @param player 假人
     */
    public void remove(@NotNull FakePlayer player) {
        this.playersByName.remove(player.getName());
        this.playersByUUID.remove(player.getUUID());
        Optional.ofNullable(this.playersByCreator.get(player.getCreator().getName())).map(players -> players.remove(player));
    }

    /**
     * 通过 UUID 移除假人
     *
     * @param uuid UUID
     * @return 被移除的假人
     */
    public @Nullable FakePlayer removeByUUID(@NotNull UUID uuid) {
        var player = getByUUID(uuid);
        if (player == null) {
            return null;
        }
        this.remove(player);
        return player;
    }

    /**
     * 获取创建的数量
     *
     * @param creator 玩家
     * @return 数量
     */
    public int countByCreator(@NotNull String creator) {
        return Optional
                .ofNullable(this.playersByCreator.get(creator))
                .map(List::size)
                .orElse(0);
    }

    /**
     * 获取所有假人
     *
     * @return 假人
     */
    public @NotNull @Unmodifiable List<FakePlayer> getAll() {
        return List.copyOf(this.playersByUUID.values());
    }

    /**
     * 检测假人是否在线, 如果不在线了则移除并返回 {@code null}
     *
     * @param player 假人
     * @return 假人
     */
    private @Nullable FakePlayer checkOnline(@NotNull FakePlayer player) {
        if (!player.isOnline()) {
            this.remove(player);
            return null;
        }

        return player;
    }

    public @NotNull Stream<FakePlayer> stream() {
        return this.playersByUUID.values().stream();
    }

    public int count() {
        return this.playersByUUID.size();
    }

}
