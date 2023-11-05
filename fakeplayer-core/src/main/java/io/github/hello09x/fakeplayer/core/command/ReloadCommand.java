package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class ReloadCommand extends AbstractCommand {

    public final static ReloadCommand instance = new ReloadCommand();

    private final FakeplayerConfig config = FakeplayerConfig.instance;

    public void reload(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        config.reload(true);
        sender.sendMessage(I18n.translate(translatable("fakeplayer.command.reload.success", GRAY)));
    }

}
