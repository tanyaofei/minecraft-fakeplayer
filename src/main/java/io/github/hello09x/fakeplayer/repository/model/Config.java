package io.github.hello09x.fakeplayer.repository.model;

import java.util.List;
import java.util.function.Function;

public record Config<T>(

        String name,

        String label,

        T defaultValue,

        List<String> options,

        Function<String, T> mapper

) {



}
