package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefillCommand extends AbstractCommand {

    public final static RefillCommand instance = new RefillCommand();

    public final static String PERMISSION = Permission.action;

    public void refill(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var refillable = args.getOptional("enabled").map(value -> Boolean.parseBoolean((String) value)).orElse(null);
        if (refillable == null) {
            refillable = !fakeplayerManager.isRefillable(target);
        }
        fakeplayerManager.setRefillable(target, refillable);
        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.refill.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("status", refillable
                        ? i18n.translate("fakeplayer.generic.enabled", DARK_GREEN)
                        : i18n.translate("fakeplayer.generic.disabled", GRAY))
                )
        );
    }

}
