package io.github.hello09x.fakeplayer.core;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;

public class EmptyNetworkManager extends NetworkManager {

    public EmptyNetworkManager(EnumProtocolDirection enumprotocoldirection) {
        super(enumprotocoldirection);
        this.m = new EmptyChannel(null);
        this.n = this.m.remoteAddress();
        this.preparing = false;
    }
}
