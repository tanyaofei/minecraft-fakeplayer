package io.github.hello09x.fakeplayer.v1_21.action;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class UseAction extends TraceAction {

    private final Current current = new Current();

    public UseAction(@NotNull ServerPlayer player) {
        super(player);
    }

    @Override
    @SuppressWarnings("resource")
    public boolean tick() {
        if (current.freeze > 0) {
            current.freeze--;
            return false;
        }

        if (player.isUsingItem()) {
            return true;
        }

        var hit = this.getTarget();
        if (hit == null) {
            return false;
        }

        for (var hand : InteractionHand.values()) {
            switch (hit.getType()) {
                case BLOCK -> {
                    player.resetLastActionTime();
                    var world = player.serverLevel();
                    var blockHit = (BlockHitResult) hit;
                    var pos = blockHit.getBlockPos();
                    var side = blockHit.getDirection();
                    if (pos.getY() < player.level().getMaxBuildHeight() - (side == Direction.UP ? 1 : 0) && world.mayInteract(player, pos)) {
                        var result = player.gameMode.useItemOn(player, world, player.getItemInHand(hand), hand, blockHit);
                        if (result.consumesAction()) {
                            player.swing(hand);
                            current.freeze = 3;
                            return true;
                        }
                    }
                }
                case ENTITY -> {
                    player.resetLastActionTime();
                    var entityHit = (EntityHitResult) hit;
                    var entity = entityHit.getEntity();
                    boolean handWasEmpty = player.getItemInHand(hand).isEmpty();
                    boolean itemFrameEmpty = (entity instanceof ItemFrame) && ((ItemFrame) entity).getItem().isEmpty();
                    var pos = entityHit.getLocation().subtract(entity.getX(), entity.getY(), entity.getZ());
                    if (entity.interactAt(player, pos, hand).consumesAction()) {
                        current.freeze = 3;
                        return true;
                    }
                    if (player.interactOn(entity, hand).consumesAction() && !(handWasEmpty && itemFrameEmpty)) {
                        current.freeze = 3;
                        return true;
                    }
                }
            }
            var handItem = player.getItemInHand(hand);
            if (player.gameMode.useItem(player, player.level(), handItem, hand).consumesAction()) {
                player.resetLastActionTime();
                current.freeze = 3;
                return true;
            }
        }
        return false;
    }

    @Override
    public void inactiveTick() {
    }

    @Override
    public void stop() {
        current.freeze = 0;
        player.releaseUsingItem();
    }

    private final static class Current {

        /**
         * 冷却, 单位: tick
         */
        public int freeze;
    }
}
