package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.api.spi.Action;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class ActionCommand extends AbstractCommand {

    private final ActionManager actionManager;

    @Inject
    public ActionCommand(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public @NotNull CommandExecutor action(@NotNull Action.ActionType action, @NotNull Action.ActionSetting setting) {
        return (sender, args) -> action(sender, args, action, setting.clone());
    }

    /**
     * 执行动作
     */
    public void action(
            @NotNull CommandSender sender,
            @NotNull CommandArguments args,
            @NotNull Action.ActionType action,
            @NotNull Action.ActionSetting setting
    ) throws WrapperCommandSyntaxException {
        var fake = super.getFakeplayer(sender, args);
        if (action == Action.ActionType.USE
                && fake.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD
                && manager.isAutofish(fake)
        ) {
            // 如果是自动钓鱼则改为 1 次
            setting = Action.ActionSetting.once();
        }

        actionManager.setAction(fake, action, setting);
        if (!setting.equals(Action.ActionSetting.once()) || sender instanceof ConsoleCommandSender) {
            sender.sendMessage(translatable("fakeplayer.command.generic.success"));
        }
    }

}
