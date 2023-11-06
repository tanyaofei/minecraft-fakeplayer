package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.wrappers.Rotation;
import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.util.Mth;
import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
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

        String baseline;
        if (setting.equals(ActionSetting.stop())) {
            baseline = i18n.asString("fakeplayer.command.action.stop");
        } else if (setting.equals(ActionSetting.once())) {
            baseline = i18n.asString("fakeplayer.command.action.once");
        } else {
            baseline = i18n.asString("fakeplayer.command.action.continuous");
        }

        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + baseline + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("action", i18n.translate(action.translateKey(), WHITE))
        ));
    }

    public void swap(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var item1 = target.getInventory().getItemInMainHand();
        var item2 = target.getInventory().getItemInOffHand();

        target.getInventory().setItemInMainHand(item2);
        target.getInventory().setItemInOffHand(item1);

        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.swap.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE))
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

        if (sneaking) {
            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + i18n.asString("fakeplayer.command.sneak.enabled") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
        } else {
            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + i18n.asString("fakeplayer.command.sneak.disabled") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void lookAt(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var location = Objects.requireNonNull((Location) args.get("location"));
        target.lookAt(location, LookAnchor.EYES);
        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.look.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE)),
                Placeholder.component("direction", text(toLocationString(location), WHITE))
        ));
    }

    public CommandExecutor look(@NotNull Direction direction) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            look(target, direction);

            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + i18n.asString("fakeplayer.command.look.success") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE)),
                    Placeholder.component("direction", i18n.translate(direction.translateKey(), WHITE))
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
            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + i18n.asString("fakeplayer.command.move.success") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
        };
    }

    public CommandExecutor turn(float yaw, float pitch) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            turn(target, yaw, pitch);
            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + i18n.asString("fakeplayer.command.turn.success") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
        };
    }

    public void turnTo(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var rotation = Objects.requireNonNull((Rotation) args.get("rotation"));
        turn(target, rotation.getYaw(), rotation.getPitch());
        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.turn.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE))
        ));
    }

    private void turn(@NotNull Player player, float yaw, float pitch) {
        look(player, player.getYaw() + yaw, player.getPitch() + pitch);
    }


}
