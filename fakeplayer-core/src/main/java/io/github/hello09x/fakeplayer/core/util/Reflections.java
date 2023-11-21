package io.github.hello09x.fakeplayer.core.util;

import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Reflections {

    public static @Nullable Field getFirstFieldByType(
            @NotNull Class<?> clazz,
            @NotNull Class<?> fieldType,
            boolean includeStatic
    ) {
        for (var field : clazz.getDeclaredFields()) {
            if (includeStatic ^ Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.getType() == fieldType) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }


    public static @N Field getFistFieldByTypeIncludeParent(
            @NotNull Class<?> clazz,
            @NotNull Class<?> fieldType
    ) {
        for (var field : clazz.getDeclaredFields()) {
            if (field.getType() == fieldType) {
                field.setAccessible(true);
                return field;
            }
        }
        var superclass = clazz.getSuperclass();
        if (superclass == null || superclass == Object.class) {
            return null;
        }
        return getFistFieldByTypeIncludeParent(superclass, fieldType);
    }

    public static @Nullable Field getFirstFieldByAssignFromType(
            @NotNull Class<?> clazz,
            @NotNull Class<?> fieldType,
            boolean includeStatic
    ) {
        for (var field : clazz.getDeclaredFields()) {
            if (includeStatic ^ Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (fieldType.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

}
