package io.github.hello09x.fakeplayer.api.spi;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

public interface Action {

    /**
     * 活跃 tick 时执行
     *
     * @return 是否有做出行动
     */
    boolean tick();

    /**
     * 非活跃 tick 时执行
     */
    void inactiveTick();

    /**
     * 结束动作时执行
     */
    void stop();


    @AllArgsConstructor
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

    @EqualsAndHashCode
    class ActionSetting implements Cloneable {

        /**
         * 总次数
         */
        public final int maximum;

        /**
         * 剩余次数
         */
        public int remains;

        /**
         * 间隔
         */
        public int interval;

        /**
         * 等待 ticks
         */
        public int wait;

        public ActionSetting(int maximum, int interval) {
            this(maximum, interval, 0);
        }

        public ActionSetting(int maximum, int interval, int wait) {
            this.maximum = maximum;
            this.remains = maximum;
            this.interval = interval;
            this.wait = wait;
        }

        public static ActionSetting once() {
            return new ActionSetting(1, 1);
        }

        public static ActionSetting stop() {
            return new ActionSetting(0, 1);
        }

        public static ActionSetting interval(int interval) {
            return new ActionSetting(-1, interval);
        }

        public static ActionSetting continuous() {
            return new ActionSetting(-1, 1);
        }

        @Override
        public ActionSetting clone() {
            try {
                return (ActionSetting) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new Error(e);
            }
        }
    }
}
