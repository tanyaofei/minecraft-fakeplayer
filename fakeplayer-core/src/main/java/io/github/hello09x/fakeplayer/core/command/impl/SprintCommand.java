package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

/**
 * @author tanyaofei
 * @since 2024/8/9
 **/
@Singleton
public class SprintCommand extends AbstractCommand {

    public void sprint(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = super.getFakeplayer(sender, args);
        var sprinting = (Boolean) args.getOptional("sprinting")
                                      .map(String.class::cast)
                                      .map(Boolean::valueOf)
                                      .orElse(!fake.isSprinting());

        var message = sprinting
                ? translatable("fakeplayer.command.sprint.success.sprinting", text(fake.getName())).color(GRAY)
                : translatable("fakeplayer.command.sprint.success.unsprinting", text(fake.getName())).color(GRAY);
        fake.setSprinting(sprinting);
        sender.sendMessage(message);
    }

}
