package io.github.hello09x.fakeplayer.command.admin;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class RemoveAllCommand extends ExecutableCommand {

    private final FakePlayerManager manager = FakePlayerManager.instance;

    public final static RemoveAllCommand instance = new RemoveAllCommand(
            "移除所有假人",
            "/fp removeall",
            "fakeplayer.admin"
    );

    public RemoveAllCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 0) {
            var count = manager.removeFakePlayers();
            sender.sendMessage(text(String.format("已移除 %d 个假人", count), GRAY));
            return true;
        }

        // TODO
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return Collections.emptyList();
    }
}
