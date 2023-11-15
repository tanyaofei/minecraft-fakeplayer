package io.github.hello09x.fakeplayer.core.manager.naming;

import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.naming.exception.IllegalCustomNameException;
import io.github.hello09x.fakeplayer.core.repository.UsedIdRepository;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class NameManager {

    public final static NameManager instance = new NameManager();
    private final static Logger log = Main.getInstance().getLogger();
    private final static MiniMessage miniMessage = MiniMessage.miniMessage();
    private final static int MAX_LENGTH = 16;   // mojang required
    private final static int MIN_LENGTH = 3; // mojang required
    private final static String FALLBACK_NAME = "_fp_";

    private final UsedIdRepository usedIdRepository = UsedIdRepository.instance;
    private final FakeplayerConfig config = FakeplayerConfig.instance;
    private final Map<String, NameSource> nameSources = new HashMap<>();
    private final I18n i18n = Main.getI18n();

    private final String serverId;

    public NameManager() {
        var file = new File(Main.getInstance().getDataFolder(), "serverid");
        serverId = Optional.ofNullable(readServerId(file)).orElseGet(() -> {
            var uuid = UUID.randomUUID().toString();
            try (var out = new FileWriter(file, StandardCharsets.UTF_8)) {
                IOUtils.write(uuid, out);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return uuid;
        });
    }

    private static @Nullable String readServerId(@NotNull File file) {
        if (!file.exists()) {
            return null;
        }

        String serverId;
        try (var in = new FileReader(file, StandardCharsets.UTF_8)) {
            serverId = IOUtils.toString(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return serverId.isBlank() ? null : serverId;
    }

    /**
     * 通过名称生成 UUID
     *
     * @param name 名称
     * @return UUID
     */
    private @NotNull UUID uuidFromName(@NotNull String name) {
        var base = serverId + ":" + name;
        var uuid = UUID.nameUUIDFromBytes(base.getBytes(StandardCharsets.UTF_8));
        if (!usedIdRepository.exists(uuid) && Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
            uuid = UUID.randomUUID();
            log.warning(String.format("Could not generate a UUID bound with name '%s' which is never played at this server, using random UUID as fallback: %s", name, uuid));
        }
        return uuid;
    }

    /**
     * 通过自定义名称获取序列名
     *
     * @param creator 创建者
     * @param name    自定义名称
     * @return 序列名
     */
    public @NotNull SequenceName custom(@NotNull CommandSender creator, @NotNull String name) {
        if (name.startsWith("-")) {
            throw new IllegalCustomNameException(miniMessage.deserialize(
                    "<red>" + i18n.asString("fakeplayer.spawn.error.name.start-with-illegal-character") + "/<red>",
                    Placeholder.component("character", text("-", WHITE))
            ));
        }

        if (name.length() > MAX_LENGTH) {
            throw new IllegalCustomNameException(miniMessage.deserialize(
                    "<red>" + i18n.asString("fakeplayer.spawn.error.name.too-long") + "</red>",
                    Placeholder.component("length", text(MAX_LENGTH, WHITE))
            ));
        }

        if (name.length() < MIN_LENGTH) {
            throw new IllegalCustomNameException(miniMessage.deserialize(
                    "<red>" + i18n.asString("fakeplayer.spawn.error.name.too-short") + "</red>",
                    Placeholder.component("length", text(MIN_LENGTH, WHITE))
            ));
        }

        if (!config.getNamePattern().asPredicate().test(name)) {
            throw new IllegalCustomNameException(i18n.translate("fakeplayer.spawn.error.name.invalid", RED));
        }

        if (Bukkit.getPlayerExact(name) != null || Bukkit.getOfflinePlayer(name).hasPlayedBefore()) {
            throw new IllegalCustomNameException(i18n.translate("fakeplayer.spawn.error.name.existed", RED));
        }

        return new SequenceName(
                "custom",
                0,
                uuidFromName(creator.getName() + ":" + name),
                name
        );
    }

    /**
     * 获取一个序列名
     *
     * @param creator 创建者
     * @return 序列名
     */
    public @NotNull SequenceName register(@NotNull CommandSender creator) {
        var source = config.getNameTemplate();
        if (source.isBlank()) {
            source = creator.getName();
        }
        source = source.replace("%c", creator.getName());

        int tries = 10;
        while (tries != 0) {
            var seq = nameSources.computeIfAbsent(source, key -> new NameSource(config.getPlayerLimit())).pop();
            var suffix = "_" + (seq + 1);

            String name;
            if (source.length() + suffix.length() > MAX_LENGTH) {
                name = source.substring(0, (MAX_LENGTH - suffix.length()));
            } else {
                name = source;
            }
            name += suffix;

            if (Bukkit.getPlayerExact(name) != null) {
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
     * @param group    分组
     * @param sequence 序列
     */
    public void unregister(@NotNull String group, int sequence) {
        Optional.ofNullable(nameSources.get(group)).ifPresent(ns -> ns.push(sequence));
    }

    /**
     * 归还序列名
     *
     * @param sn 序列名
     */
    public void unregister(@NotNull SequenceName sn) {
        this.unregister(sn.group(), sn.sequence());
    }


}
