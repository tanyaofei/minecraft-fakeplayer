package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Singleton
public class MoveCommand extends AbstractCommand {

    /**
     * 假人移动
     */
    public CommandExecutor move(float forward, float strafing) {
        return (sender, args) -> {
            var target = getTarget(sender, args);
            var handle = bridge.fromPlayer(target);
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
