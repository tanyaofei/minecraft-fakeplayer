package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;

public class TagCommandProvider implements Provider<TagCommand> {
    @Inject
    private FakeplayerManager manager;

    @Override
    public TagCommand get() {
        return new TagCommand(manager);
    }
}
