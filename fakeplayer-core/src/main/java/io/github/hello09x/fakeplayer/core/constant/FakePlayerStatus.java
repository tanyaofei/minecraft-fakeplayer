package io.github.hello09x.fakeplayer.core.constant;

/**
 * @author tanyaofei
 * @since 2024/7/25
 **/
public enum FakePlayerStatus {

    /**
     * 玩家对象已创建, 但是未创建 Minecraft 实体
     */
    PENDING,

    /**
     * 玩家正在创建实体过程中
     */
    SPAWNING,

    /**
     * 玩家创建实体完毕
     */
    SPAWNED;

    public static String METADATA_KEY = "fakeplayer.status";

}
