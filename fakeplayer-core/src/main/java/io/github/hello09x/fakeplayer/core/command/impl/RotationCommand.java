package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.wrappers.Rotation;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.util.Mth;
import io.papermc.paper.entity.LookAnchor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RotationCommand extends AbstractCommand {

    public final static RotationCommand instance = new RotationCommand();

    private static String toLocationString(@NotNull Location location) {
        return StringUtils.joinWith(", ",
                Mth.floor(location.getX(), 0.5),
                Mth.floor(location.getY(), 0.5),
                Mth.floor(location.getZ(), 0.5));
    }

    /**
     * 看向给定坐标
     */
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

    /**
     * 看向给定方向
     */
    public CommandExecutor look(@NotNull Direction direction) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            look(target, direction);

            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + i18n.asString("fakeplayer.command.look.success") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE)),
                    Placeholder.component("direction", i18n.translate(direction, WHITE))
            ));
        };
    }

    /**
     * 看向给定方向
     */
    private void look(
            @NotNull Player target,
            @NotNull Direction direction
    ) {
        switch (direction) {
            case NORTH -> look(target, 180, 0);
            case SOUTH -> look(target, 0, 0);
            case EAST -> look(target, -90, 0);
            case WEST -> look(target, 90, 0);
            case UP -> look(target, target.getLocation().getYaw(), -90);
            case DOWN -> look(target, target.getLocation().getYaw(), 90);
        }
    }

    /**
     * 看向指定方向
     */
    private void look(@NotNull Player player, float yaw, float pitch) {
        var handle = Main.getVersionSupport().player(player);
        handle.setYRot(yaw % 360);
        handle.setXRot(Mth.clamp(pitch, -90, 90));
    }

    /**
     * 转向指定角度
     */
    public CommandExecutor turn(float yaw, float pitch) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            this.turn(target, yaw, pitch);
            sender.sendMessage(miniMessage.deserialize(
                    "<gray>" + i18n.asString("fakeplayer.command.turn.success") + "</gray>",
                    Placeholder.component("name", text(target.getName(), WHITE))
            ));
        };
    }

    /**
     * 转向指定角度
     */
    public void turnTo(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var rotation = Objects.requireNonNull((Rotation) args.get("rotation"));
        this.turn(target, rotation.getYaw(), rotation.getPitch());
        sender.sendMessage(miniMessage.deserialize(
                "<gray>" + i18n.asString("fakeplayer.command.turn.success") + "</gray>",
                Placeholder.component("name", text(target.getName(), WHITE))
        ));
    }

    /**
     * 转向指定方向
     */
    private void turn(@NotNull Player player, float yaw, float pitch) {
        var pos = player.getLocation();
        this.look(player, pos.getYaw() + yaw, pos.getPitch() + pitch);
    }


}
