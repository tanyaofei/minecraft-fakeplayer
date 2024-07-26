package io.github.hello09x.fakeplayer.api.spi;

import org.bukkit.plugin.messaging.StandardMessenger;

public interface NMSServerGamePacketListener {


    String BUNGEE_CORD_CHANNEL = "BungeeCord";

    String BUNGEE_CORD_CORRECTED_CHANNEL = StandardMessenger.validateAndCorrectChannel(BUNGEE_CORD_CHANNEL);

}
