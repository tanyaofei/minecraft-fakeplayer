package io.github.hello09x.fakeplayer.manager.naming;

import java.util.UUID;

public record SequenceName(
        String group,
        Integer sequence,
        UUID uuid,
        String name
) {
}
