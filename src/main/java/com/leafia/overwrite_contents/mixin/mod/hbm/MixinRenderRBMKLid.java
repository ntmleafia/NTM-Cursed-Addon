// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.machine.rbmk.RBMKBase;
import com.hbm.items.ModItems;
import com.hbm.render.tileentity.RenderRBMKLid;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKRod;
import com.leafia.transformer.LeafiaGls;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/// this whole thing was murdered
@Mixin(value = RenderRBMKLid.class)
public abstract class MixinRenderRBMKLid {
	@Shadow(remap = false)
	protected abstract void renderCherenkovEffect(float r,float g,float b,float a,int height);

	@Redirect(method = "render(Lcom/hbm/tileentity/machine/rbmk/TileEntityRBMKRod;DDDFIF)V",at = @At(value = "INVOKE", target = "Lcom/hbm/render/tileentity/RenderRBMKLid;renderCherenkovEffect(FFFFI)V"),require = 1,remap = false)
	private void leaifa$onRender(RenderRBMKLid instance,float r,float g,float b,float a,int h,@Local(type = TileEntityRBMKRod.class) TileEntityRBMKRod te) {
		if (te instanceof TileEntityRBMKRod rod) {
			Item item = rod.inventory.getStackInSlot(0).getItem();
			if (item == ModItems.rbmk_fuel_leaus || item == ModItems.rbmk_fuel_heaus) {
				r = 1F;
				g = 0.9F;
				b = 0F;
			} else if (item == ModItems.rbmk_fuel_balefire_gold || item == ModItems.rbmk_fuel_flashlead) {
				r = 0.6F;
				g = 0F;
				b = 1F;
			} else if (item == ModItems.rbmk_fuel_balefire) {
				r = 0.25F;
				g = 1F;
				b = 0F;
			} else if (item == ModItems.rbmk_fuel_drx) {
				r = 1F;
				g = 0.25F;
				b = 0F;
			}
		}
		renderCherenkovEffect(r,g,b,a,h);
	}
	/*
	@Shadow(remap = false)
	protected abstract void renderColumnStack(TileEntityRBMKBase control,int offset);

	@Shadow(remap = false)
	protected abstract void renderLid(TileEntityRBMKBase control,int offset);

	@Shadow(remap = false)
	protected abstract void renderFuelRodStack(TileEntityRBMKBase control,float r,float g,float b,int offset);

	@Shadow(remap = false)
	protected abstract void renderCherenkovEffect(TileEntityRBMKBase control,float r,float g,float b,float a,int offset);
*/
	/*
	@Overwrite(remap = false)
	public void render(TileEntityRBMKBase control,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		boolean hasRod = false;
		boolean cherenkov = false;
		float fuelR = 0F;
		float fuelG = 0F;
		float fuelB = 0F;
		float cherenkovR = 0F;
		float cherenkovG = 0F;
		float cherenkovB = 0F;
		float cherenkovA = 0.1F;

		if(control instanceof TileEntityRBMKRod rod) {
			if(rod.hasRod) {
				hasRod = true;
				fuelR = rod.fuelR;
				fuelG = rod.fuelG;
				fuelB = rod.fuelB;
				cherenkovR = rod.cherenkovR;
				cherenkovG = rod.cherenkovG;
				cherenkovB = rod.cherenkovB;
			}
			if(rod.fluxQuantity > 5) {
				cherenkov = true;
				cherenkovA = (float) Math.max(0.25F, Math.log(rod.fluxQuantity) * 0.01F);
			}
		}
		int offset = 1;
		for (int o = 1; o < 16; o++){
			if (control.getWorld().getBlockState(control.getPos().up(o)).getBlock() == control.getBlockType()) {
				offset = o;
				int meta = control.getWorld().getBlockState(control.getPos().up(o)).getBlock().getMetaFromState(control.getWorld().getBlockState(control.getPos().up(o)));
				if (meta > 5 && meta < 12) break;
			} else break;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y, z + 0.5);

		if(!(control.getBlockType() instanceof RBMKBase block)) {
			GlStateManager.popMatrix();
			return;
		}

		Minecraft.getMinecraft().getTextureManager().bindTexture(block.columnTexture);
		LeafiaGls.pushMatrix();
		LeafiaGls.scale(1,1+control.jumpheight/offset,1);
		renderColumnStack(control, offset + 1);
		if(hasRod)
			renderFuelRodStack(control, fuelR, fuelG, fuelB, offset + 1);
		LeafiaGls.popMatrix();

		if(control.hasLid())
			renderLid(control, offset);
		if(cherenkov)
			renderCherenkovEffect(control, cherenkovR, cherenkovG, cherenkovB, cherenkovA, offset);

		GlStateManager.popMatrix();
	}*/
}
