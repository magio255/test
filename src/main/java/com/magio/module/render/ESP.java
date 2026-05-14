package com.magio.module.render;
import com.magio.module.Module;
import com.magio.setting.BooleanSetting;
public class ESP extends Module {
    public final BooleanSetting boxes = new BooleanSetting("Boxes", true);
    public final BooleanSetting tracers = new BooleanSetting("Tracers", false);
    public ESP() {
        super("ESP", "Draws entities", Category.RENDER);
        addSetting(boxes); addSetting(tracers);
    }
}
