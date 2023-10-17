package io.github.hello09x.fakeplayer.v1_20_R1.action;

import io.github.hello09x.fakeplayer.api.spi.Action;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class DropStackAction implements Action {

    public final ServerPlayer player;

    public DropStackAction(@NotNull ServerPlayer player) {
        this.player = player;
    }


    @Override
    public boolean tick() {
        player.resetLastActionTime();
        player.drop(true);
        player.resetLastActionTime();
        return true;
    }

    @Override
    public void inactiveTick() {

    }

    @Override
    public void stop() {

    }
}
