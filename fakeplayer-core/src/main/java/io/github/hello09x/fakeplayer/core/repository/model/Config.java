package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.bedrock.i18n.TranslateKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * @param name         配置项 key
 * @param translateKey 翻译 key
 * @param defaultValue 默认值
 * @param options      可选值
 * @param mapper       转换器
 */
public record Config<T>(
        String name,
        String translateKey,
        T defaultValue,
        List<String> options,
        Function<String, T> mapper

) implements TranslateKey {

    @Override
    public @NotNull String translateKey() {
        return this.translateKey;
    }

}
