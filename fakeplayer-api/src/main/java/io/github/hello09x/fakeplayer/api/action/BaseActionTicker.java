package io.github.hello09x.fakeplayer.api.action;

import io.github.hello09x.fakeplayer.api.action.impl.*;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.api.spi.VersionSupport;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class BaseActionTicker implements ActionTicker {

    protected Action action;

    @NotNull
    protected ActionSetting setting;

    public BaseActionTicker(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        var versionSupport = Objects.requireNonNull(VersionSupport.getInstance());
        this.setting = setting;
        this.action = switch (action) {
            case JUMP -> new JumpAction(versionSupport.player(player));
            case LOOK_AT_NEAREST_ENTITY -> new LookAtEntityAction(versionSupport.player(player));
            case DROP_ITEM -> new DropItemAction(versionSupport.player(player));
            case DROP_STACK -> new DropStackAction(versionSupport.player(player));
            case DROP_INVENTORY -> new DropInventoryAction(versionSupport.player(player));
            default -> null;
        };
    }


    @Override
    public void tick() {
        if (setting.wait > 0) {
            setting.wait--;
            inactiveTick();
            return;
        }

        if (setting.remains == 0) {
            inactiveTick();
            return;
        }

        var valid = action.tick();
        if (valid) {
            if (setting.remains > 0) {
                setting.remains--;
            }
            setting.wait = setting.interval;
        }
    }

    @Override
    public void inactiveTick() {
        action.inactiveTick();
    }

    @Override
    public void stop() {
        action.stop();
    }


}
