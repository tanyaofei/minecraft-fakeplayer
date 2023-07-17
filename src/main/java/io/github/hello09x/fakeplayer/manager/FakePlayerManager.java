package io.github.hello09x.fakeplayer.manager;

import com.mojang.authlib.GameProfile;
import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.entity.FakePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import properties.FakeplayerProperties;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class FakePlayerManager {

    public final static FakePlayerManager instance = new FakePlayerManager();

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    public synchronized void spawnFakePlayer(
            @NotNull CommandSender creator,
            @NotNull Location at
    ) {
        var existed = getFakePlayers(creator).size();
        if (!creator.isOp() && existed >= properties.getMaximum()) {
            creator.sendMessage(text("你创建的假人数量已达到上限...", RED));
            return;
        }

        var name = creator.getName();
        var suffix = "_" + (existed + 1);
        if (name.length() + suffix.length() > 16) {
            name = name.substring(0, (16 - suffix.length()));
        }
        name = name + suffix;

        var player = new FakePlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) at.getWorld()).getHandle(),
                UUID.randomUUID(),
                name,
                at
        );
        var p = player.prepare();
        p.setMetadata(FakePlayerSpawner.META_KEY_CREATOR, new FixedMetadataValue(Main.getInstance(), creator.getName()));
    }

    public @Nullable Player getFakePlayer(@NotNull CommandSender creator, @NotNull String name) {
        var fake = getFakePlayer(name);
        if (fake == null) {
            return null;
        }

        var c = getCreator(fake);
        if (c == null || !c.equals(creator.getName())) {
            return null;
        }

        return fake;
    }

    public @Nullable Player getFakePlayer(@NotNull String name) {
        var player = Bukkit.getServer().getPlayer(name);
        if (player == null) {
            return null;
        }

        if (!isFakePlayer(player)) {
            return null;
        }

        return player;
    }

    public int removeFakePlayers(@NotNull Player creator) {
        var fakes = getFakePlayers(creator);
        for (var f : fakes) {
            f.kick();
        }
        return fakes.size();
    }

    public @Nullable String getCreator(@NotNull Player fakePlayer) {
        var meta = fakePlayer.getMetadata(FakePlayerSpawner.META_KEY_CREATOR);
        if (meta.isEmpty()) {
            return null;
        }

        return meta.get(0).asString();
    }

    public int removeFakePlayers() {
        var fakes = getFakePlayers();
        for (var f : fakes) {
            f.kick();
        }
        return fakes.size();
    }

    public @NotNull List<Player> getFakePlayers() {
        return Bukkit
                .getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> !p.getMetadata(FakePlayerSpawner.META_KEY_CREATOR).isEmpty())
                .collect(Collectors.toList());
    }

    public @NotNull List<Player> getFakePlayers(@NotNull CommandSender creator) {
        var name = creator.getName();
        return Bukkit
                .getServer()
                .getOnlinePlayers()
                .stream()
                .filter(p -> p.getMetadata(FakePlayerSpawner.META_KEY_CREATOR)
                        .stream()
                        .anyMatch(meta -> meta.asString().equals(name)))
                .collect(Collectors.toList());
    }

    public boolean isFakePlayer(@NotNull Player player) {
        return !player.getMetadata(FakePlayerSpawner.META_KEY_CREATOR).isEmpty();
    }


}
