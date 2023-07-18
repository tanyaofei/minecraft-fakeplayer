package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class CreateCommand extends ExecutableCommand {

    public final static CreateCommand instance = new CreateCommand(
            "创建假人",
            "/fp create [名称]",
            "fakeplayer.spawn"
    );

    private final FakePlayerManager manager = FakePlayerManager.instance;

    public CreateCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (sender instanceof Player p) {
            manager.spawnFakePlayer(
                    p,
                    ((Player) sender).getLocation()
            );
        } else {
            if (args.length != 3) {
                return false;
            }

            double x, y, z;
            try {
                x = Double.parseDouble(args[0]);
                y = Double.parseDouble(args[1]);
                z = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                return false;
            }

            var world = sender.getServer().getWorlds().get(0);
            manager.spawnFakePlayer(sender, new Location(world, x, y, z));
        }

        sender.sendMessage(text("创建假人成功", GRAY));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] strings
    ) {
        return Collections.emptyList();
    }
}
