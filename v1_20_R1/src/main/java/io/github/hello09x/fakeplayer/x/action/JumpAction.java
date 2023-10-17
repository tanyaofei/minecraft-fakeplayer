package io.github.hello09x.fakeplayer.x.action;

import io.github.hello09x.fakeplayer.api.action.Action;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class JumpAction implements Action {

    private final ServerPlayer player;

    public JumpAction(@NotNull ServerPlayer player) {
        this.player = player;
    }

    @Override
    public boolean tick() {
        if (player.onGround()) {
            player.jumpFromGround();
        } else {
            player.setJumping(true);
        }
        player.resetLastActionTime();
        return true;
    }

    @Override
    public void inactiveTick() {
        player.setJumping(false);
    }

    @Override
    public void stop() {

    }

}
