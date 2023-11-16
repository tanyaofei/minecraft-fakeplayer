package io.github.hello09x.fakeplayer.api.spi;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface NMSGamePacketListener {

    int MESSAGE_HISTORY_SIZE = 50;

    static String BUNGEE_CORD_CHANNEL = "BungeeCord";

    /**
     * @return 获取最后一条消息
     */
    @Nullable ReceivedMessage getLastMessage();

    /**
     * 获取最近消息
     *
     * @return 最近消息
     */
    default @NotNull List<ReceivedMessage> getRecentMessages() {
        return getRecentMessages(0, Integer.MAX_VALUE);
    }

    /**
     * 获取最近的消息
     *
     * @param skip 跳过
     * @param size 数量
     * @return 获取最近的消息
     */
    @NotNull List<ReceivedMessage> getRecentMessages(int skip, int size);

    record ReceivedMessage(
            int id,
            Component content
    ) {

    }

}
