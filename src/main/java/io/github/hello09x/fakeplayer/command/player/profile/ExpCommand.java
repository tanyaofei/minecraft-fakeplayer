package io.github.hello09x.fakeplayer.command.player.profile;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class ExpCommand extends AbstractCommand {


    public final static ExpCommand instance = new ExpCommand(
            "查看经验值",
            "/fp exp [假人]",
            "fakeplayer.profile"
    );


    public ExpCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }

        var level = target.getLevel();
        var total = target.getTotalExperience();
        sender.sendMessage(textOfChildren(
                text(target.getName(), GRAY),
                text(" 当前 ", GRAY),
                text(level, DARK_GREEN),
                text(" 级, 共 ", GRAY),
                text(total, DARK_GREEN),
                text(" 点经验值", GRAY)
        ));
        return true;
    }
}
