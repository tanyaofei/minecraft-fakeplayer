package io.github.hello09x.fakeplayer.command.admin;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import properties.FakeplayerProperties;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class ReloadCommand extends ExecutableCommand {

    public final static ReloadCommand instance = new ReloadCommand(
            "重载配置文件",
            "/fp reload",
            "fakeplayer.admin"
    );

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    public ReloadCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
       properties.reload();
       sender.sendMessage(text("重载成功", GRAY));
       return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
