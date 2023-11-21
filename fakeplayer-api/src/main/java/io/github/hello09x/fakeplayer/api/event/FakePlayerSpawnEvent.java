package io.github.hello09x.fakeplayer.api.event;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class FakePlayerSpawnEvent extends PlayerEvent implements Cancellable {

    private final static HandlerList HANDLERS = new HandlerList();

    @NotNull
    @Getter
    private final CommandSender creator;

    @NotNull
    @Getter
    private Result result = Result.ALLOW;

    @NotNull
    @Getter
    private Component reason = Component.empty();

    public FakePlayerSpawnEvent(@NotNull CommandSender creator, @NotNull Player who) {
        super(who);
        this.creator = creator;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.result != Result.ALLOW;
    }

    /**
     * @param cancel true if you wish to cancel this event
     * @deprecated 请使用 {@link #disallow(Component)} (Result, Component)}
     */
    @Override
    @Deprecated
    public void setCancelled(boolean cancel) {
        this.disallow(Component.empty());
    }

    public void allow() {
        this.result = Result.ALLOW;
        this.reason = Component.empty();
    }

    public void disallow(@NotNull Component reason) {
        this.result = Result.DISALLOW;
        this.reason = reason;
    }

    public enum Result {

        ALLOW,

        DISALLOW

    }


}
