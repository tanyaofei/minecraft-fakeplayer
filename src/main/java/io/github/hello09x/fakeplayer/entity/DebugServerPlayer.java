package io.github.hello09x.fakeplayer.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DebugServerPlayer extends ServerPlayer {
    public DebugServerPlayer(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile);
    }

    @Override
    public void tick() {
        System.out.println("tick");
        super.tick();
    }

    @Override
    public void doTick() {
        System.out.println("dotick");
        super.doTick();
    }

    @Override
    public void baseTick() {
        System.out.println("baseTick");
        super.baseTick();
    }
}
