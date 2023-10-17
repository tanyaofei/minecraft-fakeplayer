package io.github.hello09x.fakeplayer.core.repository.model;


import io.github.hello09x.bedrock.database.Table;
import io.github.hello09x.bedrock.database.TableField;
import io.github.hello09x.bedrock.database.TableId;

@Table("user_config")
public record UserConfig(

        @TableId("id")
        Integer id,

        @TableField("player_id")
        String playerId,

        @TableField("key")
        String key,

        @TableField("value")
        String value

) {

}
