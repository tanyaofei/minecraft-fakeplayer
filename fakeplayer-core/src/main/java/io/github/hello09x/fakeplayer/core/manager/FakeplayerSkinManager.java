package io.github.hello09x.fakeplayer.core.manager;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.utils.SchedulerUtils;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.repository.FakePlayerSkinRepository;
import io.github.hello09x.fakeplayer.core.repository.model.FakePlayerSkin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author tanyaofei
 * @since 2024/8/8
 **/
@Singleton
public class FakeplayerSkinManager {

    private final FakePlayerSkinRepository repository;
    private final Cache<UUID, PlayerProfile> profileCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    @Inject
    public FakeplayerSkinManager(FakePlayerSkinRepository repository) {
        this.repository = repository;
    }

    @CanIgnoreReturnValue
    public boolean rememberSkin(@NotNull CommandSender creator, @NotNull Player to, @NotNull OfflinePlayer from) {
        if (!(creator instanceof Player p)) {
            return false;
        }

        repository.insertOrUpdate(new FakePlayerSkin(
                to.getUniqueId(),
                p.getUniqueId(),
                from.getUniqueId()
        ));
        return true;
    }

    public void useDefaultSkin(@NotNull CommandSender creator, @NotNull Player to) {
        if (!(creator instanceof Player p)) {
            return;
        }

        // 使用以前配置过的
        var skin = repository.selectByCreatorIdAndPlayerId(p.getUniqueId(), to.getUniqueId());
        if (skin != null) {
            this.useSkinAsync(to, Bukkit.getOfflinePlayer(skin.targetId()));
            return;
        }

        // 使用召唤者皮肤
        this.useSkin(to, p);
    }

    @CanIgnoreReturnValue
    public boolean useSkin(@NotNull Player to, @NotNull OfflinePlayer from) {
        var profile = from.getPlayerProfile();
        if (!profile.hasTextures()) {
            profile = profileCache.getIfPresent(from.getUniqueId());
        }

        if (profile == null || !profile.hasTextures()) {
            return false;
        }
        this.setTexture(to, profile);
        return true;
    }

    @CanIgnoreReturnValue
    public @NotNull CompletableFuture<Boolean> useSkinAsync(@NotNull Player to, @NotNull OfflinePlayer from) {
        if (this.useSkin(to, from)) {
            return CompletableFuture.completedFuture(true);
        }

        var profile = from.getPlayerProfile();
        return CompletableFuture
                .supplyAsync(profile::complete)
                .thenComposeAsync(success -> {
                    if (success && profile.hasTextures()) {
                        profileCache.put(from.getUniqueId(), profile);
                    }
                    return SchedulerUtils.runTask(Main.getInstance(), () -> {
                        if (!success) {
                            return false;
                        }
                        try {
                            this.setTexture(to, profile);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    });
                });
    }

    private void setTexture(@NotNull Player to, @NotNull PlayerProfile fromProfile) {
        var toProfile = to.getPlayerProfile();
        toProfile.setTextures(fromProfile.getTextures());
        fromProfile.getProperties().stream().filter(p -> p.getName().equals("textures")).findAny().ifPresent(toProfile::setProperty);
        to.setPlayerProfile(toProfile);
    }


}
