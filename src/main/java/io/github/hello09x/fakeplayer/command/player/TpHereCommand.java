package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.command.MessageException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class TpHereCommand extends AbstractTeleportCommand {

    public final static TpHereCommand instance = new TpHereCommand(
            "传送假人到身边",
            "/fp tphere <名称>",
            "fakeplayer"
    );

    public TpHereCommand(
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
        if (!(sender instanceof Player creator)) {
            sender.sendMessage(text("你不是玩家...", RED));
            return true;
        }

        Player fake;
        try {
            fake = getFakePlayer(creator, args);
        } catch (MessageException e) {
            sender.sendMessage(e.getText());
            return true;
        }

        teleport(fake, creator);
        return true;
    }


}
