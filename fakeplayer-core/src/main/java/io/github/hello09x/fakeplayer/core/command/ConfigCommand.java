package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ConfigCommand extends AbstractCommand {

    public final static ConfigCommand instance = new ConfigCommand();

    private final UserConfigRepository repository = UserConfigRepository.instance;

    public void getConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        @SuppressWarnings("unchecked")
        var config = (Config<Object>) Objects.requireNonNull(args.get("option"));
        var value = String.valueOf(repository.selectOrDefault(sender.getUniqueId(), config));
        sender.sendMessage(
                textOfChildren(
                        i18n.translate(config).color(GOLD),
                        text(": ", GRAY),
                        text(value, WHITE)
                )
        );
    }

    public void setConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        @SuppressWarnings("unchecked")
        var config = (Config<Object>) Objects.requireNonNull(args.get("option"));
        var value = Objects.requireNonNull(args.get("value"));
        repository.saveOrUpdate(sender.getUniqueId(), config, value);
        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.config.set.success") + "</gray>",
                Placeholder.component("option", i18n.translate(config.translateKey(), GOLD)),
                Placeholder.component("value", text(value.toString(), WHITE))
        ));
    }


}
