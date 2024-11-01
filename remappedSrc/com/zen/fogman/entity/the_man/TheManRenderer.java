package com.zen.the_fog.common.entity.the_man;

import com.zen.the_fog.ManFromTheFog;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class TheManRenderer extends GeoEntityRenderer<TheManEntity> {
    public TheManRenderer(EntityRendererFactory.Context context) {
        super(
                context,
                new DefaultedEntityGeoModel<>(new Identifier(ManFromTheFog.MOD_ID,"fogman"),false)
        );
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
