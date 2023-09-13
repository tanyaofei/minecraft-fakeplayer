package io.github.hello09x.fakeplayer.manager.action;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionPack {

    /**
     * 假人玩家
     */
    @NotNull
    public final ServerPlayer player;

    /**
     * 左键数据
     */
    @NotNull
    public final AttackActionPack attack = new AttackActionPack();

    /**
     * 右键相关数据
     */
    @NotNull
    public final UseActionPack use = new UseActionPack();

    public ActionPack(@NotNull ServerPlayer player) {
        this.player = player;
    }

    public final static class AttackActionPack {

        /**
         * 当前左键的目标位置
         */
        @Nullable
        public BlockPos pos;

        /**
         * 破坏方块的进度
         */
        public float progress;

        /**
         * 冷却, 单位: tick
         */
        public int freeze;
    }

    public final static class UseActionPack {

        /**
         * 冷却, 单位: tick
         */
        public int freeze;
    }

}
