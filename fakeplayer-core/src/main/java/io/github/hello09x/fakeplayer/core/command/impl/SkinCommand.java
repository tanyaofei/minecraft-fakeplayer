package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerSkinManager;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

@Singleton
public class SkinCommand extends AbstractCommand {

    private final Map<CommandSender, MutableInt> spams = new HashMap<>();

    private final FakeplayerSkinManager manager;

    @Inject
    public SkinCommand(FakeplayerSkinManager manager) {
        this.manager = manager;
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            spams.entrySet().removeIf(counter -> counter.getValue().decrementAndGet() <= 0);
        }, 0, 1);
    }

    /**
     * 复制皮肤
     */
    public void skin(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        var fake = getFakeplayer(sender, args);
        var player = Objects.requireNonNull((OfflinePlayer) args.get("player"));

        if (manager.useSkin(fake, player)) {
            manager.rememberSkin(sender, fake, player);
            return;
        }

        // 限制请求数, 防止 mojang api 限流
        if (!sender.isOp() && spams.computeIfAbsent(sender, k -> new MutableInt()).getValue() != 0) {
            sender.sendMessage(translatable("fakeplayer.command.skin.error.too-many-operations", RED));
            return;
        }

        try {
            this.manager.useSkinAsync(fake, player)
                        .thenAcceptAsync(success -> {
                            manager.rememberSkin(sender, fake, player);
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                if (success) {
                                    fake.sendMessage(translatable("fakeplayer.command.generic.success"));
                                }
                            });
                        });
        } finally {
            if (!sender.isOp()) {
                spams.put(sender, new MutableInt(1200));
            }
        }
    }

}
