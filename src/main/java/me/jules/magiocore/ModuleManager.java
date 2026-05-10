package me.jules.magiocore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
    private final MagioCore plugin;
    private final File modulesFile;
    private FileConfiguration modulesConfig;
    private final Map<String, Boolean> moduleStates = new HashMap<>();

    public ModuleManager(MagioCore plugin) {
        this.plugin = plugin;
        this.modulesFile = new File(plugin.getDataFolder(), "modules.yml");
        loadModulesConfig();
    }

    public void loadModulesConfig() {
        if (!modulesFile.exists()) {
            plugin.saveResource("modules.yml", false);
        }
        modulesConfig = YamlConfiguration.loadConfiguration(modulesFile);

        // Default modules
        String[] modules = {"autorestart", "staffchat", "report", "socials", "keyall", "freeze", "antigrief", "deathsystem"};
        for (String module : modules) {
            if (!modulesConfig.contains(module)) {
                modulesConfig.set(module, true);
            }
            moduleStates.put(module, modulesConfig.getBoolean(module));
        }
        saveModulesConfig();
    }

    public void saveModulesConfig() {
        try {
            modulesConfig.save(modulesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnabled(String moduleName) {
        return moduleStates.getOrDefault(moduleName.toLowerCase(), false);
    }
}
