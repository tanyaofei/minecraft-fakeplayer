package io.github.hello09x.fakeplayer.api.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionType {

    /**
     * 攻击实体
     */
    ATTACK("攻击"),

    /**
     * 挖掘
     */
    MINE("挖掘"),

    /**
     * 右键
     */
    USE("右键"),

    /**
     * 跳跃
     */
    JUMP("跳跃"),

    /**
     * 看向附近实体
     */
    LOOK_AT_NEAREST_ENTITY("看向实体"),

    /**
     * 丢弃手上 1 个物品
     */
    DROP_ITEM("丢弃单个物品"),

    /**
     * 丢弃手上整组物品
     */
    DROP_STACK("丢弃整组物品"),

    /**
     * 丢弃背包
     */
    DROP_INVENTORY("丢弃背包物品")

    ;

    @Getter
    final String displayName;

}
