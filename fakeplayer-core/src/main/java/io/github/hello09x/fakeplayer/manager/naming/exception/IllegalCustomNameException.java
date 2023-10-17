package io.github.hello09x.fakeplayer.manager.naming.exception;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public class IllegalCustomNameException extends IllegalArgumentException {

    @Getter
    private final TextComponent text;

    public IllegalCustomNameException(@NotNull TextComponent message) {
        super(message.content());
        this.text = message;
    }

}
