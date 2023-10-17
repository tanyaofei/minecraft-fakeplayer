package io.github.hello09x.fakeplayer.api.nms;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface NMSServerPlayer {

    Player getPlayer();

    double getX();

    double getY();

    double getZ();

    void setXo(double xo);

    void setYo(double yo);

    void setZo(double zo);

    void doTick();

    void absMoveTo(double x, double y, double z, float yRot, float xRot);

    float getYRot();

    void setYRot(float yRot);

    float getXRot();

    void setXRot(float xRot);

    void setZza(float zza);

    void setXxa(float xxa);

    boolean startRiding(@NotNull Entity entity, boolean flag);

    void stopRiding();

    void unpersistAdvancements(@NotNull Plugin plugin);

    void copyTexture(@NotNull Player from);

    int getTickCount();

    void drop(int slot, boolean flag1, boolean flag2);

    void drop(boolean all);

    void resetLastActionTime();

    boolean onGround();

    void jumpFromGround();

    void setJumping(boolean jumping);

    boolean isUsingItem();

    void setPlayBefore();

}
