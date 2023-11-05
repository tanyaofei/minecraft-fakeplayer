package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractCommand {

    protected final FakeplayerManager fakeplayerManager = FakeplayerManager.instance;
    protected final FakeplayerConfig config = FakeplayerConfig.instance;


    protected @NotNull Player getTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        return Optional
                .ofNullable((Player) args.get("target"))
                .or(() -> {
                    var all = fakeplayerManager.getAll(sender);
                    if (all.size() != 1) {
                        return Optional.empty();
                    }
                    return Optional.of(all.get(0));
                })
                .orElseThrow(() -> CommandAPI.failWithString(I18n.asString("command.generic.error.requires-name")));
    }

}
