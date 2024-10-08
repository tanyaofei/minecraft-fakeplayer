package io.github.hello09x.fakeplayer.core.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.database.jdbc.JdbcTemplate;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import io.github.hello09x.fakeplayer.core.repository.model.UserConfig;
import io.github.hello09x.fakeplayer.core.repository.model.UserConfigRowMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@Singleton
public class UserConfigRepository {

    private final JdbcTemplate jdbc;

    @Inject
    public UserConfigRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.initTables();
    }

    public @Nullable UserConfig selectByPlayerIdAndKey(@NotNull UUID playerId, @NotNull Feature feature) {
        var sql = "select * from user_config where player_id = ? and `key` = ?";
        return jdbc.queryForObject(sql, UserConfigRowMapper.instance, playerId.toString(), feature.name());
    }

    public int saveOrUpdate(@NotNull UserConfig config) {
        var sql = """
                  insert or replace into user_config(
                      id, player_id, `key`, `value`
                  ) values (
                      (select id from user_config where player_id = ? and `key` = ?),
                      ?,
                      ?,
                      ?
                    )
                  """;

        return jdbc.update(sql, config.playerId().toString(), config.key().name(), config.playerId(), config.key().name(), config.value());
    }

    public @NotNull List<UserConfig> selectByPlayerId(@NotNull UUID playerId) {
        var sql = "select * from user_config where player_id = ?";
        return jdbc.query(sql, UserConfigRowMapper.instance, playerId.toString());
    }

    protected void initTables() {
        jdbc.execute("""
                             create table if not exists user_config
                                 (
                                     id        integer  not null primary key autoincrement,
                                     player_id text(36) not null,
                                     `key`       text     not null,
                                     `value`     text     not null
                                 );
                             """);

        jdbc.execute("""
                             create unique index if not exists table_name_player_id_key_uindex
                                        on user_config (player_id, `key`);
                             """);
    }
}
