package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.transaction.PluginTranslator;
import io.github.hello09x.devtools.core.transaction.TranslatorUtils;
import io.github.hello09x.fakeplayer.api.spi.NMSBridge;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class AbstractCommand {

    protected final static Logger log = Main.getInstance().getLogger();

    @Inject
    protected NMSBridge bridge;

    @Inject
    protected FakeplayerManager manager;

    @Inject
    protected FakeplayerConfig config;

    @Inject
    protected PluginTranslator translator;


    protected @NotNull Player getTarget(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        return this.getTarget(sender, args, null);
    }

    /**
     * 获取命令指定的假人
     * <ol>
     *     <li>如果玩家指定了名称, 则返回对应的假人</li>
     *     <li>如果玩家通过 /fp select 选择了假人, 则返回该假人</li>
     *     <li>如果玩家仅有一个假人, 则返回该假人</li>
     *     <li>不能找到唯一的一个假人时, 返回错误给玩家</li>
     * </ol>
     *
     * @param sender    命令发送方
     * @param args      命令参数
     * @param predicate 过滤条件
     * @return 假人
     * @throws WrapperCommandSyntaxException 找不到唯一的假人时抛出次异常
     */
    protected @NotNull Player getTarget(@NotNull CommandSender sender, @NotNull CommandArguments args, @Nullable Predicate<Player> predicate) throws WrapperCommandSyntaxException {
        var target = (Player) args.get("name");
        if (target == null && sender instanceof Player p) {
            target = manager.getSelection(p);
        }
        if (target != null) {
            return target;
        }
        var locale = TranslatorUtils.getLocale(sender);

        var all = manager.getAll(sender, predicate);
        var count = all.size();
        return switch (count) {
            case 1 -> all.get(0);
            case 0 -> {
                if (predicate == null) {
                    throw CommandAPI.failWithString(translator.asString(
                            "fakeplayer.command.generic.error.non-fake-player",
                            locale
                    ));
                } else {
                    throw CommandAPI.failWithString(translator.asString(
                            "fakeplayer.command.generic.error.non-matching-fake-player",
                            locale
                    ));
                }
            }
            default -> throw CommandAPI.failWithString(translator.asString(
                    "fakeplayer.command.generic.error.name-required",
                    locale
            ));
        };
    }

    protected @Nullable Player getTargetNullable(@NotNull CommandSender sender, @NotNull CommandArguments args) {
        return (Player) args.get("name");
    }

    protected @NotNull List<Player> getTargets(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
        @SuppressWarnings("unchecked")
        var players = (List<Player>) args.get("names");

        // 优先选中的假人
        if (players == null || players.isEmpty()) {
            var target = manager.getSelection(sender);
            if (target != null) {
                return Collections.singletonList(target);
            }
        }

        // 查找唯一假人
        if (players == null || players.isEmpty()) {
            var fakeplayers = manager.getAll(sender);
            return switch (fakeplayers.size()) {
                case 1 -> fakeplayers;
                case 0 -> throw CommandAPI.failWithString(translator.asString(
                        "fakeplayer.command.generic.error.non-fake-player",
                        TranslatorUtils.getLocale(sender)
                ));
                default -> throw CommandAPI.failWithString(translator.asString(
                        "fakeplayer.command.generic.error.name-required",
                        TranslatorUtils.getLocale(sender)
                ));
            };
        }

        return players;
    }

}
