package io.github.hello09x.fakeplayer.api.spi;

public interface ActionTicker {

    void tick();

    void inactiveTick();

    void stop();

}
