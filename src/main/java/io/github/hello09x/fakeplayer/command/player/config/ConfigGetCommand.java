package io.github.hello09x.fakeplayer.command.player.config;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ConfigGetCommand extends AbstractConfigCommand {

    public final static ConfigGetCommand instance = new ConfigGetCommand(
            "获取假人参数配置",
            "/fp config get <配置项>",
            null
    );


    public ConfigGetCommand(
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
        if (!(sender instanceof Player p)) {
            sender.sendMessage(text("你不是玩家...", RED));
            return true;
        }

        var config = getConfig(p, args);
        if (config == null) {
            return false;
        }

        var value = String.valueOf(repository.selectOrDefault(p.getUniqueId(), config));
        sender.sendMessage(textOfChildren(
                text(config.label(), GOLD),
                text(": ", GRAY),
                text(value, WHITE)
        ));
        return true;
    }

}
