package com.magio.client;
import com.magio.gui.ClickGuiScreen;
import com.magio.module.ModuleManager;
import com.magio.module.combat.Triggerbot;
import com.magio.module.movement.Sprint;
import com.magio.module.player.NoFall;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MagioClientClient implements ClientModInitializer {
    private static KeyBinding guiBind;
    @Override
    public void onInitializeClient() {
        guiBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.magio.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.magio"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (guiBind.wasPressed()) client.setScreen(new ClickGuiScreen());
            Triggerbot t = (Triggerbot) ModuleManager.INSTANCE.getModuleByName("Triggerbot");
            if (t != null) t.onTick();
            Sprint s = (Sprint) ModuleManager.INSTANCE.getModuleByName("Sprint");
            if (s != null) s.onTick();
            NoFall nf = (NoFall) ModuleManager.INSTANCE.getModuleByName("NoFall");
            if (nf != null) nf.onTick();
        });
    }
}
