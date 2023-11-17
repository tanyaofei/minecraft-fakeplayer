package io.github.hello09x.fakeplayer.core.manager.naming.exception;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class IllegalCustomNameException extends IllegalArgumentException {

    @Getter
    private final Component text;

    public IllegalCustomNameException(@NotNull Component message) {
        super(PlainTextComponentSerializer.plainText().serialize(message));
        this.text = message;
    }

}
