package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.wrappers.Rotation;
import io.github.hello09x.fakeplayer.core.constant.Direction;
import io.github.hello09x.fakeplayer.core.util.Mth;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Singleton
public class RotationCommand extends AbstractCommand {

    /**
     * 看向给定坐标
     */
    @SuppressWarnings("UnstableApiUsage")
    public void lookAt(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var location = Objects.requireNonNull((Location) args.get("location"));
        target.lookAt(location, LookAnchor.EYES);
    }

    /**
     * 看向给定方向
     */
    public CommandExecutor look(@NotNull Direction direction) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            look(target, direction);
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
        var handle = bridge.fromPlayer(player);
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
        };
    }

    /**
     * 转向指定角度
     */
    public void turnTo(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var rotation = Objects.requireNonNull((Rotation) args.get("rotation"));
        this.turn(target, rotation.getYaw(), rotation.getPitch());
    }

    /**
     * 转向指定方向
     */
    private void turn(@NotNull Player player, float yaw, float pitch) {
        var pos = player.getLocation();
        this.look(player, pos.getYaw() + yaw, pos.getPitch() + pitch);
    }


}
