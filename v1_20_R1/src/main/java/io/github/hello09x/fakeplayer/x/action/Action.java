package io.github.hello09x.fakeplayer.x.action;

import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.papermc.paper.entity.LookAnchor;
import lombok.AllArgsConstructor;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.*;

@AllArgsConstructor
public enum Action {

    USE("交互/使用/放置") {
        @Override
        @SuppressWarnings("resource")
        public boolean tick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            if (ap.use.freeze > 0) {
                ap.use.freeze--;
                return false;
            }

            var player = ap.player;
            if (player.isUsingItem()) {
                return true;
            }

            var hit = getTarget(player);
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
                                ap.use.freeze = 3;
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
                            ap.use.freeze = 3;
                            return true;
                        }
                        if (player.interactOn(entity, hand).consumesAction() && !(handWasEmpty && itemFrameEmpty)) {
                            ap.use.freeze = 3;
                            return true;
                        }
                    }
                }
                var handItem = player.getItemInHand(hand);
                if (player.gameMode.useItem(player, player.level(), handItem, hand).consumesAction()) {
                    ap.use.freeze = 3;
                    return true;
                }
            }
            return false;
        }

        @Override
        public void stop(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            ap.use.freeze = 0;
            ap.player.releaseUsingItem();
        }
    },

    ATTACK("攻击/破坏") {
        @Override
        @SuppressWarnings("resource")
        public boolean tick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            var player = ap.player;
            var hit = getTarget(player);
            if (hit == null) {
                return false;
            }
            switch (hit.getType()) {
                case ENTITY -> {
                    var entityHit = (EntityHitResult) hit;
                    player.attack(entityHit.getEntity());
                    player.swing(InteractionHand.MAIN_HAND);
                    player.resetAttackStrengthTicker();
                    player.resetLastActionTime();
                    return true;
                }
                case BLOCK -> {
                    if (ap.attack.freeze > 0) {
                        ap.attack.freeze--;
                        return false;
                    }

                    var blockHit = (BlockHitResult) hit;
                    var pos = blockHit.getBlockPos();
                    var side = blockHit.getDirection();

                    if (player.blockActionRestricted(player.level(), pos, player.gameMode.getGameModeForPlayer())) {
                        return false;
                    }

                    if (ap.attack.pos != null && player.level().getBlockState(ap.attack.pos).isAir()) {
                        ap.attack.pos = null;
                        return false;
                    }

                    var state = player.level().getBlockState(pos);
                    var broken = false;
                    if (player.gameMode.getGameModeForPlayer().isCreative()) {
                        player.gameMode.handleBlockBreakAction(
                                pos,
                                START_DESTROY_BLOCK,
                                side,
                                player.level().getMaxBuildHeight(),
                                -1
                        );
                        ap.attack.freeze = 5;
                    } else if (ap.attack.pos == null || !ap.attack.pos.equals(pos)) {
                        if (ap.attack.pos != null) {
                            player.gameMode.handleBlockBreakAction(
                                    ap.attack.pos,
                                    ABORT_DESTROY_BLOCK,
                                    side,
                                    player.level().getMaxBuildHeight(),
                                    -1
                            );
                        }

                        player.gameMode.handleBlockBreakAction(
                                pos,
                                START_DESTROY_BLOCK,
                                side,
                                player.level().getMaxBuildHeight(),
                                -1
                        );

                        if (!state.isAir() && ap.attack.progress == 0) {
                            state.attack(player.level(), pos, player);
                        }

                        if (!state.isAir() && state.getDestroyProgress(player, player.level(), pos) >= 1) {
                            ap.attack.pos = null;
                            broken = true;
                        } else {
                            ap.attack.pos = pos;
                            ap.attack.progress = 0;
                        }
                    } else {
                        ap.attack.progress += state.getDestroyProgress(player, player.level(), pos);
                        if (ap.attack.progress >= 1) {
                            player.gameMode.handleBlockBreakAction(
                                    pos,
                                    STOP_DESTROY_BLOCK,
                                    side,
                                    player.level().getMaxBuildHeight(),
                                    -1
                            );
                            ap.attack.pos = null;
                            ap.attack.freeze = 5;
                            broken = true;
                        }
                        player.level().destroyBlockProgress(-1, pos, (int) (ap.attack.progress * 10));
                    }

                    player.resetLastActionTime();
                    player.swing(InteractionHand.MAIN_HAND);
                    return broken;
                }
            }
            return false;
        }

        @Override
        @SuppressWarnings("resource")
        public void stop(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            if (ap.attack.pos == null) {
                return;
            }

            var player = ap.player;
            player.level().destroyBlockProgress(-1, ap.attack.pos, -1);
            player.gameMode.handleBlockBreakAction(
                    ap.attack.pos,
                    ABORT_DESTROY_BLOCK,
                    Direction.DOWN,
                    player.level().getMaxBuildHeight(),
                    -1
            );
            ap.attack.pos = null;
            ap.attack.freeze = 0;
            ap.attack.progress = 0;
        }
    },

    JUMP("跳") {
        @Override
        public boolean tick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            var player = ap.player;
            if (setting.limit == 1) {
                if (player.onGround()) {
                    player.jumpFromGround();
                    return true;
                }
            }

            player.setJumping(true);
            return true;
        }

        @Override
        public void inactiveTick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            ap.player.setJumping(false);
        }
    },

    LOOK_AT_NEAREST_ENTITY("目视实体") {

        @Override
        @SuppressWarnings("UnstableApiUsage")
        public boolean tick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            var player = ap.player;
            var bukkitPlayer = Bukkit.getPlayer(player.getUUID());
            if (bukkitPlayer == null) {
                return true;
            }

            var target = bukkitPlayer
                    .getNearbyEntities(4.5, 4.5, 4.5)
                    .stream()
                    .filter(e -> e instanceof Damageable)
                    .findAny()
                    .orElse(null);

            if (target == null) {
                return false;
            }

            bukkitPlayer.lookAt(target, LookAnchor.EYES, LookAnchor.EYES);
            return true;
        }

    },

    DROP_ITEM("丢弃手上物品") {
        @Override
        public boolean tick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            var player = ap.player;
            player.resetLastActionTime();
            player.drop(false);
            return true;
        }
    },

    DROP_STACK("丢弃手上整组物品") {
        @Override
        public boolean tick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            var player = ap.player;
            player.resetLastActionTime();
            player.drop(true);
            return true;
        }
    },

    DROP_INVENTORY("丢弃背包物品") {
        @Override
        public boolean tick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
            var player = ap.player;
            dropInventory(player);
            return true;
        }
    };


    @NotNull
    public final String name;

    static @Nullable HitResult getTarget(@NotNull ServerPlayer player) {
        double reach = player.gameMode.isCreative() ? 5 : 4.5f;
        return Tracer.rayTrace(player, 1, reach, false);
    }

    public static void dropInventory(@NotNull ServerPlayer player) {
        var inventory = player.getInventory();
        for (int i = inventory.getContainerSize(); i >= 0; i--) {
            player.drop(inventory.removeItem(i, inventory.getItem(i).getCount()), false, true);
        }
    }


    public abstract boolean tick(@NotNull ActionPack ap, @NotNull ActionSetting setting);

    public void inactiveTick(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
        this.stop(ap, setting);
    }

    public void stop(@NotNull ActionPack ap, @NotNull ActionSetting setting) {
    }


}
