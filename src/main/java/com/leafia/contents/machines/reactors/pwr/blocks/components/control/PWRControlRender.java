package com.leafia.contents.machines.reactors.pwr.blocks.components.control;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.AddonBlocks;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

public class PWRControlRender extends TileEntitySpecialRenderer<PWRControlTE> {

	public static final ResourceLocation controlSide = new ResourceLocation("leafia", "textures/blocks/pwr/pwr_control_side.png");
	public static final ResourceLocation controlTop = new ResourceLocation("leafia", "textures/blocks/pwr/pwr_control_top.png");
	public static final ResourceLocation oldSide = new ResourceLocation("leafia", "textures/blocks/reactor_control_side.png");
	public static final ResourceLocation oldTop = new ResourceLocation("leafia", "textures/blocks/reactor_control_top.png");
	public static final WaveFrontObjectVAO meshPWR = getVAO(new ResourceLocation("leafia", "models/leafia/pwr_control_final.obj"));
	public static final WaveFrontObjectVAO meshOld = getVAO(new ResourceLocation("leafia", "models/leafia/pwr_control_classic.obj"));
	@Override
	public boolean isGlobalRenderer(PWRControlTE entity) {
		return true;
	}

	public void setLight(World world,BlockPos pos) {
		int light = world.getCombinedLight(pos, 0);
		int lx = light & 0xFFFF;
		int ly = light >> 16;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lx, ly);
	}

	@Override
	public void render(PWRControlTE entity,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y, z + 0.5D);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);

		WaveFrontObjectVAO mesh = meshPWR;
		ResourceLocation side = controlSide;
		ResourceLocation top = controlTop;
		if (entity.getBlockType() == AddonBlocks.PWR.reactor_control) {
			mesh = meshOld;
			side = oldSide;
			top = oldTop;
		}

		//GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glPushMatrix();
		bindTexture(side);
		for (int i = 1; i <= entity.height; i++) {
			setLight(entity.getWorld(),entity.getPos().down(i-1));
			mesh.renderPart("FrameSide");
			GL11.glTranslated(0,-1,0);
		}
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		bindTexture(top);
		for (int i = 1; i <= entity.height; i++) {
			setLight(entity.getWorld(),entity.getPos().down(i-1));
			mesh.renderPart("FrameEnd");
			if (i == 1)
				mesh.renderPart("FrameEndTop");
			if (i == entity.height)
				mesh.renderPart("FrameEndBtm");
			GL11.glTranslated(0,-1,0);
		}
		GL11.glPopMatrix();

		GL11.glTranslated(0,entity.position*entity.height,0);
		GL11.glPushMatrix();
		bindTexture(side);
		for (int i = 1; i <= entity.height; i++) {
			setLight(entity.getWorld(),entity.getPos().down(i-1).up((int)(entity.position*entity.height)));
			mesh.renderPart("RodsSide");
			GL11.glTranslated(0,-1,0);
		}
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		for (int i = 1; i <= entity.height; i++) {
			setLight(entity.getWorld(),entity.getPos().down(i-1).up((int)(entity.position*entity.height)));
			if ((MathHelper.positiveModulo(entity.position*entity.height,1) != 0) || (entity.position*entity.height-i+1 > 0)) {
				if (side == oldSide)
					bindTexture(side);
				else
					bindTexture(top);
				mesh.renderPart("RodsEnd");
			}
			bindTexture(top);
			if (i == 1)
				mesh.renderPart("RodsEndTop");
			if (i == entity.height)
				mesh.renderPart("RodsEndBtm");
			GL11.glTranslated(0,-1,0);
		}
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glShadeModel(GL11.GL_FLAT);

		GL11.glPopMatrix();
	}
}