package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
public class SetCommand extends AbstractCommand {

    public void set(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);

        @SuppressWarnings("unchecked")
        var config = Objects.requireNonNull((Config<Object>) args.get("config"));
        if (!config.hasPermission(sender)) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.config.set.error.no-permission"));
        }
        if (!config.hasAccessor()) {
            throw CommandAPI.failWithString(i18n.asString("fakeplayer.command.config.set.error.invalid-option"));
        }
        var value = Objects.requireNonNull(args.get("value"));

        config.accessor().setter().accept(target, value);
        sender.sendMessage(i18n.translate(
                "fakeplayer.command.set.success", GRAY,
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("config", i18n.translate(config, GOLD)),
                Placeholder.component("value", text(value.toString(), WHITE))
        ));
    }

}
