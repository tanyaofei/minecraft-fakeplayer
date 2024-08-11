package io.github.hello09x.fakeplayer.core.manager;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.utils.SchedulerUtils;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.repository.FakePlayerSkinRepository;
import io.github.hello09x.fakeplayer.core.repository.model.FakePlayerSkin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * @author tanyaofei
 * @since 2024/8/8
 **/
@Singleton
public class FakeplayerSkinManager {

    private final static Logger log = Main.getInstance().getLogger();
    private final FakePlayerSkinRepository repository;
    private final FakeplayerConfig config;
    private final Cache<UUID, PlayerProfile> profileCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    @Inject
    public FakeplayerSkinManager(FakePlayerSkinRepository repository, FakeplayerConfig config) {
        this.repository = repository;
        this.config = config;
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
            // 非玩家创建的假人只能采用在线皮肤
            if (config.isDefaultOnlineSkin()) {
                this.useOnlineSkinAsync(to, Bukkit.getOfflinePlayer(to.getName()));
                return;
            }
            return;
        }

        // 使用以前配置过的
        var skin = repository.selectByCreatorIdAndPlayerId(p.getUniqueId(), to.getUniqueId());
        if (skin != null) {
            this.useOnlineSkinAsync(to, Bukkit.getOfflinePlayer(skin.targetId()));
            return;
        }

        if (config.isDefaultOnlineSkin()) {
            // 使用真实皮肤
            this.useOnlineSkinAsync(to, Bukkit.getOfflinePlayer(to.getName()));
        } else {
            // 使用召唤者皮肤
            this.useSkin(to, p);
        }
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
    public @NotNull CompletableFuture<Boolean> useOnlineSkinAsync(@NotNull Player to, @NotNull OfflinePlayer from) {
        if (this.useSkin(to, from)) {
            return CompletableFuture.completedFuture(true);
        }

        var profile = from.getPlayerProfile();
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return profile.complete() ? ProfileCompleteResult.SUCCESS : ProfileCompleteResult.FAILED;
                    } catch (Exception e) {
                        log.warning("Failed to update skin of fake player %s since could not fetch online profile from mojang\n%s".formatted(
                                Optional.ofNullable(from.getName())
                                        .orElse(from.getUniqueId().toString()),
                                Throwables.getStackTraceAsString(e)
                        ));
                        return ProfileCompleteResult.ERROR;
                    }
                })
                .thenComposeAsync(result -> {
                    if (result == ProfileCompleteResult.SUCCESS && profile.hasTextures()) {
                        profileCache.put(from.getUniqueId(), profile);
                    }

                    return SchedulerUtils.runTask(Main.getInstance(), () -> switch (result) {
                        case SUCCESS -> {
                            try {
                                this.setTexture(to, profile);
                                yield true;
                            } catch (Exception e) {
                                yield false;
                            }
                        }
                        case FAILED -> {
                            log.warning("Failed to update online skin of fakeplayer %s, maybe not a real player".formatted(
                                    Optional.ofNullable(from.getName())
                                            .orElse(from.getUniqueId().toString()))
                            );
                            yield false;
                        }
                        case ERROR -> false;

                    });
                });
    }

    private void setTexture(@NotNull Player to, @NotNull PlayerProfile fromProfile) {
        var toProfile = to.getPlayerProfile();
        toProfile.setTextures(fromProfile.getTextures());
        fromProfile.getProperties().stream().filter(p -> p.getName().equals("textures")).findAny().ifPresent(toProfile::setProperty);
        to.setPlayerProfile(toProfile);
    }

    private enum ProfileCompleteResult {
        SUCCESS,
        FAILED,
        ERROR
    }


}
