package io.github.hello09x.fakeplayer.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public enum InteractionHand {

    MAIN_HAND(EquipmentSlot.HAND),

    OFF_HAND(EquipmentSlot.OFF_HAND);

    final EquipmentSlot slot;

    public @NotNull EquipmentSlot asSlot() {
        return slot;
    }

}
