package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SneakCommand extends AbstractCommand {

    public final static SneakCommand instance = new SneakCommand();

    /**
     * 设置潜行
     */
    public void sneak(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var sneaking = args
                .getOptional("sneaking")
                .map(String.class::cast)
                .map(Boolean::valueOf)
                .orElse(!target.isSneaking());

        target.setSneaking(sneaking);
    }

}
