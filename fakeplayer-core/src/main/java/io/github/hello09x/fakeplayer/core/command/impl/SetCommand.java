package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
public class SetCommand extends AbstractCommand {

    public void set(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var locale = TranslatorUtils.getLocale(sender);
        var target = super.getTarget(sender, args);

        @SuppressWarnings("unchecked")
        var config = Objects.requireNonNull((Config<Object>) args.get("config"));
        if (!config.hasPermission(sender)) {
            throw CommandAPI.failWithString(ComponentUtils.toString(translatable("fakeplayer.command.config.set.error.no-permission"), locale));
        }
        if (!config.hasAccessor()) {
            throw CommandAPI.failWithString(ComponentUtils.toString(translatable("fakeplayer.command.config.set.error.invalid-option"), locale));
        }
        var value = Objects.requireNonNull(args.get("value"));

        config.accessor().setter().accept(target, value);
        sender.sendMessage(translatable(
                "fakeplayer.command.set.success", GRAY,
                text(target.getName(), WHITE),
                translatable(config, GOLD),
                text(value.toString(), WHITE)
        ));
    }

}
