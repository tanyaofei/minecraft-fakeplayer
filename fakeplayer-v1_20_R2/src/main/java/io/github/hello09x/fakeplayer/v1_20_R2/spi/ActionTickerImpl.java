package io.github.hello09x.fakeplayer.v1_20_R2.spi;


import io.github.hello09x.fakeplayer.api.action.ActionSetting;
import io.github.hello09x.fakeplayer.api.action.ActionType;
import io.github.hello09x.fakeplayer.api.action.BaseActionTicker;
import io.github.hello09x.fakeplayer.api.spi.ActionTicker;
import io.github.hello09x.fakeplayer.v1_20_R2.action.AttackAction;
import io.github.hello09x.fakeplayer.v1_20_R2.action.MineAction;
import io.github.hello09x.fakeplayer.v1_20_R2.action.UseAction;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionTickerImpl extends BaseActionTicker implements ActionTicker {

    public ActionTickerImpl(@NotNull Player player, @NotNull ActionType action, @NotNull ActionSetting setting) {
        super(player, action, setting);
        if (this.action == null) {
            this.action = switch (action) {
                case ATTACK -> new AttackAction(((CraftPlayer) player).getHandle());
                case MINE -> new MineAction(((CraftPlayer) player).getHandle());
                case USE -> new UseAction(((CraftPlayer) player).getHandle());
                case JUMP, LOOK_AT_NEAREST_ENTITY, DROP_INVENTORY, DROP_STACK, DROP_ITEM ->
                        throw new UnsupportedOperationException();
            };
        }
    }

}
