package io.github.hello09x.fakeplayer.core.config;

/**
 * @author tanyaofei
 * @since 2024/7/26
 **/
public enum PreventKicking {

    /**
     * 不进行任何处理
     */
    NEVER,

    /**
     * 创建时
     */
    ON_SPAWNING,

    /**
     * 永远, 除了假人插件自身
     */
    ALWAYS

}
