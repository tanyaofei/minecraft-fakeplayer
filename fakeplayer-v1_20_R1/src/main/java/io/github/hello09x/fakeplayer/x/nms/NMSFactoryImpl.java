package io.github.hello09x.fakeplayer.x.nms;

import io.github.hello09x.fakeplayer.api.nms.*;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NMSFactoryImpl implements NMSFactory {

    @Override
    public NMSEntity entity(@NotNull Entity entity) {
        return new NMSEntityImpl(entity);
    }

    @Override
    public NMSServer server(@NotNull Server server) {
        return new NMSServerImpl(server);
    }

    @Override
    public NMSServerLevel world(@NotNull World world) {
        return new NMSServerLevelImpl(world);
    }

    @Override
    public NMSServerPlayer player(@NotNull Player player) {
        return new NMSServerPlayerImpl(player);
    }

    @Override
    public NMSNetwork network() {
        return new NMSNetworkImpl();
    }

}
