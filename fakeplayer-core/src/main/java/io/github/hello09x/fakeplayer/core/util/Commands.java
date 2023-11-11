package io.github.hello09x.fakeplayer.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands {

    public static List<String> formatCommands(@NotNull List<String> commands, @NotNull String @NotNull ... pa) {
        if ((pa.length & 1) != 0) {
            throw new IllegalArgumentException("parameter 'pa' should be paired");
        }

        var args = new HashMap<String, String>(pa.length / 2, 1.0F);
        for (int i = 0; i < pa.length; i += 2) {
            args.put(pa[i], pa[i + 1]);
        }

        var ret = new ArrayList<String>(commands.size());
        for (var cmd : commands) {
            cmd = cmd.trim();
            if (cmd.startsWith("/")) {
                if (cmd.length() < 2) {
                    continue;
                }
                cmd = cmd.substring(1);
            }

            if (!args.isEmpty()) {
                for (var entry : args.entrySet()) {
                    cmd = cmd.replace(entry.getKey(), entry.getValue());
                }
            }

            if (cmd.isBlank()) {
                continue;
            }
            ret.add(cmd);
        }
        return ret;
    }
}
