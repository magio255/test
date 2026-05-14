package com.magio.module.combat;
import com.magio.module.Module;
import com.magio.setting.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class Triggerbot extends Module {
    public final BooleanSetting weapon = new BooleanSetting("Only Weapon", true);
    public Triggerbot() {
        super("Triggerbot", "Auto attack", Category.COMBAT);
        addSetting(weapon);
    }
    public void onTick() {
        if (!isEnabled() || mc.player == null) return;
        if (weapon.getValue()) {
            Item i = mc.player.getMainHandStack().getItem();
            if (!(i instanceof SwordItem || i instanceof AxeItem)) return;
        }
        HitResult hr = mc.crosshairTarget;
        if (hr instanceof EntityHitResult ehr) {
            Entity e = ehr.getEntity();
            if (e instanceof LivingEntity le && le.isAlive() && mc.player.getAttackCooldownProgress(0) >= 1.0f) {
                mc.interactionManager.attackEntity(mc.player, e);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}
