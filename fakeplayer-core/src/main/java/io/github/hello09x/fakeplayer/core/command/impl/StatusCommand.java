package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.utils.ExperienceUtils;
import io.github.hello09x.fakeplayer.core.command.Permission;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import io.github.hello09x.fakeplayer.core.util.Attributes;
import io.github.hello09x.fakeplayer.core.util.Mth;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.JoinConfiguration.newlines;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

@Singleton
public class StatusCommand extends AbstractCommand {

    private final static Component LINE_SPLITTER = text(StringUtils.repeat("-", 20), GRAY);

    private static @NotNull NamedTextColor color(double current, double max) {
        var p = current / max;
        NamedTextColor color;
        if (p >= 0.75) {
            color = GREEN;
        } else if (p >= 0.5) {
            color = YELLOW;
        } else if (p >= 0.25) {
            color = GOLD;
        } else if (p >= 0.125) {
            color = RED;
        } else {
            color = DARK_RED;
        }
        return color;
    }

    /**
     * 查看假人状态
     */
    public void status(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = super.getFakeplayer(sender, args);
        var title = translatable(
                "fakeplayer.command.status.title",
                text(fake.getName(), WHITE)
        ).color(GRAY);

        var lines = new ArrayList<Component>(6);
        lines.add(title);
        lines.add(this.getHealthLine(fake));
        lines.add(this.getFoodLine(fake));
        if (sender.hasPermission(Permission.expme)) {
            lines.add(this.getExperienceLine(fake));
        }
        lines.add(LINE_SPLITTER);
        lines.add(getFeatureLine(fake));

        sender.sendMessage(join(newlines(), lines));
    }

    private @NotNull Component getFoodLine(@NotNull Player target) {
        var food = target.getFoodLevel();
        var max = 20.0;
        return translatable(
                "fakeplayer.command.status.food",
                textOfChildren(
                        text(Mth.floor(food, 0.5), color(food, max)),
                        text("/", GRAY),
                        text(max, WHITE)
                )
        ).color(WHITE);
    }

    private @NotNull Component getHealthLine(@NotNull Player target) {
        var health = target.getHealth();
        double max = Optional.ofNullable(target.getAttribute(Attributes.maxHealth()))
                             .map(AttributeInstance::getValue)
                             .orElse(20D);

        return translatable(
                "fakeplayer.command.status.health",
                textOfChildren(
                        text(Mth.floor(health, 0.5), color(health, max)),
                        text("/", GRAY),
                        text(max, WHITE)
                ).color(WHITE)
        );
    }

    private @NotNull Component getExperienceLine(@NotNull Player target) {
        var level = target.getLevel();
        var points = ExperienceUtils.getExp(target);

        return textOfChildren(
                translatable(
                        "fakeplayer.command.status.exp",
                        text(level, GREEN),
                        text(points, GREEN)
                ).color(WHITE),
                space(),
                translatable("fakeplayer.command.status.exp.withdraw", AQUA).clickEvent(runCommand("/fp expme " + target.getName()))
        );
    }

    private @NotNull Component getFeatureLine(@NotNull Player faker) {
        var messages = new ArrayList<Component>();
        for (var feature : Feature.values()) {
            var detector = feature.getDetector();
            if (detector == null) {
                continue;
            }
            var name = translatable(feature, WHITE);
            var options = feature.getOptions();
            var status = detector.apply(faker);

            messages.add(textOfChildren(
                    name,
                    space(),
                    join(separator(space()), options.stream().map(option -> {
                        var style = option.equals(status) ? Style.style(GREEN, UNDERLINED) : Style.style(GRAY);
                        return text("[" + option + "]").style(style).clickEvent(
                                runCommand("/fp set %s %s %s".formatted(feature.name(), option, faker.getName()))
                        );
                    }).toList())
            ));
        }
        return join(newlines(), messages);
    }

}
