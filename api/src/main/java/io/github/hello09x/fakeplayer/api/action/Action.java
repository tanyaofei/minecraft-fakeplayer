package io.github.hello09x.fakeplayer.api.action;

public interface Action {

    boolean tick();

    void inactiveTick();

    void stop();

}
