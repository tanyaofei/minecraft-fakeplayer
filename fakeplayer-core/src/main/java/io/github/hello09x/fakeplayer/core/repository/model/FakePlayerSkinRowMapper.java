package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.devtools.database.jdbc.RowMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author tanyaofei
 * @since 2024/8/8
 **/
public class FakePlayerSkinRowMapper implements RowMapper<FakePlayerSkin> {

    public final static FakePlayerSkinRowMapper instance = new FakePlayerSkinRowMapper();

    @Override
    public @Nullable FakePlayerSkin mapRow(@NotNull ResultSet rs, int i) throws SQLException {
        return new FakePlayerSkin(
                UUID.fromString(rs.getString("player_id")),
                UUID.fromString(rs.getString("creator_id")),
                UUID.fromString(rs.getString("target_id"))
        );
    }
}
