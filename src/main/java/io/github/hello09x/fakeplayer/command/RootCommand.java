package io.github.hello09x.fakeplayer.command;

import io.github.hello09x.fakeplayer.command.admin.ReloadCommand;
import io.github.hello09x.fakeplayer.command.player.config.ConfigCommand;
import io.github.hello09x.fakeplayer.command.player.spawn.CreateCommand;
import io.github.hello09x.fakeplayer.command.player.spawn.KickCommand;
import io.github.hello09x.fakeplayer.command.player.spawn.ListCommand;
import io.github.hello09x.fakeplayer.command.player.tp.TpHereCommand;
import io.github.hello09x.fakeplayer.command.player.tp.TpSwapCommand;
import io.github.hello09x.fakeplayer.command.player.tp.TpToCommand;
import io.github.tanyaofei.plugin.toolkit.command.ParentCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RootCommand extends ParentCommand {

    public final static RootCommand instance = new RootCommand(
            "假人相关命令",
            null
    );

    static {
        // spawn
        instance.register("create", CreateCommand.instance);
        instance.register("kick", KickCommand.instance);
        instance.register("list", ListCommand.instance);

        // tp
        instance.register("tp", TpToCommand.instance);
        instance.register("tphere", TpHereCommand.instance);
        instance.register("tps", TpSwapCommand.instance);

        // config
        instance.register("config", ConfigCommand.instance);

        // admin
        instance.register("reload", ReloadCommand.instance);
    }

    protected RootCommand(@NotNull String description, @Nullable String permission) {
        super(description, permission);
    }

}
