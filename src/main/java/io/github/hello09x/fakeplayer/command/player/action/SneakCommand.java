package io.github.hello09x.fakeplayer.command.player.action;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class SneakCommand extends AbstractCommand {

    public final static SneakCommand instance = new SneakCommand(
            "让假人潜行或取消",
            "/fp sneak [假人]",
            "fakeplayer.action"
    );

    public SneakCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }

        var sneaking = !target.isSneaking();
        target.setSneaking(sneaking);

        sender.sendMessage(textOfChildren(
                text(target.getName(), GRAY),
                text(sneaking ? "潜行" : "取消潜行", WHITE)
        ));

        return true;
    }
}
