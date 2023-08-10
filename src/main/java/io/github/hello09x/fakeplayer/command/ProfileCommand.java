package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.util.Experience;
import io.github.hello09x.fakeplayer.util.Mth;
import net.kyori.adventure.text.format.NamedTextColor;
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
        var total = Experience.getExp(target);
        sender.sendMessage(textOfChildren(
                text(target.getName(), WHITE),
                text(" 当前 ", GRAY),
                text(level, DARK_GREEN),
                text(" 级, 共 ", GRAY),
                text(total, DARK_GREEN),
                text(" 点经验值", GRAY)
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
        } else if (rate > 0.8) {
            color = DARK_GREEN;
        } else if (rate > 0.5) {
            color = GOLD;
        } else if (rate > 0.2) {
            color = RED;
        } else {
            color = DARK_RED;
        }

        sender.sendMessage(textOfChildren(
                text(target.getName(), WHITE),
                text(" 当前生命值: ", GRAY),
                text(Mth.round(health, 0.5), color),
                text("/", color),
                text(max, color)
        ));
    }



}
