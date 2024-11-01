package com.zen.the_fog.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zen.the_fog.common.gamerules.ModGamerules;
import com.zen.the_fog.common.other.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Unique
    private static final float NIGHT_FOG_END = 24f;

    @Unique
    private static float fogEnd = NIGHT_FOG_END;

    @Inject(method = "applyFog",at = @At(value = "TAIL"))
    private static void makeFogThiccer(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity entity = camera.getFocusedEntity();
        ClientWorld world = (ClientWorld) entity.getWorld();

        double fogDensityModifier = 1.0;

        if (client.player != null) {
            fogDensityModifier = client.player.the_fog_is_coming$getFogDensity();
        }

        if (world.getRegistryKey() != ClientWorld.OVERWORLD) {
            return;
        }

        world.calculateAmbientDarkness();

        float darkness = Math.round(world.getAmbientDarkness() / 11.0f);
        fogEnd = Util.lerp(fogEnd,Util.lerp(viewDistance,NIGHT_FOG_END,darkness),tickDelta * 0.01f);

        if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
            RenderSystem.setShaderFogStart(0f);
            RenderSystem.setShaderFogEnd((float) (fogEnd * fogDensityModifier));
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);
        }
    }
}
