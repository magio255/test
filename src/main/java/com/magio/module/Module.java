package com.magio.module;
import net.minecraft.client.MinecraftClient;
import java.util.ArrayList;
import java.util.List;
import com.magio.setting.Setting;
import com.magio.setting.KeySetting;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    private final String name, description;
    private final Category category;
    private final KeySetting keySetting = new KeySetting("Keybind", 0);
    private boolean enabled;
    private final List<Setting> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name; this.description = description; this.category = category;
        settings.add(keySetting);
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
    }
    public void toggle() { setEnabled(!enabled); }
    protected void onEnable() {}
    protected void onDisable() {}
    public void addSetting(Setting s) { settings.add(s); }
    public List<Setting> getSettings() { return settings; }
    public int getKey() { return keySetting.getValue(); }
    public void setKey(int key) { keySetting.setValue(key); }

    public enum Category { COMBAT, MOVEMENT, RENDER, PLAYER, MISC }
}
