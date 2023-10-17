package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.wrappers.Rotation;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.command.support.Direction;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.util.Mth;
import io.papermc.paper.entity.LookAnchor;
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

    private final ActionManager actionManager = ActionManager.instance;

    private static String toLocationString(@NotNull Location location) {
        return StringUtils.joinWith(", ",
                Mth.floor(location.getX(), 0.5),
                Mth.floor(location.getY(), 0.5),
                Mth.floor(location.getZ(), 0.5));
    }

    public CommandExecutor action(@NotNull ActionType action, @NotNull ActionSetting setting) {
        return (sender, args) -> action(sender, args, action, setting.clone());
    }

    public void action(
            @NotNull CommandSender sender,
            @NotNull CommandArguments args,
            @NotNull ActionType action,
            @NotNull ActionSetting setting
    ) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        actionManager.setAction(target, action, setting);

        String desc;
        if (setting.equals(ActionSetting.stop())) {
            desc = " 已停止";
        } else if (setting.equals(ActionSetting.once())) {
            desc = " ";
        } else {
            desc = " 开始";
        }

        sender.sendMessage(textOfChildren(
                text(target.getName()),
                text(desc, GRAY),
                text(action.getDisplayName(), GRAY)
        ));
    }

    public void swap(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var item1 = target.getInventory().getItemInMainHand();
        var item2 = target.getInventory().getItemInOffHand();

        target.getInventory().setItemInMainHand(item2);
        target.getInventory().setItemInOffHand(item1);

        sender.sendMessage(textOfChildren(
                text(target.getName()),
                text(" 交换手上物品", GRAY)
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

    @SuppressWarnings("UnstableApiUsage")
    public void lookAt(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var location = Objects.requireNonNull((Location) args.get("location"));
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
        switch (direction) {
            case NORTH -> look(target, 180, 0);
            case SOUTH -> look(target, 0, 0);
            case EAST -> look(target, -90, 0);
            case WEST -> look(target, 90, 0);
            case UP -> look(target, target.getYaw(), -90);
            case DOWN -> look(target, target.getYaw(), 90);
        }
    }

    private void look(@NotNull Player player, float yaw, float pitch) {
        var handle = Main.getVersionSupport().player(player);
        handle.setYRot(yaw % 360);
        handle.setXRot(Mth.clamp(pitch, -90, 90));
    }

    public CommandExecutor move(float forward, float strafing) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            var handle = Main.getVersionSupport().player(target);
            float vel = target.isSneaking() ? 0.3F : 1.0F;
            if (forward != 0.0F) {
                handle.setZza(vel * forward);
            }
            if (strafing != 0.0F) {
                handle.setXxa(vel * strafing);
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
            turn(target, yaw, pitch);
            sender.sendMessage(textOfChildren(
                    text(target.getName()),
                    text(" 动了一下身子", GRAY)
            ));
        };
    }

    public void turnTo(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var rotation = Objects.requireNonNull((Rotation) args.get("rotation"));
        turn(target, rotation.getYaw(), rotation.getPitch());
        sender.sendMessage(textOfChildren(
                text(target.getName()),
                text(" 动了一下身子", GRAY)
        ));
    }

    private void turn(@NotNull Player player, float yaw, float pitch) {
        look(player, player.getYaw() + yaw, player.getPitch() + pitch);
    }


}
