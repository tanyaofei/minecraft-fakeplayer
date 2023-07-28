package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.repository.UserConfigRepository;
import io.github.hello09x.fakeplayer.repository.model.Config;
import io.github.hello09x.fakeplayer.repository.model.Configs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ConfigCommand extends AbstractCommand {

    public final static ConfigCommand instance = new ConfigCommand();

    private final UserConfigRepository repository = UserConfigRepository.instance;

    public static Argument<Config<Object>> config(@NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            var arg = info.currentInput();
            try {
                return Configs.valueOf(arg);
            } catch (Exception e) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("未知的配置项: ").appendArgInput());
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(Arrays.stream(Configs.values()).map(Config::name).toList()));
    }

    public static Argument<Object> configValue(@NotNull String configNodeName, @NotNull String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            @SuppressWarnings("unchecked")
            var config = Objects.requireNonNull((Config<Object>) info.previousArgs().get(configNodeName));
            var arg = info.currentInput();
            if (!config.options().contains(arg)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("未知的配置值: ").appendArgInput());
            }
            return config.mapper().apply(arg);
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> {
            var config = Objects.requireNonNull((Config<?>) info.previousArgs().get(configNodeName));
            var arg = info.currentArg().toLowerCase();
            var options = config.options().stream();
            if (!arg.isEmpty()) {
                options = options.filter(o -> o.toLowerCase().contains(arg));
            }
            return options.toArray(String[]::new);
        }));
    }

    public void getConfig(@NotNull Player sender, @NotNull CommandArguments args) {
        Config<Object> config = Objects.requireNonNull(args.getUnchecked("config"));
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
        Config<Object> config = Objects.requireNonNull(args.getUnchecked("config"));
        Object value = Objects.requireNonNull(args.getUnchecked("value"));
        repository.saveOrUpdate(sender.getUniqueId(), config, value);
        sender.sendMessage(textOfChildren(
                text(config.label(), GOLD),
                text("变更为 ", GRAY),
                text(value.toString(), WHITE),
                text(" , 下次创建假人时生效", GRAY)
        ));
    }


}
