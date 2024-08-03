package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.devtools.database.jdbc.RowMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author tanyaofei
 * @since 2024/8/3
 **/
public record FakePlayerProfile(

        Integer id,

        @NotNull
        String name,

        @NotNull
        String uuid

) {

    public static class FakePlayerProfileRowMapper implements RowMapper<FakePlayerProfile> {

        public final static FakePlayerProfileRowMapper instance = new FakePlayerProfileRowMapper();

        @Override
        public @Nullable FakePlayerProfile mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
            return new FakePlayerProfile(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("uuid")
            );
        }

    }


}
