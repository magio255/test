package com.magio.module;
import java.util.*;
import java.util.stream.Collectors;
import com.magio.module.combat.*;
import com.magio.module.movement.*;
import com.magio.module.render.*;
import com.magio.module.player.*;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList<>();
    private ModuleManager() {
        register(new ESP());
        register(new Triggerbot());
        register(new Sprint());
        register(new Fullbright());
        register(new NoFall());
    }
    private void register(Module m) { modules.add(m); }
    public List<Module> getModules() { return modules; }
    public List<Module> getModulesByCategory(Module.Category c) {
        return modules.stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }
    public Module getModuleByName(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
