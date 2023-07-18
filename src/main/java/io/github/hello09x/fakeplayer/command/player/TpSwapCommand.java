package io.github.hello09x.fakeplayer.command.player;

import io.github.hello09x.fakeplayer.command.MessageException;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class TpSwapCommand extends AbstractTeleportCommand {

    public final static TpSwapCommand instance = new TpSwapCommand(
            "与假人交换位置",
            "/fp tps <名称>",
            "fakeplayer.tp"
    );

    public TpSwapCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
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

        var l1 = creator.getLocation();
        var l2 = fake.getLocation();

        fake.teleport(l1, PlayerTeleportEvent.TeleportCause.PLUGIN);
        l1.getWorld().playSound(l1, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        creator.teleport(l2, PlayerTeleportEvent.TeleportCause.PLUGIN);
        l2.getWorld().playSound(l2, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}
