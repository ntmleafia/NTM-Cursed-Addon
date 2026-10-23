// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.effects.folkvangr.visual;

import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.leafia.dev.render.RenderState;
import com.leafia.overwrite_contents.interfaces.IMixinEntityCloudFleija;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class LCERenderCloudRainbow extends Render<EntityCloudFleijaRainbow> {
    private static final float[] RADII = {0.5F, 0.6F, 0.6F + 0.1F,
            0.6F + 0.1F + 0.1F, 0.6F + 0.1F + 0.1F + 0.1F};
    public static final IRenderFactory<EntityCloudFleijaRainbow> FACTORY = LCERenderCloudRainbow::new;
    public float scale;
    public float ring;

    protected LCERenderCloudRainbow(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityCloudFleijaRainbow cloud, double x, double y, double z,
                         float yaw, float partialTicks) {
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(x, y, z);
                GlStateManager.disableLighting();
                GlStateManager.enableCull();
                GlStateManager.disableTexture2D();
                var size = (float) (cloud.scale + partialTicks * ((IMixinEntityCloudFleija) cloud).getTickrate());
                GlStateManager.scale(size, size, size);
                for (var index = 0; index < RADII.length; index++) {
                    if (index == 1) {
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                    }
                    GlStateManager.color(cloud.world.rand.nextInt(256) / 255F,
                            cloud.world.rand.nextInt(256) / 255F, cloud.world.rand.nextInt(256) / 255F);
                    var radius = RADII[index];
                    GlStateManager.scale(radius, radius, radius);
                    LCERenderCloudFleija.cloudFleija.renderAll();
                    GlStateManager.scale(1F / radius, 1F / radius, 1F / radius);
                }
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float yaw, float partialTicks) {}

    @Override
    protected ResourceLocation getEntityTexture(EntityCloudFleijaRainbow entity) {
        return null;
    }
}
