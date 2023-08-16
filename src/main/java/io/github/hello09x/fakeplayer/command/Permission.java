package io.github.hello09x.fakeplayer.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public interface Permission {

    String spawnName = "fakeplayer.spawn.name";
    String spawn = "fakeplayer.spawn";
    String spawnLocation = "fakeplayer.spawn.location";
    String profile = "fakeplayer.profile";
    String tp = "fakeplayer.tp";
    String exp = "fakeplayer.exp";
    String action = "fakeplayer.action";
    String admin = "fakeplayer.admin";
    String cmd = "fakeplayer.cmd";

    interface Keepalive {

        Duration permanent = Duration.ofNanos(-1);

        Map<String, Duration> _alive = new LinkedHashMap<>() {{
            put("fakeplayer.alive.permanent", permanent);
            put("fakeplayer.alive.24hour", Duration.ofHours(24));
            put("fakeplayer.alive.12hour", Duration.ofHours(12));
            put("fakeplayer.alive.8hour", Duration.ofHours(8));
            put("fakeplayer.alive.4hour", Duration.ofHours(4));
            put("fakeplayer.alive.2hour", Duration.ofHours(2));
            put("fakeplayer.alive.1hour", Duration.ofHours(1));
            put("fakeplayer.alive.30min", Duration.ofMinutes(30));
            put("fakeplayer.alive.15min", Duration.ofMinutes(15));
            put("fakeplayer.alive.1min", Duration.ofMinutes(1));
        }};

        static boolean isPermanent(@NotNull Duration duration) {
            return permanent.equals(duration);
        }

        static @Nullable Duration of(@NotNull CommandSender sender) {
            if (sender.isOp()) {
                return permanent;
            }
            for (var entry : _alive.entrySet()) {
                if (sender.hasPermission(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return null;
        }

        static @NotNull Duration of(@NotNull CommandSender sender, @NotNull Duration defaultValue) {
            return Optional.ofNullable(of(sender)).orElse(defaultValue);
        }
    }


}
