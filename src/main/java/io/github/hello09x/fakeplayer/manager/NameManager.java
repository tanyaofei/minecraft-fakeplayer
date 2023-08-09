package io.github.hello09x.fakeplayer.manager;

import io.github.hello09x.fakeplayer.Main;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class NameManager {

    public final static NameManager instance = new NameManager();
    private final static Logger log = Main.getInstance().getLogger();
    private final static int MAX_LENGTH = 16;   // mojang required
    private final static int MIN_LENGTH = 3; // mojang required
    private final static String FALLBACK_NAME = "_fp_";
    private final FakeplayerProperties properties = FakeplayerProperties.instance;
    private final Map<String, NameSource> nameSources = new HashMap<>();

    /**
     * 通过自定义名称获取序列名
     *
     * @param creator 创建者
     * @param name    自定义名称
     * @return 序列名
     */
    public @Nullable SequenceName custom(@NotNull CommandSender creator, @NotNull String name) {
        if (name.startsWith("-")) {
            creator.sendMessage(textOfChildren(text("名称不能以", GRAY), text(" - ", RED), text("开头", GRAY)));
            return null;
        }

        if (name.length() > MAX_LENGTH) {
            creator.sendMessage(text(String.format("名称最多 %d 位字符", MAX_LENGTH), GRAY));
            return null;
        }

        if (name.length() < MIN_LENGTH) {
            creator.sendMessage(text(String.format("名称最少 %d 位字符", MIN_LENGTH), GRAY));
            return null;
        }

        if (!properties.getCustomNamePattern().asPredicate().test(name)) {
            creator.sendMessage(text("名称不符合格式要求", GRAY));
            return null;
        }

        if (Bukkit.getPlayerExact(name) != null || Bukkit.getOfflinePlayer(name).hasPlayedBefore()) {
            creator.sendMessage(text("名称已被使用", GRAY));
            return null;
        }

        return new SequenceName(
                "custom",
                0,
                UUID.randomUUID(),
                name
        );
    }

    /**
     * 获取一个序列名
     *
     * @param creator 创建者
     * @return 序列名
     */
    public @NotNull SequenceName take(@NotNull CommandSender creator) {
        var source = properties.getNameTemplate();
        if (source.isBlank()) {
            source = creator.getName();
        }
        source = source.replace("%c", creator.getName());

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

            if (Bukkit.getServer().getOfflinePlayer(name).hasPlayedBefore() || Bukkit.getPlayerExact(name) != null) {
                tries--;
                continue;
            }

            return new SequenceName(
                    source,
                    seq,
                    uuidFromName(name),
                    name
            );
        }

        var name = FALLBACK_NAME + RandomStringUtils.random(MAX_LENGTH - FALLBACK_NAME.length(), true, true);
        log.warning("Could not generate a name which is never used at this server after 10 tries, using random player name as fallback: " + name);
        return new SequenceName("random", 0, uuidFromName(name), name);
    }

    /**
     * 归还序列名
     *
     * @param source   来源
     * @param sequence 序列
     */
    public void giveback(@NotNull String source, @NotNull Integer sequence) {
        Optional.ofNullable(nameSources.get(source)).ifPresent(ns -> ns.push(sequence));
    }

    /**
     * 归还序列名
     *
     * @param sequenceName 序列名
     */
    public void giveback(@NotNull SequenceName sequenceName) {
        this.giveback(sequenceName.source, sequenceName.sequence);
    }

    /**
     * 通过名称生成 UUID
     *
     * @param name 名称
     * @return UUID
     */
    private @NotNull UUID uuidFromName(@NotNull String name) {
        var uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        if (Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
            uuid = UUID.randomUUID();
            log.warning(String.format("Could not generate a UUID bound with name '%s' which is never played at this server, using random UUID as fallback: %s", name, uuid));
        }
        return uuid;
    }

    public record SequenceName(
            String source,
            Integer sequence,
            UUID uniqueId,
            String name

    ) {

    }


}
