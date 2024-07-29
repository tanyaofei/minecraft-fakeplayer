package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.transaction.PluginTranslator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

/**
 * @author tanyaofei
 * @since 2024/7/29
 **/
@Singleton
public class TranslationCommand {

    private final PluginTranslator translator;

    @Inject
    public TranslationCommand(PluginTranslator translator) {
        this.translator = translator;
    }

    public void reloadTranslation(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        translator.reload();
        sender.sendMessage(translatable(
                "fakeplayer.command.generic.success"
        ));
    }

}
