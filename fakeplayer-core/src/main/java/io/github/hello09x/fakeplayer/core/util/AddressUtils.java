package io.github.hello09x.fakeplayer.core.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Optional;

public class AddressUtils {


    public static String getAddress(@NotNull CommandSender sender) {
        if (sender instanceof Player p) {
            return Optional.ofNullable(p.getAddress()).map(InetSocketAddress::getHostString).orElse("<unknown>");
        } else {
            return "0.0.0.0";
        }
    }

}
