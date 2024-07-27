package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.devtools.database.jdbc.RowMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author tanyaofei
 * @since 2024/7/27
 **/
public class UserConfigRowMapper implements RowMapper<UserConfig> {

    public final static UserConfigRowMapper instance = new UserConfigRowMapper();

    @Override
    public @Nullable UserConfig mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        return new UserConfig(
                rs.getInt("id"),
                rs.getString("player_id"),
                rs.getString("key"),
                rs.getString("value")
        );
    }
}
