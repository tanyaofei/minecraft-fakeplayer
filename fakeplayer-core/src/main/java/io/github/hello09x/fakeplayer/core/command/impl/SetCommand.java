package io.github.hello09x.fakeplayer.core.command.impl;

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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SetCommand extends AbstractCommand {

    public final static SetCommand instance = new SetCommand();

    public void set(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);

        @SuppressWarnings("unchecked")
        var config = Objects.requireNonNull((Config<Object>) args.get("config"));
        var value = Objects.requireNonNull(args.get("value"));

        config.configurer().accept(target, value);
        sender.sendMessage(i18n.translate(
                "fakeplayer.command.set.success", GRAY,
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("config", i18n.translate(config, GOLD)),
                Placeholder.component("value", text(value.toString(), WHITE))
        ));
    }

}
