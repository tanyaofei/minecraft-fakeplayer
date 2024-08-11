package io.github.hello09x.fakeplayer.core.repository;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.database.jdbc.JdbcTemplate;
import io.github.hello09x.fakeplayer.core.repository.model.FakePlayerSkin;
import io.github.hello09x.fakeplayer.core.repository.model.FakePlayerSkinRowMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author tanyaofei
 * @since 2024/8/8
 **/
@Singleton
public class FakeplayerSkinRepository {

    private final JdbcTemplate jdbc;

    @Inject
    public FakeplayerSkinRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.initTables();
    }

    @CanIgnoreReturnValue
    public int insertOrUpdate(@NotNull FakePlayerSkin skin) {
        var sql = """
                  insert or replace into fakeplayer_skin (player_id, creator_id, target_id)
                  values (?, ?, ?)
                  """;

        return jdbc.update(sql, skin.playerId().toString(), skin.creatorId().toString(), skin.targetId());
    }

    public @Nullable FakePlayerSkin selectByCreatorIdAndPlayerId(@NotNull UUID creatorId, @NotNull UUID playerId) {
        return jdbc.queryForObject(
                "select * from fakeplayer_skin where creator_id = ? and player_id = ?",
                FakePlayerSkinRowMapper.instance,
                creatorId.toString(),
                playerId.toString()
        );
    }

    public void initTables() {
        this.jdbc.update("""
                         create table if not exists fakeplayer_skin
                         (
                             player_id   text(36) not null,
                             creator_id  text(36) not null,
                             target_id   text(36) not null,
                             constraint fakeplayer_skin_pk
                                 primary key (player_id, creator_id)
                         );
                         """);
    }

}
