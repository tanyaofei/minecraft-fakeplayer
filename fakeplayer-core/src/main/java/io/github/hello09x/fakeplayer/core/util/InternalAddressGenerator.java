package io.github.hello09x.fakeplayer.core.util;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class InternalAddressGenerator {

    private final AtomicInteger next = new AtomicInteger(1);

    /**
     * 判断 IP 地址能否由本工具类生成
     *
     * @param address 地址
     * @return 能否生成
     */
    public static boolean canBeGenerated(@NotNull InetAddress address) {
        return address.getAddress()[0] == 127;
    }

    /**
     * 获取下一个 IP 地址
     *
     * @return IP 地址
     */
    @SneakyThrows
    public @NotNull InetAddress next() {
        var ip = next.getAndIncrement();
        // max 127.255.255.254
        if (ip == 0xfffffe) {
            next.set(0);
        }

        var p2 = (ip >> 16) & 0xff;
        var p3 = (ip >> 8) & 0xff;
        var p4 = ip & 0xff;

        return InetAddress.getByAddress(new byte[]{127, (byte) p2, (byte) p3, (byte) p4});
    }

}
