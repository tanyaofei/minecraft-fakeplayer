package io.github.hello09x.fakeplayer.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class DebugServerPlayer extends ServerPlayer {
    public DebugServerPlayer(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void doTick() {
        super.doTick();
    }

    @Override
    public void baseTick() {
        super.baseTick();
    }

    @Override
    public void setPos(double d0, double d1, double d2) {
        System.out.println(Arrays.asList(d0, d1, d2));
        setPos0(d0, d1, d2);
    }

    public void setPos0(double d0, double d1, double d2) {
        super.setPos(d0, d1, d2);
    }

    @Override
    public void move(MoverType enummovetype, Vec3 vec3d) {
        if (this.getY() + vec3d.y > 0) {
            System.out.println(this.getY());
            System.out.println(vec3d);
        }
        super.move(enummovetype, vec3d);
    }
}
