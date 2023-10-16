package io.github.hello09x.fakeplayer.api.nms;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ServiceLoader;

public interface NMSFactory {

    static @NotNull NMSFactory getInstance() {
        return ServiceLoader.load(NMSFactory.class, NMSFactory.class.getClassLoader()).findFirst().orElseThrow();
    }

    NMSEntity entity(@NotNull Entity entity);

    NMSServer server(@NotNull Server server);

    NMSServerLevel world(@NotNull World world);

    NMSServerPlayer player(@NotNull Player player);

    NMSNetwork network();

}
