package io.github.hello09x.fakeplayer.entity.action;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class ActionPack {

    public final ServerPlayer player;

    public final AttackActionPack attack = new AttackActionPack();

    public final UseActionPack use = new UseActionPack();

    public ActionPack(ServerPlayer player) {
        this.player = player;
    }

    public final static class AttackActionPack {
        public BlockPos pos;
        public float progress;
        public int freeze;
    }

    public final static class UseActionPack {
        public int freeze;
    }

}
