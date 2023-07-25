package io.github.hello09x.fakeplayer.command;

import io.github.hello09x.fakeplayer.command.admin.ReloadCommand;
import io.github.hello09x.fakeplayer.command.player.action.*;
import io.github.hello09x.fakeplayer.command.player.config.ConfigCommand;
import io.github.hello09x.fakeplayer.command.player.exp.ExpmeCommand;
import io.github.hello09x.fakeplayer.command.player.profile.ExpCommand;
import io.github.hello09x.fakeplayer.command.player.profile.HealthCommand;
import io.github.hello09x.fakeplayer.command.player.spawn.CreateCommand;
import io.github.hello09x.fakeplayer.command.player.spawn.KillCommand;
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
        instance.register("kill", KillCommand.instance);
        instance.register("list", ListCommand.instance);

        // tp
        instance.register("tp", TpToCommand.instance);
        instance.register("tphere", TpHereCommand.instance);
        instance.register("tps", TpSwapCommand.instance);

        // action
        instance.register("attack", AttackCommand.instance);
        instance.register("use", UseCommand.instance);
        instance.register("sneak", SneakCommand.instance);
        instance.register("drop", DropCommand.instance);
        instance.register("dropinv", DropInvCommand.instance);

        // exp
        instance.register("expme", ExpmeCommand.instance);

        // profile
        instance.register("health", HealthCommand.instance);
        instance.register("exp", ExpCommand.instance);

        // config
        instance.register("config", ConfigCommand.instance);

        // admin
        instance.register("reload", ReloadCommand.instance);
    }

    protected RootCommand(@NotNull String description, @Nullable String permission) {
        super(description, permission);
    }

}
