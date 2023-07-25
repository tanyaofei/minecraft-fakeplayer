package io.github.hello09x.fakeplayer.entity;

import org.jetbrains.annotations.NotNull;

public enum FakeplayerMetadata {

    CREATOR("creator"),
    CREATOR_IP("creator-ip"),
    NAME_SOURCE("name-source"),
    NAME_SEQUENCE("name-sequence"),
    ACTION_TASK_ID("action-task-id");

    public final String key;

    FakeplayerMetadata(@NotNull String key) {
        this.key = "fakeplayer:" + key;
    }


}
