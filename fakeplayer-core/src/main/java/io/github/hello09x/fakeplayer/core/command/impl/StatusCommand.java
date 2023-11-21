package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.io.Experiences;
import io.github.hello09x.fakeplayer.core.command.Permission;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import io.github.hello09x.fakeplayer.core.util.Mth;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatusCommand extends AbstractCommand {

    public final static StatusCommand instance = new StatusCommand();

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
        var target = super.getTarget(sender, args);
        var title = i18n.translate(
                "fakeplayer.command.status.title", GRAY,
                Placeholder.component("name", text(target.getName(), WHITE))
        );

        var lines = new ArrayList<Component>(6);
        lines.add(title);
        lines.add(this.getHealthLine(target));
        lines.add(this.getFoodLine(target));
        if (sender.hasPermission(Permission.expme)) {
            lines.add(this.getExperienceLine(target));
        }
        lines.add(LINE_SPLITTER);
        lines.add(getConfigLine(target));

        sender.sendMessage(join(JoinConfiguration.newlines(), lines));
    }

    private @NotNull Component getFoodLine(@NotNull Player target) {
        var food = target.getFoodLevel();
        var max = 20.0;
        return i18n.translate(
                "fakeplayer.command.status.food", WHITE,
                Placeholder.component("food", textOfChildren(
                        text(Mth.floor(food, 0.5), color(food, max)),
                        text("/", GRAY),
                        text(max, WHITE)
                ))
        );
    }

    private @NotNull Component getHealthLine(@NotNull Player target) {
        var health = target.getHealth();
        double max = Optional.ofNullable(target.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                .map(AttributeInstance::getValue)
                .orElse(20D);

        return i18n.translate(
                "fakeplayer.command.status.health", WHITE,
                Placeholder.component("health", textOfChildren(
                        text(Mth.floor(health, 0.5), color(health, max)),
                        text("/", GRAY),
                        text(max, WHITE)
                ))
        );
    }

    private @NotNull Component getExperienceLine(@NotNull Player target) {
        var level = target.getLevel();
        var points = Experiences.getExp(target);

        return textOfChildren(
                i18n.translate(
                        "fakeplayer.command.status.exp", WHITE,
                        Placeholder.component("level", text(level, GREEN)),
                        Placeholder.component("points", text(points, GREEN))
                ),
                space(),
                i18n.translate("fakeplayer.command.status.exp.withdraw", AQUA).clickEvent(runCommand("/fp expme " + target.getName()))
        );
    }

    private @NotNull Component getConfigLine(@NotNull Player target) {
        var configs = Arrays.stream(Config.values()).filter(Config::hasConfigurer).toList();
        var messages = new ArrayList<Component>();
        for (var config : configs) {
            var name = i18n.translate(config.translationKey());
            var options = config.options();
            var status = config.current().apply(target).toString();

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
