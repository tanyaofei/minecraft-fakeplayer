package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
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
        var config = (Config<Object>) Objects.requireNonNull(args.get("config"));
        var value = String.valueOf(repository.selectOrDefault(sender.getUniqueId(), config));
        sender.sendMessage(
                textOfChildren(
                        text(config.label(), GOLD),
                        text(": ", GRAY),
                        text(value, WHITE)
                )
        );
    }

    public void setConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        @SuppressWarnings("unchecked")
        var config = (Config<Object>) Objects.requireNonNull(args.get("config"));
        var value = Objects.requireNonNull(args.get("value"));
        repository.saveOrUpdate(sender.getUniqueId(), config, value);
        sender.sendMessage(textOfChildren(
                text(config.label(), GOLD),
                text("变更为 ", GRAY),
                text(value.toString(), WHITE),
                text(" , 下次创建假人时生效", GRAY)
        ));
    }


}
