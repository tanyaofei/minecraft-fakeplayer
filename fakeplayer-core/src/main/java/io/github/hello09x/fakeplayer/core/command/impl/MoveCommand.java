package io.github.hello09x.fakeplayer.core.command.impl;

import dev.jorel.commandapi.executors.CommandExecutor;
import io.github.hello09x.fakeplayer.core.Main;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoveCommand extends AbstractCommand {

    public final static MoveCommand instance = new MoveCommand();

    /**
     * 假人移动
     */
    public CommandExecutor move(float forward, float strafing) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            var handle = Main.getBridge().player(target);
            float vel = target.isSneaking() ? 0.3F : 1.0F;
            if (forward != 0.0F) {
                handle.setZza(vel * forward);
            }
            if (strafing != 0.0F) {
                handle.setXxa(vel * strafing);
            }
        };
    }


}
