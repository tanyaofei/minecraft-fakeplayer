package io.github.hello09x.fakeplayer.core.manager.naming;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 序列名
 *
 * @param group    分组
 * @param sequence 序列
 * @param uuid     UUID
 * @param name     名称
 */
public record SequenceName(

        @NotNull
        String group,

        int sequence,

        @NotNull
        UUID uuid,

        @NotNull
        String name

) {
}
