package io.github.hello09x.fakeplayer.command.player.control;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class AttackCommand extends AbstractCommand {

    public final static AttackCommand instance = new AttackCommand(
            "控制假人点击鼠标左键",
            "/fp attach [名称] [频率]",
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
            sender.sendMessage(text("请指定假人名称或看向他...", RED));
            return true;
        }

        var entity = target.getTargetEntity(5);
        if (entity != null) {
            target.swingMainHand();
            target.attack(entity);
            return true;
        }
        return true;
    }

}
