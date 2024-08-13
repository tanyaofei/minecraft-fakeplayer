package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.devtools.core.utils.SingletonSupplier;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerAutofishManager;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerReplenishManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;

import java.util.function.Supplier;

/**
 * @author tanyaofei
 * @since 2024/8/13
 **/
public interface Singletons {

    Supplier<ActionManager> actionManager = new SingletonSupplier<>(() -> Main.getInjector().getInstance(ActionManager.class));
    Supplier<FakeplayerReplenishManager> replenishManager = new SingletonSupplier<>(() -> Main.getInjector().getInstance(FakeplayerReplenishManager.class));
    Supplier<FakeplayerAutofishManager> autofishManager = new SingletonSupplier<>(() -> Main.getInjector().getInstance(FakeplayerAutofishManager.class));

}
