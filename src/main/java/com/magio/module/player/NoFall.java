package com.magio.module.player;
import com.magio.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
public class NoFall extends Module {
    public NoFall() { super("NoFall", "Prevents fall damage", Category.PLAYER); }
    public void onTick() {
        if (isEnabled() && mc.player != null && mc.player.fallDistance > 2.0f) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision));
        }
    }
}
