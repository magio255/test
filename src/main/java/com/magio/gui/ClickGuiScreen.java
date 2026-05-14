package com.magio.gui;
import com.magio.module.Module;
import com.magio.module.ModuleManager;
import com.magio.setting.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.*;

public class ClickGuiScreen extends Screen {
    private static final int W = 100, H = 20;
    private final Map<Module.Category, Boolean> expCat = new HashMap<>();
    private final Map<Module, Boolean> expMod = new HashMap<>();
    public ClickGuiScreen() {
        super(Text.of("Magio Client"));
        for (Module.Category c : Module.Category.values()) expCat.put(c, true);
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int x = 10;
        int accent = 0xFFEA427F;
        for (Module.Category c : Module.Category.values()) {
            int y = 10;
            context.fill(x, y, x + W, y + H, 0xBB000000);
            context.drawText(this.textRenderer, c.name(), x + 5, y + 5, accent, false);
            y += H;
            if (expCat.getOrDefault(c, false)) {
                for (Module m : ModuleManager.INSTANCE.getModulesByCategory(c)) {
                    int col = m.isEnabled() ? accent : 0xFFFFFFFF;
                    context.fill(x, y, x + W, y + H, 0x99000000);
                    context.drawText(this.textRenderer, m.getName(), x + 5, y + 5, col, false);
                    y += H;
                    if (expMod.getOrDefault(m, false)) {
                        for (Setting<?> s : m.getSettings()) {
                            context.fill(x, y, x + W, y + H, 0x77000000);
                            String t = s.getName() + ": " + s.getValue();
                            context.drawText(this.textRenderer, t, x + 10, y + 5, 0xFFAAAAAA, false);
                            y += H;
                        }
                    }
                }
            }
            x += W + 10;
        }
        super.render(context, mouseX, mouseY, delta);
    }
    @Override
    public boolean mouseClicked(double mx, double my, int b) {
        int x = 10;
        for (Module.Category c : Module.Category.values()) {
            int y = 10;
            if (is(mx, my, x, y, W, H)) { expCat.put(c, !expCat.getOrDefault(c, false)); return true; }
            y += H;
            if (expCat.getOrDefault(c, false)) {
                for (Module m : ModuleManager.INSTANCE.getModulesByCategory(c)) {
                    if (is(mx, my, x, y, W, H)) {
                        if (b == 0) m.toggle(); else expMod.put(m, !expMod.getOrDefault(m, false));
                        return true;
                    }
                    y += H;
                    if (expMod.getOrDefault(m, false)) {
                        for (Setting<?> s : m.getSettings()) {
                            if (is(mx, my, x, y, W, H)) { handle(s, b); return true; }
                            y += H;
                        }
                    }
                }
            }
            x += W + 10;
        }
        return super.mouseClicked(mx, my, b);
    }
    private void handle(Setting<?> s, int b) {
        if (s instanceof BooleanSetting bs) bs.setValue(!bs.getValue());
        else if (s instanceof ModeSetting ms) ms.next();
    }
    private boolean is(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
    @Override public boolean shouldPause() { return false; }
}
