package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.hello09x.fakeplayer.core.entity.Fakeplayer;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class TagCommand {
    private final FakeplayerManager manager;

    public TagCommand(FakeplayerManager manager) {
        this.manager = manager;
    }

    public CommandAPICommand getCommand() {
        return new CommandAPICommand("tag")
                .withPermission(CommandPermission.fromString("fakeplayer.command.tag"))
                .withSubcommand(new CommandAPICommand("add")
                        .withArguments(new StringArgument("tag"))
                        .withOptionalArguments(new StringArgument("--all").setOptional(true))
                        .executesPlayer((player, args) -> {
                            String tag = (String) args.get(0);
                            boolean all = args.size() > 1 && "--all".equals(args.get(1));
                            if (all) {
                                var list = manager.getAll(player);
                                int success = 0, exist = 0;
                                for (var p : list) {
                                    var fake = manager.getByOwner(p);
                                    if (fake != null) {
                                        if (fake.getHandle().addTag(tag)) success++;
                                        else exist++;
                                    }
                                }
                                player.sendMessage(text("批量添加标签: " + tag + "，成功 " + success + " 个，已存在 " + exist + " 个", GREEN));
                            } else {
                                Fakeplayer fake = getSelfFakeplayer(player);
                                if (fake == null) return;
                                if (fake.getHandle().addTag(tag)) {
                                    player.sendMessage(text("已添加标签: " + tag, GREEN));
                                } else {
                                    player.sendMessage(text("标签已存在: " + tag, YELLOW));
                                }
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(new StringArgument("tag"))
                        .executesPlayer((player, args) -> {
                            Fakeplayer fake = getSelfFakeplayer(player);
                            if (fake == null) return;
                            String tag = (String) args.get(0);
                            if (fake.getHandle().removeTag(tag)) {
                                player.sendMessage(text("已移除标签: " + tag, GREEN));
                            } else {
                                player.sendMessage(text("标签不存在: " + tag, YELLOW));
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("list")
                        .executesPlayer((player, args) -> {
                            Fakeplayer fake = getSelfFakeplayer(player);
                            if (fake == null) return;
                            Set<String> tags = fake.getHandle().getTags();
                            if (tags.isEmpty()) {
                                player.sendMessage(text("当前没有标签", GRAY));
                            } else {
                                player.sendMessage(text("标签: " + String.join(", ", tags), AQUA));
                            }
                        })
                );
    }

    private Fakeplayer getSelfFakeplayer(@NotNull Player player) {
        Fakeplayer fake = manager.getByOwner(player);
        if (fake == null) {
            player.sendMessage(text("你没有可操作的假人", RED));
            return null;
        }
        return fake;
    }
}
