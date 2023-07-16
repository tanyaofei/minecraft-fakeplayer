package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.command.MessageException;
import io.github.hello09x.fakeplayer.manager.FakePlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class TpCommand extends AbstractTeleportCommand {


    private final FakePlayerManager manager = FakePlayerManager.instance;


    public final static TpCommand instance = new TpCommand(
            "传送到假人身边",
            "/fp tp <名称>",
            "fakeplayer"
    );


    public TpCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
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

        teleport(creator, fake);
        return true;
    }

}
