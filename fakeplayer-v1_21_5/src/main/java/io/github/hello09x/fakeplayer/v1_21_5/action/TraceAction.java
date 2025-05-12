package io.github.hello09x.fakeplayer.v1_21_5.action;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.v1_21_5.action.util.Tracer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TraceAction implements Action {

    protected final ServerPlayer player;

    protected TraceAction(@NotNull ServerPlayer player) {
        this.player = player;
    }

    protected @Nullable HitResult getTarget() {
        double reach = player.gameMode.isCreative() ? 5 : 4.5f;
        return Tracer.rayTrace(player, 1, reach, false);
    }


}
