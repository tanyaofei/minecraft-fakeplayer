package io.github.hello09x.fakeplayer.command;

import lombok.Getter;
import net.kyori.adventure.text.Component;

public class MessageException extends Exception {

    @Getter
    private final Component text;

    public MessageException(Component text) {
        this.text = text;
    }

}
