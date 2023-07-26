package io.github.hello09x.fakeplayer.command;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.fakeplayer.properties.FakeplayerProperties;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;

public class ReloadCommand extends AbstractCommand {

    public final static ReloadCommand instance = new ReloadCommand();

    private final FakeplayerProperties properties = FakeplayerProperties.instance;

    public void reload(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        properties.reload();
        sender.sendMessage(text("重载配置文件完成", GRAY));
    }

}
