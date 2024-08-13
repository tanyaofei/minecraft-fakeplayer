package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
public class SetCommand extends AbstractCommand {

    public void set(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getFakeplayer(sender, args);
        var feature = (Feature) Objects.requireNonNull(args.get("feature"));
        var value = (String) Objects.requireNonNull(args.get("option"));

        var modifier = feature.getModifier();
        if (modifier == null) {
            sender.sendMessage(translatable("fakeplayer.command.config.set.error.invalid-key", RED));
            return;
        }

        modifier.accept(target, value);
        sender.sendMessage(translatable(
                "fakeplayer.command.set.success",
                text(target.getName(), WHITE),
                translatable(feature, GOLD),
                text(value, WHITE)
        ).color(GRAY));


    }

}
