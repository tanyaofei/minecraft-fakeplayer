package io.github.hello09x.fakeplayer.command.player.config;

import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigCommand extends ParentCommand {

    public final static ConfigCommand instance = new ConfigCommand(
            "假人玩家个性化配置",
            null
    );

    static {
        instance.register("get", ConfigGetCommand.instance);
        instance.register("set", ConfigSetCommand.instance);
    }

    protected ConfigCommand(@NotNull String description, @Nullable String permission) {
        super(description, permission);
    }

}
