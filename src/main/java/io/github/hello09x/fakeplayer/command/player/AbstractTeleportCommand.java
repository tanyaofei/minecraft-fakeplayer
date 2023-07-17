package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.command.MessageException;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import io.github.tanyaofei.plugin.toolkit.command.ExecutableCommand;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT;

public abstract class AbstractTeleportCommand extends ExecutableCommand {


    protected final FakePlayerManager manager = FakePlayerManager.instance;

    public AbstractTeleportCommand(
            @NotNull String description,
            @NotNull String usage,
            @Nullable String permission
    ) {
        super(description, usage, permission);
    }

    public @NotNull Player getFakePlayer(
            @NotNull Player creator,
            @NotNull String[] args
    ) throws MessageException {
        if (creator.isOp()) {
            if (args.length == 0) {
                throw new MessageException(text("请指定假人的名字...", RED));
            }

            var name = args[0];
            var fake = manager.getFakePlayer(name);
            if (fake == null) {
                throw new MessageException(text("假人不存在...", RED));
            }
            return fake;
        }

        if (args.length == 0) {
            var fakes = manager.getFakePlayers(creator);
            if (fakes.isEmpty()) {
                throw new MessageException(text("你还没有创建假人...", RED));
            }

            if (fakes.size() > 1) {
                throw new MessageException(text("请指定假人的名字...", RED));
            }

            return fakes.get(0);
        }

        var name = args[0];
        var fake = manager.getFakePlayer(creator, name);
        if (fake == null) {
            throw new MessageException(text("假人不存在...", RED));
        }
        return fake;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        if (!(sender instanceof Player creator)) {
            return Collections.emptyList();
        }

        Stream<String> names = creator.isOp()
                ? manager.getFakePlayers().stream().map(Player::getName)
                : manager.getFakePlayers(creator).stream().map(Player::getName);

        if (args[0].isEmpty()) {
            return names.toList();
        } else {
            return names.filter(name -> name.toLowerCase().contains(args[0].toLowerCase())).toList();
        }
    }

    protected void teleport(@NotNull Player from, @NotNull Player to) {
        from.teleport(to.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        to.getLocation().getWorld().playSound(to.getLocation(), ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }
}
