package com.leafia.contents.gear.combat.shields;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.contents.AddonItems.Shields;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AddonShieldRender extends TileEntityItemStackRenderer {
	private final TileEntityBanner banner = new TileEntityBanner();
	private final ModelShield modelShield = new ModelShield();
	private final ResourceLocation tex;
	private final BannerTextures.Cache shieldDesigns;

	public static WaveFrontObjectVAO mdlKms = getVAO(getIntegrated("items/shields/shield_kms.obj"));

	public AddonShieldRender(String id, ResourceLocation texture, ResourceLocation textureBlank){
		tex = texture;
		shieldDesigns = new BannerTextures.Cache(id, textureBlank, "textures/entity/shield/");
	}

	@Override
	public void renderByItem(ItemStack itemStackIn) {
		if (itemStackIn.getSubCompound("BlockEntityTag") != null) {
			this.banner.setItemValues(itemStackIn, true);
			Minecraft.getMinecraft().getTextureManager().bindTexture(shieldDesigns.getResourceLocation(this.banner.getPatternResourceLocation(), this.banner.getPatternList(), this.banner.getColorList()));
		} else {
			Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		}

		GlStateManager.pushMatrix();
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		if (itemStackIn.getItem() == Shields.mysticite_shield || itemStackIn.getItem() == Shields.fissite_shield)
			mdlKms.renderAll();
		else
			this.modelShield.render();
		GlStateManager.popMatrix();
	}
}
