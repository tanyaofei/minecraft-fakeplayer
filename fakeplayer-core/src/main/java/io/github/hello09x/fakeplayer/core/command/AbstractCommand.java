package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractCommand {

    protected final FakeplayerManager fakeplayerManager = FakeplayerManager.instance;
    protected final FakeplayerConfig config = FakeplayerConfig.instance;
    protected final MiniMessage miniMessage = MiniMessage.miniMessage();
    protected final I18n i18n = Main.i18n();

    protected @NotNull Player getTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var player = (Player) args.get("name");
        if (player != null) {
            return player;
        }

        var all = fakeplayerManager.getAll(sender);
        var count = all.size();
        return switch (count) {
            case 1 -> all.get(0);
            case 0 -> throw CommandAPI.failWithString(i18n.asString(
                    "fakeplayer.command.generic.error.non-fake-player"
            ));
            default -> throw CommandAPI.failWithString(i18n.asString(
                    "fakeplayer.command.generic.error.name-required"
            ));
        };
    }

    protected @NotNull List<Player> getTargets(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        @SuppressWarnings("unchecked")
        var players = (List<Player>) args.get("names");
        if (players == null || players.isEmpty()) {
            var fakeplayers = fakeplayerManager.getAll(sender);
            return switch (fakeplayers.size()) {
                case 1 -> fakeplayers;
                case 0 -> throw CommandAPI.failWithString(i18n.asString(
                        "fakeplayer.command.generic.error.non-fake-player"
                ));
                default -> throw CommandAPI.failWithString(i18n.asString(
                        "fakeplayer.command.generic.error.name-required"
                ));
            };
        }

        return players;
    }

}
