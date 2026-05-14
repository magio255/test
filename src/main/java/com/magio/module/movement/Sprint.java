package com.magio.module.movement;
import com.magio.module.Module;
public class Sprint extends Module {
    public Sprint() { super("Sprint", "Auto sprint", Category.MOVEMENT); }
    public void onTick() {
        if (isEnabled() && mc.player != null && mc.player.forwardSpeed > 0 && !mc.player.horizontalCollision)
            mc.player.setSprinting(true);
    }
}
