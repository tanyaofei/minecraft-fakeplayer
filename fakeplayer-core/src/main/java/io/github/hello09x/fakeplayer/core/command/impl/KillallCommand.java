package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.executors.CommandArguments;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KillallCommand extends AbstractCommand {

    public final static KillallCommand instance = new KillallCommand();

    /**
     * 移除服务器所有假人
     */
    public void killall(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        fakeplayerManager.removeAll("Command killall");
    }

}
