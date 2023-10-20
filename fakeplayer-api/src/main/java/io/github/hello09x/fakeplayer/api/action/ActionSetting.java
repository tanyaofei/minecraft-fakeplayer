package io.github.hello09x.fakeplayer.api.action;

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
    public int ticket;

    /**
     * 间隔
     */
    public int interval;

    /**
     * 等待 ticks
     */
    public int wait;

    public ActionSetting(int ticket, int interval) {
        this(ticket, interval, 0);
    }

    public ActionSetting(int ticket, int interval, int wait) {
        this.limit = ticket;
        this.ticket = ticket;
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
        try {
            return (ActionSetting) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
