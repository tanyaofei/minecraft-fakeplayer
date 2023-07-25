package io.github.hello09x.fakeplayer.command.player.action;

import io.github.hello09x.fakeplayer.command.player.AbstractCommand;
import io.github.hello09x.fakeplayer.entity.action.ActionSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractActionCommand extends AbstractCommand {
    public AbstractActionCommand(@NotNull String description, @NotNull String usage, @Nullable String permission) {
        super(description, usage, permission);
    }

    public @Nullable  ActionSetting getActionSettings(String[] args) {
        var n = getArgs(args, "-n", "1");
        var i = getArgs(args, "-i", "1");
        var keep = hasFlag(args, "--keep") || hasFlag(args, "-k");

        int times;
        if (keep) {
            times = -1;
        } else {
            try {
                times = Integer.parseInt(n);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        int interval;
        try {
            interval = Integer.parseInt(i);
        } catch (NumberFormatException e) {
            return null;
        }

        return new ActionSetting(
                times,
                interval
        );
    }
}
