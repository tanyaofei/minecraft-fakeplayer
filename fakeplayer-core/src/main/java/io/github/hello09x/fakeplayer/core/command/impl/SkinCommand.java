package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.transaction.TranslatorUtils;
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

@Singleton
public class SkinCommand extends AbstractCommand {

    private final Map<CommandSender, MutableInt> spams = new HashMap<>();

    public SkinCommand() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            spams.entrySet().removeIf(counter -> counter.getValue().decrementAndGet() <= 0);
        }, 0, 1);
    }

    /**
     * 复制皮肤
     */
    public void skin(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var locale = TranslatorUtils.getLocale(sender);
        var target = getTarget(sender, args);
        var player = Objects.requireNonNull((OfflinePlayer) args.get("player"));

        if (Skins.copySkin(player, target)) {
            return;
        }

        // 调用 Mojang API 来拷贝皮肤
        if (!sender.isOp() && spams.computeIfAbsent(sender, k -> new MutableInt()).getValue() != 0) {
            // 限制请求数, 防止 mojang api 限流
            sender.sendMessage(translator.translate("fakeplayer.command.skin.error.too-many-operations", locale, RED));
            return;
        }
        try {
            Skins.copySkinFromMojang(Main.getInstance(), player, target);
        } finally {
            spams.put(sender, new MutableInt(1200));
        }
    }

}
