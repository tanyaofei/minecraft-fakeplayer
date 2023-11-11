package io.github.hello09x.fakeplayer.core.repository;

import io.github.hello09x.bedrock.database.Repository;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import io.github.hello09x.fakeplayer.core.repository.model.UserConfig;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserConfigRepository extends Repository<UserConfig> {

    public final static UserConfigRepository instance = new UserConfigRepository(Main.getInstance());

    public UserConfigRepository(Plugin plugin) {
        super(plugin);
    }

    public @Nullable String select(@NotNull UUID playerId, @NotNull Config<?> config) {
        var sql = """
                  select * from user_config
                  where player_id = ?
                  and `key` = ?
                  """;

        return execute(connection -> {
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setString(1, playerId.toString());
                stm.setString(2, config.key());
                return Optional.ofNullable(mapOne(stm.executeQuery()))
                        .map(UserConfig::value)
                        .orElse(null);
            }
        });
    }

    public @NotNull List<UserConfig> selectList(@NotNull UUID playerId) {
        var sql = """
                select * from user_config
                where player_id = ?
                """;
        return execute(connection -> {
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setString(1, playerId.toString());
                return mapMany(stm.executeQuery());
            }
        });
    }

    public <T> int saveOrUpdate(@NotNull UUID playerId, @NotNull Config<T> config, @NotNull T value) {
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

        return execute(connection -> {
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                int i = 1;
                stm.setString(i++, playerId.toString());
                stm.setString(i++, config.key());
                stm.setString(i++, playerId.toString());
                stm.setString(i++, config.key());
                stm.setString(i++, value.toString());
                return stm.executeUpdate();
            }
        });
    }


    @Override
    protected void initTables() {

        execute(connection -> {
            try (Statement stm = connection.createStatement()) {
                stm.execute("""
                            create table if not exists user_config
                             (
                                 id        integer  not null primary key autoincrement,
                                 player_id text(36) not null,
                                 `key`       text     not null,
                                 `value`     text     not null
                             );
                            """);
                stm.execute("""
                            create unique index if not exists table_name_player_id_key_uindex
                                on user_config (player_id, `key`);
                                """);
            }
        });
    }
}
