package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.util.Skins;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class SkinCommand extends AbstractCommand {

    public final static SkinCommand instance = new SkinCommand();

    private final Map<CommandSender, MutableInt> spams = new HashMap<>();

    private SkinCommand() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            spams.entrySet().removeIf(counter -> counter.getValue().decrementAndGet() <= 0);
        }, 0, 1);
    }

    /**
     * 复制皮肤
     */
    public void skin(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var target = getTarget(sender, args);
        var player = Objects.requireNonNull((OfflinePlayer) args.get("player"));

        if (Skins.copySkin(player, target)) {
            return;
        }

        // 调用 Mojang API 来拷贝皮肤
        if (!sender.isOp() && spams.computeIfAbsent(sender, k -> new MutableInt()).getValue() != 0) {
            // 限制请求数, 防止 mojang api 限流
            sender.sendMessage(i18n.translate("fakeplayer.command.skin.error.too-many-operations", RED));
            return;
        }
        try {
            Skins.copySkinFromMojang(Main.getInstance(), player, target);
        } finally {
            spams.put(sender, new MutableInt(1200));
        }
    }

}
