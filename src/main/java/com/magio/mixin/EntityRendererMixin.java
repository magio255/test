package com.magio.mixin;
import com.magio.module.ModuleManager;
import com.magio.module.render.ESP;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
        ESP esp = (ESP) ModuleManager.INSTANCE.getModuleByName("ESP");
        if (esp != null && esp.isEnabled()) { }
    }
}
