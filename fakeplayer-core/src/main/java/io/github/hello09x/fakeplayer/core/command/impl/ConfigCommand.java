package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.util.Components;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.UserConfigManager;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
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

    private final UserConfigManager manager = UserConfigManager.instance;

    /**
     * 获取单项配置
     */
    public void getConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        @SuppressWarnings("unchecked")
        var config = (Config<Object>) Objects.requireNonNull(args.get("option"));
        var value = String.valueOf(manager.getConfig(sender, config));
        sender.sendMessage(
                textOfChildren(
                        i18n.translate(config).color(GOLD),
                        text(": ", GRAY),
                        text(value, WHITE)
                )
        );
    }

    /**
     * 设置配置
     */
    public void setConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        @SuppressWarnings("unchecked")
        var config = (Config<Object>) Objects.requireNonNull(args.get("option"));

        if (!config.hasPermission(sender)) {
            sender.sendMessage(i18n.translate("fakeplayer.command.config.set.error.no-permission", RED));
            return;
        }

        var value = Objects.requireNonNull(args.get("value"));
        manager.setConfig(sender, config, value);
        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.config.set.success") + "</gray>",
                Placeholder.component("option", i18n.translate(config, GOLD)),
                Placeholder.component("value", text(value.toString(), WHITE))
        ));
    }

    /**
     * 获取所有配置
     */
    public void listConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        CompletableFuture.runAsync(() -> {
            var components = Arrays.stream(Config.values()).map(config -> {
                var value = String.valueOf(manager.getConfig(sender, config));
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
