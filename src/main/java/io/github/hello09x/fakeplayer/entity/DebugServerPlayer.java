package io.github.hello09x.fakeplayer.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

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
    public void move(MoverType enummovetype, Vec3 vec3d) {
        if (this.tickCount == 19) {
            return;
        }
        super.move(enummovetype, vec3d);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }
}
