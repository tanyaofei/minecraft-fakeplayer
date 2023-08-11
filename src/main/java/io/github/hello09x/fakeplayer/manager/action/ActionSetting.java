package io.github.hello09x.fakeplayer.manager.action;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ActionSetting implements Cloneable {

    /**
     * 总次数
     */
    public final int limit;

    /**
     * 剩余次数
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
        this(times, interval, 0);
    }

    public ActionSetting(int times, int interval, int wait) {
        this.limit = times;
        this.times = times;
        this.interval = interval;
        this.wait = wait;
    }

    public static ActionSetting once() {
        return new ActionSetting(1, 1 );
    }

    public static ActionSetting stop() {
        return new ActionSetting(0, 1);
    }

    public static ActionSetting interval(int interval) {
        return new ActionSetting(-1, interval);
    }

    public static ActionSetting continuous() {
        return new ActionSetting(-1, 1);
    }

    @Override
    public ActionSetting clone() {
        return new ActionSetting(
                this.times,
                this.interval,
                this.wait
        );
    }
}
