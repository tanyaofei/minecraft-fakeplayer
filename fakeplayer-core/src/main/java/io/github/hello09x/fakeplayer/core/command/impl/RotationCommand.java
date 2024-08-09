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
        var fake = getFakeplayer(sender, args);
        var location = Objects.requireNonNull((Location) args.get("location"));
        fake.lookAt(location, LookAnchor.EYES);
    }

    /**
     * 看向给定方向
     */
    public CommandExecutor look(@NotNull Direction direction) {
        return (sender, args) -> {
            var fake = getFakeplayer(sender, args);
            look(fake, direction);
        };
    }

    @SuppressWarnings("UnstableApiUsage")
    public void lookMe(@NotNull Player sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException{
        var fake = getFakeplayer(sender, args);
        if (!Objects.equals(fake.getWorld(), sender.getWorld())) {
            return;
        }
        fake.lookAt(sender.getEyeLocation(), LookAnchor.EYES);
    }

    /**
     * 看向给定方向
     */
    private void look(
            @NotNull Player fake,
            @NotNull Direction direction
    ) {
        switch (direction) {
            case NORTH -> look(fake, 180, 0);
            case SOUTH -> look(fake, 0, 0);
            case EAST -> look(fake, -90, 0);
            case WEST -> look(fake, 90, 0);
            case UP -> look(fake, fake.getLocation().getYaw(), -90);
            case DOWN -> look(fake, fake.getLocation().getYaw(), 90);
        }
    }

    /**
     * 看向指定方向
     */
    private void look(@NotNull Player fake, float yaw, float pitch) {
        var handle = bridge.fromPlayer(fake);
        handle.setYRot(yaw % 360);
        handle.setXRot(Mth.clamp(pitch, -90, 90));
    }

    /**
     * 转向指定角度
     */
    public CommandExecutor turn(float yaw, float pitch) {
        return (sender, args) -> {
            var fake = getFakeplayer(sender, args);
            this.turn(fake, yaw, pitch);
        };
    }

    /**
     * 转向指定角度
     */
    public void turnTo(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = getFakeplayer(sender, args);
        var rotation = Objects.requireNonNull((Rotation) args.get("rotation"));
        this.turn(fake, rotation.getYaw(), rotation.getPitch());
    }

    /**
     * 转向指定方向
     */
    private void turn(@NotNull Player fake, float yaw, float pitch) {
        var pos = fake.getLocation();
        this.look(fake, pos.getYaw() + yaw, pos.getPitch() + pitch);
    }


}
