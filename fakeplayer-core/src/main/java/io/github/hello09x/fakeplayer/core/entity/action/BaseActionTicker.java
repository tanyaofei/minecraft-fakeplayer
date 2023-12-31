package io.github.hello09x.fakeplayer.core.entity.action;

import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.entity.action.impl.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

public abstract class BaseActionTicker implements ActionTicker {

    protected final static NMSBridge bridge = Objects.requireNonNull(NMSBridge.getInstance(), "Unsupported");

    /**
     * 这个类无法初始化的动作, 由子类完成
     */
    @UnknownNullability
    protected Action action;

    @NotNull
    protected Action.ActionSetting setting;

    public BaseActionTicker(@NotNull Player player, @NotNull Action.ActionType action, @NotNull Action.ActionSetting setting) {
        this.setting = setting;
        this.action = switch (action) {
            case JUMP -> new JumpAction(bridge.fromPlayer(player));
            case LOOK_AT_NEAREST_ENTITY -> new LookAtEntityAction(bridge.fromPlayer(player));
            case DROP_ITEM -> new DropItemAction(bridge.fromPlayer(player));
            case DROP_STACK -> new DropStackAction(bridge.fromPlayer(player));
            case DROP_INVENTORY -> new DropInventoryAction(bridge.fromPlayer(player));
            default -> null;    // 子类需要实现其他 Action
        };
    }


    @Override
    public boolean tick() {
        if (setting.wait > 0) {
            this.setting.wait--;
            this.inactiveTick();
            return false;
        }

        if (this.setting.remains == 0) {
            this.inactiveTick();
            return true;
        }

        var valid = this.action.tick();
        if (valid) {
            if (this.setting.remains > 0) {
                this.setting.remains--;
            }
            this.setting.wait = this.setting.interval;
        }
        return false;
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
