package io.github.hello09x.fakeplayer.entity.action;

public class ActionSetting {

    /**
     * 次数
     */
    public int times;

    /**
     * 间隔
     */
    public int interval;

    /**
     * 等待 ticks
     */
    public int wait;

    public ActionSetting(int times, int interval) {
        this.times = times;
        this.interval = interval;
        this.wait = 0;
    }

    public ActionSetting(int times, int interval, int wait) {
        this.times = times;
        this.interval = interval;
        this.wait = wait;
    }

    public static ActionSetting once() {
        return new ActionSetting(1, 1, 0);
    }

    public static ActionSetting stop() {
        return new ActionSetting(0, 1, 0);
    }

}
