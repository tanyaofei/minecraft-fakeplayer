package io.github.hello09x.fakeplayer.x.action;


import io.github.hello09x.fakeplayer.api.action.Action;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.x.nms.NMSServerPlayerImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionTicker implements io.github.hello09x.fakeplayer.api.action.ActionTicker {

    /**
     * 行为类型
     */
    public Action action;

    /**
     * 行为设置
     */
    public ActionSetting setting;

    @Override
    public void tick() {
        if (setting.wait > 0) {
            setting.wait--;
            inactiveTick();
            return;
        }

        if (setting.times == 0) {
            inactiveTick();
            return;
        }

        var done = action.tick();
        if (done) {
            if (setting.times > 0) {
                setting.times--;
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

    @Override
    public void init(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        var handle = new NMSServerPlayerImpl(player).getHandle();
        this.action = switch (action) {
            case ATTACK -> new AttackAction(handle);
            case MINE -> new MineAction(handle);
            case USE -> new UseAction(handle);
            case JUMP -> new JumpAction(handle);
            case LOOK_AT_NEAREST_ENTITY -> new StareEntityAction(handle);
            case DROP_ITEM -> new DropItemAction(handle);
            case DROP_STACK -> new DropStackAction(handle);
            case DROP_INVENTORY -> new DropInventoryAction(handle);
        };

        this.setting = setting;
    }


}
