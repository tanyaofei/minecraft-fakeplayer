package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class RemoveCommand extends ExecutableCommand {

    public final static RemoveCommand instance = new RemoveCommand(
            "移除假人",
            "/fp remove",
            "fakeplayer"
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
        if (args.length == 0) {
            return false;
        }

        if (!(sender instanceof Player creator)) {
            sender.sendMessage(text("你不是玩家...", RED));
            return true;
        }

        var name = args[0];
        if (name.equals("@all") || name.equals("@a")) {
            int count = manager.removeFakePlayers(creator);
            sender.sendMessage(text(String.format("已移除 %d 个假人", count), GRAY));
            return true;
        }

        var fake = creator.isOp()
                ? manager.getFakePlayer(name)
                : manager.getFakePlayer(creator, name);

        if (fake == null) {
            sender.sendMessage(text("假人不存在", RED));
            return true;
        }

        fake.kick();
        sender.sendMessage(text("成功移除假人", GRAY));
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
            return Collections.emptyList();
        }
        if (!(sender instanceof Player creator)) {
            return Collections.emptyList();
        }

        var fakes = creator.isOp()
                ? manager.getFakePlayers()
                : manager.getFakePlayers(creator);

        return fakes
                .stream()
                .map(Player::getName)
                .filter(name -> args[0].isBlank() || name.toLowerCase().contains(args[0].toLowerCase()))
                .toList();
    }
}
