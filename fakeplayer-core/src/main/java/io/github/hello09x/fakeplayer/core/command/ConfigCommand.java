package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.util.Components;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import io.github.hello09x.fakeplayer.core.repository.model.Configs;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public void listConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        var uuid = sender.getUniqueId();
        CompletableFuture.runAsync(() -> {
            var components = Arrays.stream(Configs.values()).map(config -> {
                var value = String.valueOf(repository.selectOrDefault(uuid, config));
                return textOfChildren(
                        i18n.translate(config, GOLD),
                        text(": ", GRAY),
                        text(value, WHITE)
                );
            }).toList();

            var message = Components.join(components, newline());
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> sender.sendMessage(message));
        });
    }


}
