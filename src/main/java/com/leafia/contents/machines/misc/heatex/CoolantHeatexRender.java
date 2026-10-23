// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.heatex;

import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.tileentity.IItemRendererProvider;
import com.leafia.contents.AddonBlocks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CoolantHeatexRender extends TileEntitySpecialRenderer<CoolantHeatexTE> implements IItemRendererProvider {
	public static final ResourceLocation tex = new ResourceLocation("leafia","textures/models/leafia/lftr/condenser.png");
	@Override
	public void render(CoolantHeatexTE heatex, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y, z + 0.5D);
		GlStateManager.enableLighting();
		GlStateManager.disableCull();
		
		switch(heatex.getBlockMetadata() - 10) {
			case 2: GlStateManager.rotate(90, 0F, 1F, 0F); break;
			case 4: GlStateManager.rotate(180, 0F, 1F, 0F); break;
			case 3: GlStateManager.rotate(270, 0F, 1F, 0F); break;
			case 5: GlStateManager.rotate(0, 0F, 1F, 0F); break;
		}

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
		bindTexture(tex);
		ResourceManager.condenser.renderPart("Condenser");

		float rot = heatex.lastSpin + (heatex.spin - heatex.lastSpin) * partialTicks;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0,1.5, 0);
		GlStateManager.rotate(rot, 1, 0, 0);
		GlStateManager.translate(0, -1.5, 0);
		ResourceManager.condenser.renderPart("Fan1");
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0,1.5, 0);
		GlStateManager.rotate(rot, -1, 0, 0);
		GlStateManager.translate(0, -1.5, 0);
		ResourceManager.condenser.renderPart("Fan2");
		GlStateManager.popMatrix();
		
		GlStateManager.shadeModel(GL11.GL_FLAT);
		
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	@Override
	public Item getItemForRenderer() {
		return Item.getItemFromBlock(AddonBlocks.coolant_heatex);
	}

	@Override
	public ItemRenderBase getRenderer(Item item) {
		return new ItemRenderBase() {

			public void renderInventory() {
				GlStateManager.translate(-1, -1, 0);
				GlStateManager.scale(2.75, 2.75, 2.75);
			}

			public void renderCommon() {
				GlStateManager.scale(0.75, 0.75, 0.75);
				GlStateManager.translate(0.5, 0, 0);
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
				NTMRenderHelper.bindTexture(tex);
				ResourceManager.condenser.renderAll();
				GlStateManager.shadeModel(GL11.GL_FLAT);
			}
		};
	}
}
