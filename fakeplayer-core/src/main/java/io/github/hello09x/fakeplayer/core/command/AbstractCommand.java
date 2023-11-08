package io.github.hello09x.fakeplayer.core.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.bedrock.i18n.I18n;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractCommand {

    protected final FakeplayerManager fakeplayerManager = FakeplayerManager.instance;
    protected final FakeplayerConfig config = FakeplayerConfig.instance;
    protected final MiniMessage miniMessage = MiniMessage.miniMessage();
    protected final I18n i18n = Main.i18n();

    protected @NotNull Player getTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        return this.getTarget(sender, args, null);
    }

    /**
     * 获取命令指定的假人
     * <ul>
     *     <li>如果玩家指定了名称, 则返回对应的假人</li>
     *     <li>如果没有指定名称, 并且玩家仅有一个假人, 则返回该假人</li>
     *     <li>如果提供了筛选条件并且没有指定名称, 经筛选后仅有一个假人, 则返回该假人</li>
     *     <li>不能找到唯一的一个假人时, 返回错误给玩家</li>
     * </ul>
     *
     * @param sender    命令发送方
     * @param args      命令参数
     * @param predicate 过滤条件
     * @return 假人
     * @throws WrapperCommandSyntaxException 找不到唯一的假人时抛出次异常
     */
    protected @NotNull Player getTarget(@NotNull CommandSender sender, @NotNull CommandArguments args, @Nullable Predicate<Player> predicate) throws WrapperCommandSyntaxException {
        var player = (Player) args.get("name");
        if (player != null) {
            return player;
        }

        var all = fakeplayerManager.getAll(sender, predicate);
        var count = all.size();
        return switch (count) {
            case 1 -> all.get(0);
            case 0 -> {
                if (predicate == null) {
                    throw CommandAPI.failWithString(i18n.asString(
                            "fakeplayer.command.generic.error.non-fake-player"
                    ));
                } else {
                   throw CommandAPI.failWithString(i18n.asString(
                            "fakeplayer.command.generic.error.non-matching-fake-player"
                    ));
                }
            }
            default -> throw CommandAPI.failWithString(i18n.asString(
                    "fakeplayer.command.generic.error.name-required"
            ));
        };
    }

    protected @NotNull List<Player> getTargets(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        @SuppressWarnings("unchecked")
        var players = (List<Player>) args.get("names");
        if (players == null || players.isEmpty()) {
            var fakeplayers = fakeplayerManager.getAll(sender);
            return switch (fakeplayers.size()) {
                case 1 -> fakeplayers;
                case 0 -> throw CommandAPI.failWithString(i18n.asString(
                        "fakeplayer.command.generic.error.non-fake-player"
                ));
                default -> throw CommandAPI.failWithString(i18n.asString(
                        "fakeplayer.command.generic.error.name-required"
                ));
            };
        }

        return players;
    }

}
