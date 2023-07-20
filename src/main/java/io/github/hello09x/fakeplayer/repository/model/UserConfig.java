package io.github.hello09x.fakeplayer.repository.model;

import io.github.tanyaofei.plugin.toolkit.database.Column;
import io.github.tanyaofei.plugin.toolkit.database.Id;
import io.github.tanyaofei.plugin.toolkit.database.Table;

@Table("user_config")
public record UserConfig(

        @Id("id")
        Integer id,

        @Column("player_id")
        String playerId,

        @Column("key")
        String key,

        @Column("value")
        String value

) {

}
