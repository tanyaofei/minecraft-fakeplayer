package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.util.nms.NMS;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractCommand {

    protected final FakeplayerManager fakeplayerManager = FakeplayerManager.instance;
    protected final FakeplayerConfig config = FakeplayerConfig.instance;

    protected final NMS nms = Main.getNms();

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
                .orElseThrow(() -> CommandAPI.failWithString("你需要指定假人"));
    }

}
