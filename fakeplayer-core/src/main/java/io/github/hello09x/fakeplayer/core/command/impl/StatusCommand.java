package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ExperienceUtils;
import io.github.hello09x.fakeplayer.core.command.Permission;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import io.github.hello09x.fakeplayer.core.util.Mth;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.*;
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
        var locale = TranslatorUtils.getLocale(sender);
        var target = super.getTarget(sender, args);
        var title = translator.translate(
                "fakeplayer.command.status.title", locale, GRAY,
                Placeholder.component("name", text(target.getName(), WHITE))
        );

        var lines = new ArrayList<Component>(6);
        lines.add(title);
        lines.add(this.getHealthLine(target, locale));
        lines.add(this.getFoodLine(target, locale));
        if (sender.hasPermission(Permission.expme)) {
            lines.add(this.getExperienceLine(target, locale));
        }
        lines.add(LINE_SPLITTER);
        lines.add(getConfigLine(target, locale));

        sender.sendMessage(join(JoinConfiguration.newlines(), lines));
    }

    private @NotNull Component getFoodLine(@NotNull Player target, @Nullable Locale locale) {
        var food = target.getFoodLevel();
        var max = 20.0;
        return translator.translate(
                "fakeplayer.command.status.food", locale, WHITE,
                Placeholder.component("food", textOfChildren(
                        text(Mth.floor(food, 0.5), color(food, max)),
                        text("/", GRAY),
                        text(max, WHITE)
                ))
        );
    }

    private @NotNull Component getHealthLine(@NotNull Player target, @Nullable Locale locale) {
        var health = target.getHealth();
        double max = Optional.ofNullable(target.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                .map(AttributeInstance::getValue)
                .orElse(20D);

        return translator.translate(
                "fakeplayer.command.status.health", locale, WHITE,
                Placeholder.component("health", textOfChildren(
                        text(Mth.floor(health, 0.5), color(health, max)),
                        text("/", GRAY),
                        text(max, WHITE)
                ))
        );
    }

    private @NotNull Component getExperienceLine(@NotNull Player target, @Nullable Locale locale) {
        var level = target.getLevel();
        var points = ExperienceUtils.getExp(target);

        return textOfChildren(
                translator.translate(
                        "fakeplayer.command.status.exp", locale, WHITE,
                        Placeholder.component("level", text(level, GREEN)),
                        Placeholder.component("points", text(points, GREEN))
                ),
                space(),
                translator.translate("fakeplayer.command.status.exp.withdraw", locale, AQUA).clickEvent(runCommand("/fp expme " + target.getName()))
        );
    }

    private @NotNull Component getConfigLine(@NotNull Player target, @Nullable Locale locale) {
        var configs = Arrays.stream(Config.values()).filter(Config::hasAccessor).toList();
        var messages = new ArrayList<Component>();
        for (var config : configs) {
            var name = translator.translate(config.translationKey(), locale, WHITE);
            var options = config.options();
            var status = config.accessor().getter().apply(target).toString();

            messages.add(textOfChildren(
                    name,
                    space(),
                    join(JoinConfiguration.separator(space()), options.stream().map(option -> {
                        var style = option.equals(status) ? Style.style(GREEN, UNDERLINED) : Style.style(GRAY);
                        return text("[" + option + "]").style(style).clickEvent(
                                runCommand("/fp set %s %s %s".formatted(config.key(), option, target.getName()))
                        );
                    }).collect(Collectors.toList()))
            ));
        }
        return join(JoinConfiguration.newlines(), messages);
    }

}
