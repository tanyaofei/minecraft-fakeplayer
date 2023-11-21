package io.github.hello09x.fakeplayer.core.util;

import io.github.hello09x.bedrock.util.LazyInit;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public abstract class ClientboundSystemChatPackets {

    private final static MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private final static LazyInit<MethodHandle> ADVENTURE$CONTENT = new LazyInit<>();

    public static @Nullable Component getAdventureContent(@NotNull Object packet) {
        var adventure$content = ADVENTURE$CONTENT.computeIfAbsent(() -> {
            try {
                return LOOKUP.findVirtual(packet.getClass(), "adventure$content", MethodType.methodType(Component.class));
            } catch (Throwable e) {
                return null;
            }
        }, true);

        if (adventure$content != null) {
            try {
                return (Component) adventure$content.invoke(packet);
            } catch (Throwable e) {
                return null;
            }
        }
        return null;
    }
}
