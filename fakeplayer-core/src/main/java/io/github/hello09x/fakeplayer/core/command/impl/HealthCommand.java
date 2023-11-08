package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.util.Mth;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HealthCommand extends AbstractCommand {

    public final static HealthCommand instance = new HealthCommand();

    public void health(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = super.getTarget(sender, args);
        var health = target.getHealth();
        double max = Optional.ofNullable(target.getAttribute(GENERIC_MAX_HEALTH))
                .map(AttributeInstance::getValue)
                .orElse(20D);

        var rate = health / max;

        NamedTextColor color;
        if (rate >= 0.75) {
            color = GREEN;
        } else if (rate > 0.5) {
            color = YELLOW;
        } else if (rate > 0.25) {
            color = GOLD;
        } else if (rate > 0.125) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.health.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("health", textOfChildren(
                        text(Mth.floor(health, 0.5), color),
                        text("/", GRAY),
                        text(max, WHITE)
                ))
        ));
    }


}
