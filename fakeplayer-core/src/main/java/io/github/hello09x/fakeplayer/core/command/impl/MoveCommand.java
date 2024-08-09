package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Singleton;
import dev.jorel.commandapi.executors.CommandExecutor;

@Singleton
public class MoveCommand extends AbstractCommand {

    /**
     * 假人移动
     */
    public CommandExecutor move(float forward, float strafing) {
        return (sender, args) -> {
            var fake = getFakeplayer(sender, args);
            var handle = bridge.fromPlayer(fake);
            float vel = fake.isSneaking() ? 0.3F : 1.0F;
            if (forward != 0.0F) {
                handle.setZza(vel * forward);
            }
            if (strafing != 0.0F) {
                handle.setXxa(vel * strafing);
            }
        };
    }


}
