package io.github.hello09x.fakeplayer.command.player.control;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import io.github.hello09x.fakeplayer.manager.FakeplayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;

public class AttackCommand extends AbstractCommand {

    private final static FakeplayerManager manager = FakeplayerManager.instance;

    public final static AttackCommand instance = new AttackCommand(
            "控制假人点击鼠标左键",
            "/fp attach [假人名称] [频率]",
            "fakeplayer.control"
    );

    public AttackCommand(
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

        if (args.length != 2) {
            manager.setAttack(target, -1);
        } else {
            int period;
            try {
                period = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                return false;
            }

            if (period < 10) {
                period = 10;
            }
            manager.setAttack(target, period);
        }

        return true;
    }

}
