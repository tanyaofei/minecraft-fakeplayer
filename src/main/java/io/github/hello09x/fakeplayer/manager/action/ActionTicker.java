package io.github.hello09x.fakeplayer.manager.action;


import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class ActionTicker {

    /**
     * 行为类型
     */
    @NotNull
    public final Action action;

    /**
     * 行为数据
     */
    @NotNull
    public final ActionPack actionPack;

    /**
     * 行为设置
     */
    @NotNull
    public ActionSetting setting;

    public ActionTicker(@NotNull ServerPlayer player, @NotNull Action action, @NotNull ActionSetting setting) {
        this.action = action;
        this.setting = setting;
        this.actionPack = new ActionPack(player);
    }

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

    public void inactiveTick() {
        action.inactiveTick(this.actionPack, this.setting);
    }

    public void stop() {
        action.stop(this.actionPack, this.setting);
    }


}
