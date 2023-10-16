package io.github.hello09x.fakeplayer.x.action;


import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionTicker implements io.github.hello09x.fakeplayer.api.action.ActionTicker {

    /**
     * 行为类型
     */
    public Action action;

    /**
     * 行为数据
     */
    public ActionPack actionPack;

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

        var done = action.tick(this.actionPack, this.setting);
        if (done) {
            if (setting.times > 0) {
                setting.times--;
            }
            setting.wait = setting.interval;
        }
    }

    @Override
    public void inactiveTick() {
        action.inactiveTick(this.actionPack, this.setting);
    }

    @Override
    public void stop() {
        action.stop(this.actionPack, this.setting);
    }

    @Override
    public void init(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        this.action = switch (action) {
            case ATTACK -> Action.ATTACK;
            case USE -> Action.USE;
            case JUMP -> Action.JUMP;
            case LOOK_AT_NEAREST_ENTITY -> Action.LOOK_AT_NEAREST_ENTITY;
            case DROP_ITEM -> Action.DROP_ITEM;
            case DROP_STACK -> Action.DROP_STACK;
            case DROP_INVENTORY -> Action.DROP_INVENTORY;
        };

        this.actionPack = new ActionPack(((CraftPlayer) player).getHandle());
        this.setting = setting;
    }


}
