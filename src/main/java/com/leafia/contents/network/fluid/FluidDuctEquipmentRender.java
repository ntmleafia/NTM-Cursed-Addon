package com.leafia.contents.network.fluid;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.main.ResourceManager;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.network.fluid.gauges.FluidDuctGaugeTE;
import com.leafia.contents.network.fluid.valves.FluidDuctValveTE;
import com.leafia.dev.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

public class FluidDuctEquipmentRender extends TileEntitySpecialRenderer<FluidDuctEquipmentTE> {
	public static final ResourceLocation ntmf = new ResourceLocation("hbm", "textures/blocks/pipe_silver.png");
	static WaveFrontObjectVAO mdlGauge = getVAO(new ResourceLocation("leafia:models/leafia/pipes/gauge.obj"));
	static WaveFrontObjectVAO mdlValve = getVAO(new ResourceLocation("leafia:models/leafia/pipes/valve.obj"));
	static WaveFrontObjectVAO mdlValveRS = getVAO(new ResourceLocation("leafia:models/leafia/pipes/valve_rs.obj"));
	static ResourceLocation texGauge = new ResourceLocation("leafia:textures/models/leafia/pipes/gauge.png");
	static ResourceLocation texValve = new ResourceLocation("leafia:textures/models/leafia/pipes/valve.png");
	public static class FluidDuctEquipmentItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 1.5;
		}
		@Override
		protected double _itemYoffset() {
			return 0.175;
		}
		@Override protected ResourceLocation __getTexture() { return null; }
		@Override protected WaveFrontObjectVAO __getModel() { return null; }
		@Override
		public void renderCommon(ItemStack stack) {
			GL11.glScaled(0.5, 0.5, 0.5);
			if (stack.getItem() instanceof ItemBlock ib) {
				WaveFrontObjectVAO mdl = mdlGauge;
				ResourceLocation tex = texGauge;
				if (ib.getBlock().equals(AddonBlocks.fluid_duct_valve_mdl)) {
					mdl = mdlValve;
					tex = texValve;
				} else if (ib.getBlock().equals(AddonBlocks.fluid_duct_valve_mdl_rs)) {
					mdl = mdlValveRS;
					tex = texValve;
				}
				bindTexture(ntmf);
				mdl.renderPart("pipe");
				LeafiaGls.color(1,1,1);
				bindTexture(tex);
				mdl.renderPart("body");
				if (ib.getBlock().equals(AddonBlocks.fluid_duct_gauge_mdl))
					mdl.renderPart("needle");
				if (ib.getBlock().equals(AddonBlocks.fluid_duct_valve_mdl))
					mdl.renderPart("valve");
			}
		}
	}
	void setColorFromFluid(FluidType fluid) {
		int code = fluid.getColor();
		float max = 240/255f;
		float red = (code>>>16&0xFF)/255f;
		float green = (code>>>8&0xFF)/255f;
		float blue = (code&0xFF)/255f;
		LeafiaGls.color(red*max,green*max,blue*max);
	}
	@Override
	public void render(FluidDuctEquipmentTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y+0.5,z+0.5);
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if (state.getBlock() instanceof FluidDuctEquipmentBase) {
			WaveFrontObjectVAO mdl = mdlGauge;
			ResourceLocation tex = texGauge;
			if (state.getBlock().equals(AddonBlocks.fluid_duct_valve_mdl)) {
				mdl = mdlValve;
				tex = texValve;
			} else if (state.getBlock().equals(AddonBlocks.fluid_duct_valve_mdl_rs)) {
				mdl = mdlValveRS;
				tex = texValve;
			}
			LeafiaGls.rotate(180-te.direction.getHorizontalIndex()*90,0,1,0);
			LeafiaGls.rotate(-90*te.face,1,0,0);
			if (te.vertical)
				LeafiaGls.rotate(-90,0,0,1);
			bindTexture(ntmf);
			if(te.getType() != null)
				setColorFromFluid(te.getType());
			mdl.renderPart("pipe");
			LeafiaGls.color(1,1,1);
			bindTexture(tex);
			mdl.renderPart("body");
			if (te instanceof FluidDuctGaugeTE gauge) {
				LeafiaGls.rotate(-gauge.local_fillPerSec/(float)gauge.maximum*360,0,0,1);
				mdl.renderPart("needle");
			}
			if (te instanceof FluidDuctValveTE valve && mdl != mdlValveRS) {
				float addition = (valve.local_angle-valve.local_angle_last)*partialTicks;
				float finalval = valve.local_angle_last+addition;
				LeafiaGls.rotate((10-finalval)*9,0,0,1);
				mdl.renderPart("valve");
			}
		}
		LeafiaGls.popMatrix();
	}
}
