package io.github.hello09x.fakeplayer.repository.model;

import java.util.List;
import java.util.function.Function;

/**
 * @param name         配置项 key
 * @param label        中文名称
 * @param defaultValue 默认值
 * @param options      可选值
 * @param mapper       转换器
 */
public record Config<T>(


        String name,


        String label,

        T defaultValue,

        List<String> options,

        Function<String, T> mapper

) {


}
