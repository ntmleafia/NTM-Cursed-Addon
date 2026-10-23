// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.bomb.missile.customnuke.entity;

import com.hbm.render.NTMRenderHelper;
import com.hbm.util.RenderUtil;
import com.leafia.contents.bomb.missile.AddonMissileItemRender;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class CustomNukeMissileEntityRender extends Render<CustomNukeMissileEntity> {

	public static final IRenderFactory<CustomNukeMissileEntity> FACTORY = CustomNukeMissileEntityRender::new;

	protected CustomNukeMissileEntityRender(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(CustomNukeMissileEntity missile, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		boolean prevLighting = RenderUtil.isLightingEnabled();
		GlStateManager.enableLighting();
		double[] renderPos = NTMRenderHelper.getRenderPosFromMissile(missile, partialTicks);
		x = renderPos[0];
		y = renderPos[1];
		z = renderPos[2];
		GlStateManager.translate(x, y, z);
        GlStateManager.rotate(missile.prevRotationYaw + (missile.rotationYaw - missile.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(missile.prevRotationPitch + (missile.rotationPitch - missile.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);

        GlStateManager.disableCull();
        bindTexture(AddonMissileItemRender.neonc_tex);
        AddonMissileItemRender.neonc_mdl.renderAll();
        GlStateManager.enableCull();
		if (!prevLighting) GlStateManager.disableLighting();
		GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(CustomNukeMissileEntity entity) {
		return AddonMissileItemRender.neonc_tex;
	}
}
