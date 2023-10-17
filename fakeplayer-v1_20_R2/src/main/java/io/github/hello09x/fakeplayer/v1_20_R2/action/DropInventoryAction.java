package io.github.hello09x.fakeplayer.v1_20_R2.action;

import io.github.hello09x.fakeplayer.api.spi.Action;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class DropInventoryAction implements Action {

    private final ServerPlayer player;

    public DropInventoryAction(@NotNull ServerPlayer player) {
        this.player = player;
    }

    @Override
    public boolean tick() {
        var inventory = player.getInventory();
        for (int i = inventory.getContainerSize(); i >= 0; i--) {
            player.drop(inventory.removeItem(i, inventory.getItem(i).getCount()), false, true);
        }
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
