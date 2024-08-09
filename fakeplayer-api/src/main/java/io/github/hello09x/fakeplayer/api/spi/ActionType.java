package io.github.hello09x.fakeplayer.api.spi;

import lombok.AllArgsConstructor;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

/**
 * @author tanyaofei
 * @since 2024/8/9
 **/
@AllArgsConstructor
public
enum ActionType implements Translatable {

    /**
     * 攻击实体
     */
    ATTACK("fakeplayer.action.attack"),

    /**
     * 挖掘
     */
    MINE("fakeplayer.action.mine"),

    /**
     * 右键
     */
    USE("fakeplayer.action.use"),

    /**
     * 跳跃
     */
    JUMP("fakeplayer.action.jump"),

    /**
     * 看向附近实体
     */
    LOOK_AT_NEAREST_ENTITY("fakeplayer.action.look-at-entity"),

    /**
     * 丢弃手上 1 个物品
     */
    DROP_ITEM("fakeplayer.action.drop-item"),

    /**
     * 丢弃手上整组物品
     */
    DROP_STACK("fakeplayer.action.drop-stack"),

    /**
     * 丢弃背包
     */
    DROP_INVENTORY("fakeplayer.action.drop-inventory");

    final String translationKey;


    @Override
    public @NotNull String translationKey() {
        return this.translationKey;
    }
}
