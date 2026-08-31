package com.leafia.contents.machines.processing.solblaster;

import com.hbm.blocks.BlockDummyable;
import com.hbm.items.ModItems;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.util.RenderUtil;
import com.leafia.AddonBase;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class SolBlasterRender extends TileEntitySpecialRenderer<SolBlasterTE> {
	public static WaveFrontObjectVAO mdl = getVAO(getIntegrated("machines/crafting/solblaster/solblaster.obj"));
	public static ResourceLocation charge = getIntegrated("machines/crafting/solblaster/charge.png");
	public static ResourceLocation door = getIntegrated("machines/crafting/solblaster/door.png");
	public static ResourceLocation alt = getIntegrated("decoration/doors/reactordoor/reactordoor.png");
	public static ResourceLocation stain = getIntegrated("machines/crafting/solblaster/stain.png");
	public static ResourceLocation chem = new ResourceLocation("hbm","textures/models/machines/chemical_plant.png");
	public static ResourceLocation conc = new ResourceLocation("hbm","textures/blocks/brick_concrete.png");
	public static ResourceLocation ducr = new ResourceLocation("hbm","textures/blocks/ducrete_brick.png");
	public static class SolBlasterItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 7;
		}
		@Override
		protected double _itemYoffset() {
			return -0.15;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return null;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return null;
		}
		@Override
		public void renderCommon() {
			GL11.glScaled(0.5, 0.5, 0.5);bindTexture(chem);
			mdl.renderPart("Base");
			mdl.renderPart("Frame");
			bindTexture(door);
			mdl.renderPart("Door");
			bindTexture(alt);
			mdl.renderPart("DoorAlt");
			mdl.renderPart("Handle");
			bindTexture(conc);
			mdl.renderPart("Containment");
			bindTexture(ducr);
			mdl.renderPart("ContainmentInside");
			bindTexture(stain);
			boolean prevBlend = RenderUtil.isBlendEnabled();
			LeafiaGls.enableBlend();
			LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
			mdl.renderPart("Stain");
			if (!prevBlend)
				LeafiaGls.disableBlend();
			bindTexture(AddonBase.solid);
			LeafiaGls.color(0,0,0);
			mdl.renderPart("SolidBlack");
			LeafiaGls.color((float)Math.pow(0.104514,0.5),(float)Math.pow(0.110955f,0.5),(float)Math.pow(0.108075f,0.5));
			mdl.renderPart("Hemisphere");
			mdl.renderPart("Tower");
			LeafiaGls.color(1,1,1);
			bindTexture(charge);
			mdl.renderPart("Charge1");
			mdl.renderPart("Charge2");
			mdl.renderPart("Charge3");
			mdl.renderPart("Charge4");
		}
	}
	public boolean checkForItem(SolBlasterTE te,Item item,int slot) {
		return te.inventory.getStackInSlot(slot).getItem() == item;
	}
	@Override
	public void render(SolBlasterTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		boolean prevBlend = RenderUtil.isBlendEnabled();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		switch(te.getBlockMetadata() - BlockDummyable.offset) {
			case 2: LeafiaGls.rotate(180, 0F, 1F, 0F); break;
			case 4: LeafiaGls.rotate(270, 0F, 1F, 0F); break;
			case 3: LeafiaGls.rotate(0, 0F, 1F, 0F); break;
			case 5: LeafiaGls.rotate(90, 0F, 1F, 0F); break;
		}
		bindTexture(chem);
		mdl.renderPart("Base");
		mdl.renderPart("Frame");
		{
			float r = (te.prevDoorPos+(te.doorPos-te.prevDoorPos)*partialTicks)/(float)SolBlasterTE.doorDuration;
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(-0.625,1.125,1);
			LeafiaGls.rotate(-135*r,0,1,0);
			LeafiaGls.translate(0.625,-1.125,-1);
			bindTexture(door);
			mdl.renderPart("Door");
			bindTexture(alt);
			mdl.renderPart("DoorAlt");
			LeafiaGls.translate(0,1.688,0);
			LeafiaGls.rotate(1080*r,0,0,1);
			LeafiaGls.translate(0,-1.688,0);
			mdl.renderPart("Handle");
			LeafiaGls.popMatrix();
		}
		bindTexture(conc);
		mdl.renderPart("Containment");
		bindTexture(ducr);
		mdl.renderPart("ContainmentInside");
		bindTexture(AddonBase.solid);
		LeafiaGls.color(0,0,0);
		mdl.renderPart("SolidBlack");
		LeafiaGls.color((float)Math.pow(0.104514,0.5),(float)Math.pow(0.110955f,0.5),(float)Math.pow(0.108075f,0.5));
		mdl.renderPart("Hemisphere");
		mdl.renderPart("Tower");
		for (int i = 1; i <= 4; i++) {
			if (checkForItem(te,ModItems.early_explosive_lenses,i) || checkForItem(te,ModItems.explosive_lenses,i))
				mdl.renderPart("Cover"+i);
		}
		LeafiaGls.color(1,1,1);
		bindTexture(charge);
		for (int i = 1; i <= 4; i++) {
			if (checkForItem(te,ModItems.early_explosive_lenses,i) || checkForItem(te,ModItems.explosive_lenses,i))
				mdl.renderPart("Charge"+i);
		}
		bindTexture(stain);
		{ // keep this last to be rendered
			LeafiaGls.enableBlend();
			LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
			mdl.renderPart("Stain");
			if (!prevBlend)
				LeafiaGls.disableBlend();
		}
		LeafiaGls.popMatrix();
	}
}
