package io.github.hello09x.fakeplayer.core.manager.naming.exception;

import io.github.hello09x.devtools.command.exception.CommandException;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class IllegalCustomNameException extends CommandException {

    public IllegalCustomNameException(@NotNull Component message) {
        super(message);
    }

}
