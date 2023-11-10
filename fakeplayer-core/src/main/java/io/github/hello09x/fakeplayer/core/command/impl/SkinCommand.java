package io.github.hello09x.fakeplayer.core.command.impl;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.Main;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class SkinCommand extends AbstractCommand {

    public final static SkinCommand instance = new SkinCommand();

    private final static int COOL_DOWN_TICKS = 1200;

    private final Map<CommandSender, MutableInt> cd = new HashMap<>();

    private SkinCommand() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            cd.entrySet().removeIf(counter -> counter.getValue().decrementAndGet() <= 0);
        }, 0, 1);
    }

    /**
     * 复制皮肤
     */
    public void skin(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);

        var player = Objects.requireNonNull((OfflinePlayer) args.get("player"));
        var profile = player.getPlayerProfile();
        if (profile.hasTextures()) {
            this.setTextures(target, profile);
            return;
        }

        // 拷贝离线玩家皮肤
        if (!sender.isOp() && cd.computeIfAbsent(sender, k -> new MutableInt()).getValue() != 0) {
            // 限制请求数, 防止 mojang api 限流
            sender.sendMessage(i18n.translate("fakeplayer.command.skin.error.too-many-operations", RED));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            // complete 会发起网络请求, 需要异步处理
            if (profile.complete()) {
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.setTextures(target, profile));
            }
            cd.computeIfAbsent(sender, k -> new MutableInt()).setValue(COOL_DOWN_TICKS);
        });
    }

    /**
     * 设置皮肤
     *
     * @param target 目标玩家
     * @param source 皮肤来源
     */
    public void setTextures(@NotNull Player target, @NotNull PlayerProfile source) {
        var profile = target.getPlayerProfile();
        profile.setTextures(source.getTextures());
        source.getProperties().stream().filter(p -> p.getName().equals("textures")).findAny().ifPresent(profile::setProperty);
        target.setPlayerProfile(profile);
    }

}
