package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.util.Components;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.UserConfigManager;
import io.github.hello09x.fakeplayer.core.repository.model.Config;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

@Singleton
public class ConfigCommand extends AbstractCommand {

    private final UserConfigManager configManager;

    @Inject
    public ConfigCommand(UserConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * 设置配置
     */
    public void setConfig(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        @SuppressWarnings("unchecked")
        var config = (Config<Object>) Objects.requireNonNull(args.get("config"));

        if (!config.hasPermission(sender)) {
            throw CommandAPI.failWithString(translator.asString(
                    "fakeplayer.command.config.set.error.no-permission",
                    TranslatorUtils.getLocale(sender)
            ));
        }

        var locale = TranslatorUtils.getLocale(sender);
        var value = Objects.requireNonNull(args.get("value"));
        configManager.setConfig(sender, config, value);
        sender.sendMessage(translator.translate(
                "fakeplayer.command.config.set.success",
                locale,
                GRAY,
                Placeholder.component("config", translator.translate(config.translationKey(), locale, GOLD)),
                Placeholder.component("value", text(value.toString(), WHITE))
        ));
    }

    /**
     * 获取所有配置
     */
    public void listConfig(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        CompletableFuture.runAsync(() -> {
            var components = Arrays.stream(Config.values()).map(config -> {
                var options = new ArrayList<>(config.options());
                var value = String.valueOf(configManager.getConfig(sender, config));
                return textOfChildren(
                        translator.translate(config, TranslatorUtils.getLocale(sender), GOLD),
                        text(": ", GRAY),
                        join(JoinConfiguration.separator(space()), options.stream().map(option -> {
                            var style = option.equals(value) ? Style.style(GREEN, UNDERLINED) : Style.style(GRAY);
                            return text("[" + option + "]").style(style).clickEvent(
                                    runCommand("/fp config set " + config.key() + " " + option)
                            );
                        }).toList())
                );
            }).toList();

            var message = Components.join(components, newline());
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> sender.sendMessage(message));
        });
    }


}
