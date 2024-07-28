package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.fakeplayer.core.config.Config;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

@Singleton
public class ReloadCommand extends AbstractCommand {

    private final Config config;

    @Inject
    public ReloadCommand(Config config) {
        this.config = config;
    }

    public void reload(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        var locale = TranslatorUtils.getLocale(sender);
        config.reload();
        sender.sendMessage(translator.translate("fakeplayer.command.reload.success", locale, GRAY));
        if (config.isFileConfigurationOutOfDate()) {
            sender.sendMessage(translator.translate("fakeplayer.configuration.outofdate", locale, GRAY));
        }
    }

}
