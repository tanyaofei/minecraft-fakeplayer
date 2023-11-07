package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReloadCommand extends AbstractCommand {

    public final static ReloadCommand instance = new ReloadCommand();

    private final FakeplayerConfig config = FakeplayerConfig.instance;

    public void reload(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        config.reload(true);
        sender.sendMessage(i18n.translate("fakeplayer.command.reload.success", GRAY));
    }

}
