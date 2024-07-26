package io.github.hello09x.fakeplayer.v1_20_R5_R6.network;

import com.mojang.datafixers.DataFixer;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.nio.file.Path;

public class FakePlayerAdvancements extends PlayerAdvancements {

    public FakePlayerAdvancements(
            DataFixer datafixer,
            PlayerList playerlist,
            ServerAdvancementManager manager,
            Path path,
            ServerPlayer player
    ) {
        super(datafixer, playerlist, manager, path, player);
        this.save();
    }

    @Override
    public boolean award(AdvancementHolder advancementholder, String s) {
        return false;
    }

    @Override
    public void flushDirty(ServerPlayer player) {
    }

    @Override
    public AdvancementProgress getOrStartProgress(AdvancementHolder advancement) {
        return new AdvancementProgress();
    }

    @Override
    public boolean revoke(AdvancementHolder advancement, String s) {
        return false;
    }

    @Override
    public void save() {
    }

    @Override
    public void setPlayer(ServerPlayer player) {
    }

    @Override
    public void setSelectedTab(AdvancementHolder advancement) {
    }

    @Override
    public void stopListening() {

    }


}
