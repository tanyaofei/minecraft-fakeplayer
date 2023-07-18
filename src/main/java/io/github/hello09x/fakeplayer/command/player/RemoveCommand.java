package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class RemoveCommand extends ExecutableCommand {

    public final static RemoveCommand instance = new RemoveCommand(
            "移除假人",
            "/fp remove",
            "fakeplayer.spawn"
    );

    private final FakePlayerManager manager = FakePlayerManager.instance;

    public RemoveCommand(
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
        var removed = new ArrayList<>(args.length);
        if (args.length < 1) {
            sender.sendMessage(text("请指定要移除的假人名称...", RED));
            return true;
        }

        for (var name : args) {
            if (name.isEmpty()) {
                continue;
            }

            if (sender.isOp() && (name.equals("@all") || name.equals("@a"))) {
                var count = manager.removeFakePlayers();
                sender.sendMessage(text(String.format("已移除 %d 个假人", count), GRAY));
                return true;
            }

            if (manager.removeFakePlayer(name)) {
                removed.add(name);
            }

        }

        if (removed.isEmpty()) {
            sender.sendMessage(text("找不到对应名称的假人...", RED));
            return true;
        }

        sender.sendMessage(text("已移除这些假人: " + StringUtils.join(" ", removed), GRAY));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length != 1) {
            return sender.isOp() ? Collections.singletonList("@all") : Collections.emptyList();
        }

        var fakers = sender.isOp()
                ? manager.getFakePlayers()
                : manager.getFakePlayers(sender);

        var names = sender.isOp()
                ? Stream.concat(fakers.stream().map(Player::getName), Stream.of("@all"))
                : fakers.stream().map(Player::getName);

        return names
                .filter(name -> name.equals("@all") || args[0].isBlank() || name.toLowerCase().contains(args[0].toLowerCase()))
                .toList();
    }
}
