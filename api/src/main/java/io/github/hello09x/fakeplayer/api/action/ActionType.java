package io.github.hello09x.fakeplayer.api.action;

public enum ActionType {

    /**
     * 攻击实体
     */
    ATTACK,

    /**
     * 挖掘
     */
    MINE,

    /**
     * 右键
     */
    USE,

    /**
     * 跳跃
     */
    JUMP,

    /**
     * 看向附近实体
     */
    LOOK_AT_NEAREST_ENTITY,

    /**
     * 丢弃手上 1 个物品
     */
    DROP_ITEM,

    /**
     * 丢弃手上整组物品
     */
    DROP_STACK,

    /**
     * 丢弃背包
     */
    DROP_INVENTORY

}
