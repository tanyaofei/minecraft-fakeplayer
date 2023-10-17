package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpCommand extends AbstractCommand {

    public final static HelpCommand instance = new HelpCommand();

    public void help(@NotNull Player sender, @NotNull CommandArguments args) {
        var page = (int) args.getOptional("page").orElse(1);
        sender.performCommand("help fakeplayer " + page);
    }


}
