package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.bedrock.io.Experiences;
import io.github.hello09x.fakeplayer.core.util.Mth;
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

public class ProfileCommand extends AbstractCommand {

    public final static ProfileCommand instance = new ProfileCommand();

    public void exp(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);

        var level = target.getLevel();
        var exp = Experiences.getExp(target);
        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + I18n.asString("fakeplayer.command.exp.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("level", text(level, DARK_GREEN)),
                Placeholder.component("experience", text(exp, DARK_GREEN))
        ));
    }

    public void health(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var health = target.getHealth();
        double max = Optional.ofNullable(target.getAttribute(GENERIC_MAX_HEALTH))
                .map(AttributeInstance::getValue)
                .orElse(20D);

        var rate = health / max;

        NamedTextColor color;
        if (rate >= 1.0) {
            color = GREEN;
        } else if (rate > 0.75) {
            color = YELLOW;
        } else if (rate > 0.5) {
            color = GOLD;
        } else if (rate > 0.25) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + I18n.asString("fakeplayer.command.health.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("health", textOfChildren(
                        text(Mth.floor(health, 0.5), color),
                        text("/", GRAY),
                        text(max, WHITE)
                ))
        ));
    }


}
