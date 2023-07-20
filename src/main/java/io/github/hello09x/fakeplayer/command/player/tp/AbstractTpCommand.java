package io.github.hello09x.fakeplayer.command.player.tp;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT;

public abstract class AbstractTpCommand extends AbstractCommand {


    protected final FakePlayerManager manager = FakePlayerManager.instance;

    protected final FakeplayerProperties properties = FakeplayerProperties.instance;

    public AbstractTpCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    protected void teleport(@NotNull Player from, @NotNull Player to) {
        from.teleport(to.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        to.getLocation().getWorld().playSound(to.getLocation(), ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

}
