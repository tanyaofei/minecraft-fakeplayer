package io.github.hello09x.fakeplayer.api.spi;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface NMSGamePacketListener {

    int MESSAGE_HISTORY_SIZE = 50;

    /**
     * @return 获取最后一条消息
     */
    @Nullable ReceivedMessage getLastMessage();

    /**
     * 获取最近消息消息
     *
     * @return 最近消息
     */
    @NotNull List<ReceivedMessage> getRecentMessages();

    record ReceivedMessage(
            int id,
            Component content
    ) {

    }

}
