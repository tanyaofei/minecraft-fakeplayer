package io.github.hello09x.fakeplayer.api.spi;

public interface ActionTicker {

    /**
     * 时刻计算
     *
     * @return 是否已经完成, 不再需要继续执行
     */
    boolean tick();

    /**
     * 非活跃时刻计算
     */
    void inactiveTick();

    /**
     * 停止行为
     */
    void stop();

}
