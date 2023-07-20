package io.github.hello09x.fakeplayer.repository;

import com.google.common.base.Throwables;
import io.github.hello09x.fakeplayer.Main;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class UsedUUIDRepository {


    public final static UsedUUIDRepository instance = new UsedUUIDRepository();
    private final static Logger log = Main.getInstance().getLogger();

    private final Set<UUID> UUIDS = new HashSet<>();

    public UsedUUIDRepository() {
        this.load();
    }

    public boolean contains(@NotNull UUID uuid) {
        return UUIDS.contains(uuid);
    }

    public void add(@NotNull UUID uuid) {
        synchronized (UUIDS) {
            UUIDS.add(uuid);
        }
    }

    public void load() {
        var file = new File(Main.getInstance().getDataFolder(), "used-uuids.txt");
        if (!file.exists() || !file.isFile()) {
            return;
        }

        try (var in = new FileReader(file)) {
            for (var line : IOUtils.readLines(in)) {
                try {
                    if (line.isBlank()) {
                        continue;
                    }
                    this.UUIDS.add(UUID.fromString(line));
                } catch (Throwable ignored) {
                    log.warning("文件 used-uuids.txt 存在非法的 UUID: " + line);
                }
            }
        } catch (IOException e) {
            log.warning("无法读取 used-uuids.txt\n" + Throwables.getStackTraceAsString(e));
        }
    }

    public void save() {
        var folder = Main.getInstance().getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            return;
        }
        var file = new File(folder, "used-uuids.txt");

        try (var out = new FileWriter(file)) {
            IOUtils.writeLines(UUIDS, null, out);
        } catch (IOException e) {
            log.warning("无法保存 used-uuids.txt\n" + Throwables.getStackTraceAsString(e));
        }
    }


}
