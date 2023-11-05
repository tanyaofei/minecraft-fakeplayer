package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand {

    protected final FakeplayerManager fakeplayerManager = FakeplayerManager.instance;
    protected final FakeplayerConfig config = FakeplayerConfig.instance;
    protected final MiniMessage miniMessage = MiniMessage.miniMessage();


    protected @NotNull Player getTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var player = (Player) args.get("name");
        if (player != null) {
            return player;
        }

        var all = fakeplayerManager.getAll(sender);
        var count = all.size();
        return switch (count) {
            case 1 -> all.get(0);
            case 0 -> throw CommandAPI.failWithString(I18n.asString("fakeplayer.command.generic.error.non-fake-player"));
            default -> throw CommandAPI.failWithString(I18n.asString("fakeplayer.command.generic.error.name-required"));
        };
    }

}
