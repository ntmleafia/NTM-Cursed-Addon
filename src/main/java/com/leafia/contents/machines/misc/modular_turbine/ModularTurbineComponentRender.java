package com.leafia.contents.machines.misc.modular_turbine;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase.TurbineComponentType;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class ModularTurbineComponentRender extends TileEntitySpecialRenderer<ModularTurbineComponentTE> {
	static final String basePath = "machines/modular_turbines/";
	public static final WaveFrontObjectVAO mdl = getVAO(getIntegrated(basePath+"export.obj"));
	public static final ResourceLocation tex0 = getIntegrated(basePath+"texture0.png");
	public static final ResourceLocation tex0g = getIntegrated(basePath+"texture0_glass.png");;
	public static final ResourceLocation tex0s = getIntegrated(basePath+"texture0_smooth.png");
	public static final ResourceLocation tex0gs = getIntegrated(basePath+"texture0_glass_smooth.png");
	public static final ResourceLocation tex1 = getIntegrated(basePath+"texture1.png");
	public static final WaveFrontObjectVAO genMdl = getVAO(getIntegrated(basePath+"gen3x3.obj"));
	public static final ResourceLocation gen3x3tetx = getIntegrated(basePath+"generator3x3.png");
	public static final WaveFrontObjectVAO fwMdl = getVAO(getIntegrated(basePath+"flywheel.obj"));
	public static class ModularTurbineComponentItemRender extends LeafiaItemRenderer {
		/* what was i thinking
		static Map<Integer,ModularTurbineComponentItemRender> renderersBySize = new HashMap<>();
		@SideOnly(Side.CLIENT)
		public static void registerItemRenderer(Block block) {
			if (block instanceof ModularTurbineBlockBase turbine) {
				if (!renderersBySize.containsKey(turbine.shaftHeight()))
					renderersBySize.put(turbine.shaftHeight(),new ModularTurbineComponentItemRender());
				ItemRendererInit.register(block,renderersBySize.get(turbine.shaftHeight()));
			}
		}*/
		@Override
		protected double _sizeReference() {
			return 1.9;
		}
		@Override
		protected double _itemYoffset() {
			return 0.24;
		}
		@Override
		protected ResourceLocation __getTexture() {
			return null;
		}
		@Override
		protected WaveFrontObjectVAO __getModel() {
			return null;
		}
		public void renderItemTurbine(ModularTurbineBlockBase block) {
			bindTexture(tex1);
			switch(block.size()) {
				case 2: case 3:
					mdl.renderPart("Block2_3");
					break;
				case 5:
					mdl.renderPart("Block5");
					break;
				case 7:
					mdl.renderPart("Block7");
					break;
				case 9:
					mdl.renderPart("Block9");
					break;
			}
		}
		ModularTurbineComponentRender cloneRenderer = new ModularTurbineComponentRender();
		Map<Block,ModularTurbineComponentTE> dummyTEMap = new HashMap<>();
		public ModularTurbineComponentItemRender() {
			cloneRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
		}
		@Override
		public void renderCommon(ItemStack stack) {
			GL11.glScaled(0.5, 0.5, 0.5);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			if (stack.getItem() instanceof ItemBlock ib) {
				Block block = ib.getBlock();
				if (block instanceof ModularTurbineBlockBase turbine) {
					int shaft = turbine.shaftHeight();
					int size = shaft*2+1;
					LeafiaGls.scale(1d/size);
					LeafiaGls.translate(0,-size/2d,0);
					ModularTurbineComponentTE dummyTE = dummyTEMap.get(block);
					if (dummyTE == null) {
						dummyTE = new ModularTurbineComponentTE();
						dummyTEMap.put(block,dummyTE);
						dummyTE.setBlockType(block);
						dummyTE.blockMetadata = EnumFacing.NORTH.getIndex()+10;
					}
					cloneRenderer.render(dummyTE,0,0,0,0,0,1);
				}
			}
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
	}
	@Override
	public void render(ModularTurbineComponentTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		// CAUTION: WHEN USING te.world/te.getWorld(), DON'T FORGET NULL POINTER CHECK
		// BECAUSE THIS METHOD IS ALSO USED FOR ITEM RENDERING!
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		switch(te.getBlockMetadata()-10) {
			case 2 -> LeafiaGls.rotate(180,0F,1F,0F);
			case 3 -> LeafiaGls.rotate(0,0F,1F,0F);
			case 4 -> LeafiaGls.rotate(270,0F,1F,0F);
			case 5 -> LeafiaGls.rotate(90,0F,1F,0F);
		}
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		if (te.getBlockType() instanceof ModularTurbineBlockBase block) {
			int[] dimensions = block.getDimensions();
			int forwardSize = dimensions[2];
			int backwardSize = dimensions[3];
			int totalSize = 1+forwardSize+backwardSize;
			double totalSizeHalf = 1+forwardSize/2d+backwardSize/2d;
			EnumFacing face = EnumFacing.byIndex(te.getBlockMetadata()-10).getOpposite();
			boolean renderShaft = false;
			if (block.componentType() != null)
				renderShaft = true;
			boolean reverseShaft = face.equals(EnumFacing.SOUTH) || face.equals(EnumFacing.WEST);
			if (renderShaft) {
				LeafiaGls.pushMatrix();
				LeafiaGls.translate(0,block.shaftHeight()+0.5,0);
				if (te.core != null) {
					double spin = te.core.local$shaftAnglePrev+(te.core.local$shaftAngle-te.core.local$shaftAnglePrev)*partialTicks;
					if (!reverseShaft)
						spin *= -1;
					LeafiaGls.rotate((float)spin,0,0,1);
				}
				bindTexture(tex1);
				LeafiaGls.pushMatrix();
				LeafiaGls.translate(0,0,-(forwardSize-backwardSize)/2d);
				LeafiaGls.scale(1,1,totalSize+0.01/totalSizeHalf); // retard gaming
				mdl.renderPart("Shaft");
				LeafiaGls.popMatrix();
				if (block.componentType() == TurbineComponentType.FLYWHEEL) {
					float light = 0.539f;
					float dark = 0.195f;
					bindTexture(AddonBase.solid);
					switch(block.size()) {
						case 2: case 3:
							LeafiaGls.pushMatrix();
							LeafiaGls.translate(0,0,-0.25);
							LeafiaGls.color(light,light,light);
							fwMdl.renderPart("Flywheel"+block.size()+"x"+block.size());
							LeafiaGls.color(dark,dark,dark);
							fwMdl.renderPart("Flywheel"+block.size()+"x"+block.size()+".001");
							LeafiaGls.popMatrix();
							LeafiaGls.pushMatrix();
							LeafiaGls.translate(0,0,0.25);
							LeafiaGls.color(light,light,light);
							fwMdl.renderPart("Flywheel"+block.size()+"x"+block.size());
							LeafiaGls.color(dark,dark,dark);
							fwMdl.renderPart("Flywheel"+block.size()+"x"+block.size()+".001");
							LeafiaGls.popMatrix();
							break;
						case 5: case 7: case 9:
							LeafiaGls.color(light,light,light);
							fwMdl.renderPart("Flywheel"+block.size()+"x"+block.size());
							LeafiaGls.color(dark,dark,dark);
							fwMdl.renderPart("Flywheel"+block.size()+"x"+block.size()+".001");
							break;
					}
					LeafiaGls.color(1,1,1);
				} else if (block.componentType() == TurbineComponentType.BLADES) {
					if (te.hasWorld())
						te.local$checkForBlades(te.local$firstRender);
					te.local$firstRender = false;
					double diameter = block.size();
					ModularTurbineComponentTE curBlade = te;
					while (true) {
						if (curBlade.local$nextBlade != null) {
							curBlade = curBlade.local$nextBlade;
							diameter *= 0.9;
						} else break;
					}
					LeafiaGls.scale(diameter/3,diameter/3,2);
					LeafiaGls.disableCull();
					if (reverseShaft)
						LeafiaGls.scale(-1,1,1);
					LeafiaGls.pushMatrix();
					LeafiaGls.translate(0,0,-0.125);
					mdl.renderPart("Blades");
					LeafiaGls.popMatrix();
					LeafiaGls.pushMatrix();
					LeafiaGls.scale(Math.pow(0.9,0.5),Math.pow(0.9,0.5),1);
					LeafiaGls.rotate(7.5f,0,0,1);
					LeafiaGls.translate(0,0,0.125);
					mdl.renderPart("Blades");
					LeafiaGls.popMatrix();
					LeafiaGls.enableCull();
				}
				LeafiaGls.popMatrix();
			}
			if (block.componentType() == TurbineComponentType.BLADES) {
				String name = "Tube"+block.size();
				if (block.variant().equals("glass"))
					bindTexture(tex0g);
				else if (block.variant().equals("smooth"))
					bindTexture(tex0s);
				else if (block.variant().equals("glass_smooth"))
					bindTexture(tex0gs);
				else
					bindTexture(tex0);
				LeafiaGls.disableCull();
				mdl.renderPart(name+".001");
				LeafiaGls.enableCull();
				bindTexture(tex1);
				mdl.renderPart(name);
			} else if (block.componentType() == TurbineComponentType.FLYWHEEL) {
			} else if (block.componentType() == TurbineComponentType.GENERATOR) {
				switch(block.size()) {
					case 3 -> {
						bindTexture(gen3x3tetx);
						genMdl.renderAll();
					}
				}
			} else {
				bindTexture(tex1);
				switch(block.size()) {
					case 2: case 3:
						mdl.renderPart("Block2_3");
						break;
					case 5:
						mdl.renderPart("Block5");
						break;
					case 7:
						mdl.renderPart("Block7");
						break;
					case 9:
						mdl.renderPart("Block9");
						break;
				}
			}
		}
		LeafiaGls.shadeModel(GL11.GL_FLAT);
		LeafiaGls.popMatrix();
	}
}
