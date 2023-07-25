package io.github.hello09x.fakeplayer.entity.action;

import net.minecraft.server.level.ServerPlayer;

public class ActionManager {

    public final Action action;
    public final Action.ActionPack actionPack;
    public ActionSetting setting;

    public ActionManager(ServerPlayer player, Action action, ActionSetting setting) {
        this.action = action;
        this.setting = setting;
        this.actionPack = new Action.ActionPack(player);
    }

    public void tick() {
        if (setting.wait > 0) {
            setting.wait--;
            return;
        }

        if (setting.times == 0) {
            return;
        }

        var done = action.tick(this.actionPack);
        if (done) {
            if (setting.times > 0) {
                setting.times--;
            }
            setting.wait = setting.interval;
        }
    }

    public void stop() {
        action.stop(this.actionPack);
    }


}
