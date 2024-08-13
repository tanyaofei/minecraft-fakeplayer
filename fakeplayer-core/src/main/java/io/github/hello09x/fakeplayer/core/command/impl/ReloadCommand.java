package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.PluginTranslator;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@Singleton
public class ReloadCommand extends AbstractCommand {

    private final FakeplayerConfig config;

    private final PluginTranslator translator;

    @Inject
    public ReloadCommand(FakeplayerConfig config, PluginTranslator translator) {
        this.config = config;
        this.translator = translator;
    }

    public void reload(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        config.reload();
        sender.sendMessage(translatable("fakeplayer.command.generic.success", GRAY));
        if (config.isConfigFileOutOfDate()) {
            sender.sendMessage(translatable("fakeplayer.configuration.out-of-date", GRAY));
        }
    }

    public void reloadTranslation(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        translator.reload();
        sender.sendMessage(translatable(
                "fakeplayer.command.generic.success"
        ));
    }

}
