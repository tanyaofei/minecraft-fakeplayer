package io.github.hello09x.fakeplayer.command.player.exp;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ExpmeCommand extends AbstractCommand {

    public final static ExpmeCommand instance = new ExpmeCommand(
            "转移假人的经验值",
            "/fp expme [假人]",
            "fakeplayer.exp"
    );

    public ExpmeCommand(
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
        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage(text("你不是玩家...", RED));
            return true;
        }

        var exp = target.getTotalExperience();
        target.setTotalExperience(0);
        p.setTotalExperience(p.getTotalExperience() + exp);
        sender.sendMessage(textOfChildren(
                text(target.getName(), GRAY),
                text(" 转移 ", GRAY),
                text(exp, DARK_GREEN),
                text(" 点经验值给你", GRAY)
        ));
        return true;
    }
}
