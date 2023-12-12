package io.github.hello09x.fakeplayer.v1_20_R1.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.constant.ConstantPool;
import io.github.hello09x.fakeplayer.core.util.Reflections;
import io.github.hello09x.fakeplayer.v1_20_R1.network.DummyPlayerAdvancements;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class NMSServerPlayerImpl implements NMSServerPlayer {

    private final static Field ServerPlayer$advancements = Reflections.getFirstFieldByType(
            ServerPlayer.class,
            PlayerAdvancements.class,
            false
    );

    @Getter
    private final ServerPlayer handle;

    @Getter
    private final CraftPlayer player;

    public NMSServerPlayerImpl(@NotNull Player player) {
        this.player = ((CraftPlayer) player);
        this.handle = ((CraftPlayer) player).getHandle();
    }

    @Override
    public double getX() {
        return handle.getX();
    }

    @Override
    public double getY() {
        return handle.getY();
    }

    @Override
    public double getZ() {
        return handle.getZ();
    }

    @Override
    public void setXo(double xo) {
        handle.xo = xo;
    }

    @Override
    public void setYo(double yo) {
        handle.yo = yo;
    }

    @Override
    public void setZo(double zo) {
        handle.zo = zo;
    }

    @Override
    public void doTick() {
        handle.doTick();
    }

    @Override
    public void absMoveTo(double x, double y, double z, float yRot, float xRot) {
        handle.absMoveTo(x, y, z, yRot, xRot);
    }

    @Override
    public float getYRot() {
        return handle.getYRot();
    }

    @Override
    public void setYRot(float yRot) {
        handle.setYRot(yRot);
    }

    @Override
    public float getXRot() {
        return handle.getXRot();
    }

    @Override
    public void setXRot(float xRot) {
        handle.setXRot(xRot);
    }

    @Override
    public void setZza(float zza) {
        handle.zza = zza;
    }

    @Override
    public void setXxa(float xxa) {
        handle.xxa = xxa;
    }

    @Override
    public boolean startRiding(@NotNull Entity entity, boolean force) {
        return handle.startRiding(new NMSEntityImpl(entity).getHandle(), force);
    }

    @Override
    public void stopRiding() {
        handle.stopRiding();
    }


    @Override
    public int getTickCount() {
        return handle.tickCount;
    }

    @Override
    public void drop(boolean allStack) {
        handle.drop(allStack);
    }

    @Override
    public void resetLastActionTime() {
        handle.resetLastActionTime();
    }

    @Override
    public boolean onGround() {
        return handle.onGround();
    }

    @Override
    public void jumpFromGround() {
        handle.jumpFromGround();
    }

    @Override
    public void setJumping(boolean jumping) {
        handle.setJumping(jumping);
    }

    @Override
    public boolean isUsingItem() {
        return handle.isUsingItem();
    }

    @Override
    public void disableAdvancements(@NotNull Plugin plugin) {
        if (ServerPlayer$advancements == null) {
            return;
        }

        var server = ((CraftServer) Bukkit.getServer()).getServer();
        try {
            ServerPlayer$advancements.set(
                    handle,
                    new DummyPlayerAdvancements(
                            server.getFixerUpper(),
                            server.getPlayerList(),
                            server.getAdvancements(),
                            plugin.getDataFolder().getParentFile().toPath(),
                            handle
                    )
            );
        } catch (IllegalAccessException ignored) {
        }
    }

    @Override
    public void drop(int slot, boolean flag, boolean flag1) {
        var inventory = handle.getInventory();
        handle.drop(inventory.removeItem(slot, inventory.getItem(slot).getCount()), flag, flag1);
    }

    @Override
    public void setPlayBefore() {
        player.readExtraData(new CompoundTag());
    }

    @Override
    public void setupClientOptions() {
        var option = new ServerboundClientInformationPacket(
                "en_us",
                Bukkit.getViewDistance(),
                ChatVisiblity.SYSTEM,
                false,
                ConstantPool.MODEL_CUSTOMISATION,
                HumanoidArm.RIGHT,
                false,
                true
        );
        handle.updateOptions(option);
    }

    @Override
    public void respawn() {
        if (!this.player.isDead()) {
            return;
        }

        var packet = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
        handle.connection.handleClientCommand(packet);
    }

    public void swapItemWithOffhand() {
        handle.connection.handlePlayerAction(new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                new BlockPos(0, 0, 0),
                Direction.DOWN
        ));
    }

}
