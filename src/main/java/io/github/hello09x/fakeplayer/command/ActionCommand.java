package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.wrappers.Rotation;
import io.github.hello09x.fakeplayer.entity.action.Action;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import io.github.hello09x.fakeplayer.entity.action.PlayerActionManager;
import io.github.hello09x.fakeplayer.util.MathUtils;
import io.github.hello09x.fakeplayer.util.Unwrapped;
import io.papermc.paper.entity.LookAnchor;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class ActionCommand extends AbstractCommand {

    public final static ActionCommand instance = new ActionCommand();

    private final PlayerActionManager actionManager = PlayerActionManager.instance;

    private static String toLocationString(@NotNull Location location) {
        return StringUtils.joinWith(", ",
                MathUtils.round(location.getX(), 0.5),
                MathUtils.round(location.getY(), 0.5),
                MathUtils.round(location.getZ(), 0.5));
    }

    public CommandExecutor action(@NotNull Action action, @NotNull ActionSetting setting) {
        return (sender, args) -> action(sender, args, action, setting.clone());
    }

    public void action(
            @NotNull CommandSender sender,
            @NotNull CommandArguments args,
            @NotNull Action action,
            @NotNull ActionSetting setting
    ) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        actionManager.setAction(target, action, setting);

        String desc;
        if (setting.equals(ActionSetting.stop())) {
            desc = " 已停止";
        } else if (setting.equals(ActionSetting.once())) {
            desc = "";
        } else {
            desc = " 开始";
        }

        sender.sendMessage(textOfChildren(
                text(target.getName()),
                text(desc, GRAY),
                text(" "),
                text(action.name, GRAY)
        ));
    }

    public void sneak(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var sneaking = args
                .getOptional("sneaking")
                .map(String.class::cast)
                .map(Boolean::valueOf)
                .orElse(!target.isSneaking());

        target.setSneaking(sneaking);

        sender.sendMessage(textOfChildren(
                text(target.getName(), WHITE),
                text(" 现在", GRAY),
                text(sneaking ? "潜行中" : "取消了潜行", GRAY)
        ));
    }

    public void lookAt(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var location = (Location) args.get("location");
        target.lookAt(location, LookAnchor.EYES);
        sender.sendMessage(textOfChildren(
                text(target.getName(), WHITE),
                text(" 正在看向 ", GRAY),
                text(toLocationString(location), GRAY)
        ));
    }

    public CommandExecutor look(@NotNull Direction direction) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            look(target, direction);
            sender.sendMessage(textOfChildren(
                    text(target.getName(), WHITE),
                    text(" 看向 ", GRAY),
                    text(switch (direction) {
                        case DOWN -> "下方";
                        case UP -> "上方";
                        case NORTH -> "北边";
                        case SOUTH -> "南边";
                        case WEST -> "西边";
                        case EAST -> "东边";
                    }, GRAY)
            ));
        };
    }

    private void look(
            @NotNull Player target,
            @NotNull Direction direction
    ) {
        var player = Unwrapped.getServerPlayer(target);
        switch (direction) {
            case NORTH -> look(player, 180, 0);
            case SOUTH -> look(player, 0, 0);
            case EAST -> look(player, -90, 0);
            case WEST -> look(player, 90, 0);
            case UP -> look(player, player.getYRot(), -90);
            case DOWN -> look(player, player.getYRot(), 90);
        }
    }

    private void look(@NotNull ServerPlayer player, float yaw, float pitch) {
        player.setYRot(yaw % 360);
        player.setXRot(MathUtils.clamp(pitch, -90, 90));
    }

    public CommandExecutor move(float forward, float strafing) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            var player = Unwrapped.getServerPlayer(target);
            float vel = target.isSneaking() ? 0.3F : 1.0F;
            if (forward != 0.0F) {
                player.zza = vel * forward;
            }
            if (strafing != 0.0F) {
                player.xxa = vel * strafing;
            }
            sender.sendMessage(textOfChildren(
                    text(target.getName()),
                    text(" 动了一下", GRAY)
            ));
        };
    }

    public CommandExecutor turn(float yaw, float pitch) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            turn(Unwrapped.getServerPlayer(target), yaw, pitch);
            sender.sendMessage(textOfChildren(
                    text(target.getName()),
                    text(" 动了一下身子", GRAY)
            ));
        };
    }

    public void turnTo(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var rotation = Objects.requireNonNull((Rotation) args.get("rotation"));
        turn(Unwrapped.getServerPlayer(target), rotation.getYaw(), rotation.getPitch());
        sender.sendMessage(textOfChildren(
                text(target.getName()),
                text(" 动了一下身子", GRAY)
        ));
    }

    private void turn(@NotNull ServerPlayer player, float yaw, float pitch) {
        look(player, player.getYRot() + yaw, player.getXRot() + pitch);
    }


}
