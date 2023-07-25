package io.github.hello09x.fakeplayer.command.player.tp;

import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class TpToCommand extends AbstractTpCommand {

    private final FakeplayerManager manager = FakeplayerManager.instance;


    public final static TpToCommand instance = new TpToCommand(
            "传送到假人身边",
            "/fp tp [假人]",
            "fakeplayer.tp"
    );


    public TpToCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
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

        var target = getTarget(sender, args);
        if (target == null) {
            return false;
        }

        teleport(creator, target);
        return true;
    }

}
