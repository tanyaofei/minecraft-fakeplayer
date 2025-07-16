package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/**
 * @author tanyaofei
 * @since 2025/7/16
 **/
public abstract class Attributes {

    /**
     * <ul>
     *     <li><= 1.12.5 GENERIC_MAX_HEALTH</li>
     *     <li>>= 1.21.6 MAX_HEALTH</li>
     * </ul>
     *
     * @return
     */
    public static @NotNull Attribute maxHealth() {
        try {
            return (Attribute) Attribute.class.getField("GENERIC_MAX_HEALTH").get(Attribute.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                return (Attribute) Attribute.class.getField("MAX_HEALTH").get(Attribute.class);
            } catch (Exception e1) {
                throw new NoSuchElementException("No attribute found for MAX_HEALTH or GENERIC_MAX_HEALTH");
            }
        }
    }


}
