package io.github.hello09x.fakeplayer.command.player.profile;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH;

public class HealthCommand extends AbstractCommand {


    public static final HealthCommand instance = new HealthCommand(
            "获取假人生命值",
            "/fp health [假人]",
            "fakeplayer.profile"
    );

    public HealthCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    public static double round(double num, double base) {
        if (num % base == 0) {
            return num;
        }
        return Math.floor(num / base) * base;
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }

        var health = target.getHealth();
        double max = Optional.ofNullable(target.getAttribute(GENERIC_MAX_HEALTH))
                .map(AttributeInstance::getValue)
                .orElse(20D);

        var rate = health / max;

        NamedTextColor color;
        if (rate >= 1.0) {
            color = GREEN;
        } else if (rate > 0.8) {
            color = DARK_GREEN;
        } else if (rate > 0.5) {
            color = GOLD;
        } else if (rate > 0.2) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        var h = BigDecimal.valueOf(health).setScale(1, RoundingMode.DOWN);

        sender.sendMessage(textOfChildren(
                text(target.getName()),
                text(" 当前生命值: ", GRAY),
                text(round(health, 0.5), color),
                text("/", color),
                text(max, color)
        ));
        return true;
    }

}
