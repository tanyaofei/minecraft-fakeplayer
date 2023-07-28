package io.github.hello09x.fakeplayer.manager;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class NameManager {

    public final static NameManager instance = new NameManager();
    private final static Logger log = Main.getInstance().getLogger();
    private final static int MAX_LENGTH = 16;   // mojang required
    private final FakeplayerProperties properties = FakeplayerProperties.instance;
    private final ConcurrentHashMap<String, NameSource> nameSources = new ConcurrentHashMap<>();

    public @NotNull SequenceName take(CommandSender creator) {
        var source = properties.getNameTemplate();
        if (source.isBlank()) {
            source = creator.getName();
        }

        int tries = 10;
        while (tries != 0) {
            var seq = nameSources.computeIfAbsent(source, key -> new NameSource(properties.getPlayerLimit())).pop();
            var suffix = "_" + (seq + 1);

            String name;
            if (source.length() + suffix.length() > MAX_LENGTH) {
                name = source.substring(0, (MAX_LENGTH - suffix.length()));
            } else {
                name = source;
            }
            name += suffix;

            if (Bukkit.getServer().getOfflinePlayer(name).hasPlayedBefore()) {
                tries--;
                continue;
            }

            return new SequenceName(
                    source,
                    seq,
                    name
            );
        }

        var name = "_fp_";
        name = name + RandomStringUtils.random(MAX_LENGTH - name.length(), true, true);
        log.warning("Could not generate a name which is never used at this server after 10 tries, using random player name as fallback: " + name);
        return new SequenceName("random", 0, name);
    }

    public void giveback(@NotNull String source, @NotNull Integer sequence) {
        Optional.ofNullable(nameSources.get(source)).ifPresent(ns -> ns.push(sequence));
    }


    public record SequenceName(
            String source,
            Integer sequence,
            String name
    ) {

    }

}
