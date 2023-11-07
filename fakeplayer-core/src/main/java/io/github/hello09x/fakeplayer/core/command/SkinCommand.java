package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.core.Main;
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
        var player = Objects.requireNonNull((OfflinePlayer) args.get("player"));
        var target = getTarget(sender, args);
        var profile = target.getPlayerProfile();

        var from = player.getPlayerProfile();
        var copy = (Runnable) () -> {
            profile.setTextures(from.getTextures());
            from.getProperties().stream().filter(p -> p.getName().equals("textures")).findAny().ifPresent(profile::setProperty);
            target.setPlayerProfile(profile);
        };

        if (!from.isComplete()) {
            if (!sender.isOp() && cd.computeIfAbsent(sender, k -> new MutableInt()).getValue() != 0) {
                sender.sendMessage(i18n.translate("fakeplayer.command.skin.error.too-many-operations", RED));
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                if (from.complete()) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), copy);
                }
                cd.computeIfAbsent(sender, k -> new MutableInt()).setValue(COOL_DOWN_TICKS);
            });
        } else {
            copy.run();
        }
    }

}
