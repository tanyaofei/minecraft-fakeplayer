package io.github.hello09x.fakeplayer.command.player.config;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class ConfigSetCommand extends AbstractConfigCommand {

    public final static ConfigSetCommand instance = new ConfigSetCommand(
            "修改假人参数配置",
            "/fp config get <配置项> <配置值>",
            "fakeplayer.config"
    );


    public ConfigSetCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 2) {
            return false;
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage(text("你不是玩家...", RED));
            return true;
        }

        var config = getConfig(p, args);
        if (config == null) {
            return false;
        }

        Object value;
        try {
            value = config.mapper().apply(args[1]);
        } catch (Throwable e) {
            sender.sendMessage(text("配置值有误"));
            return true;
        }


        repository.saveOrUpdate(p.getUniqueId(), config, value);
        sender.sendMessage(text("修改假人配置成功, 在下一次创建假人时生效", GRAY));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 2 || !(sender instanceof Player p)) {
            return super.onTabComplete(sender, command, label, args);
        }

        var config = getConfig(p, args);
        if (config == null) {
            return Collections.emptyList();
        }

        var prefix = args[1].toLowerCase(Locale.ROOT);
        var options = config.options().stream();
        if (!prefix.isEmpty()) {
            options = options.filter(opt -> opt.toLowerCase().contains(prefix));
        }
        return options.toList();
    }
}
