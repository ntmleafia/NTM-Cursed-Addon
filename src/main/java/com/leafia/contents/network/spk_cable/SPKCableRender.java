package com.leafia.contents.network.spk_cable;

import com.custom_hbm.render.misc.LCEBeamPronter;
import com.custom_hbm.render.misc.LCEBeamPronter.EnumBeamType;
import com.custom_hbm.render.misc.LCEBeamPronter.EnumWaveType;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.network.spk_cable.SPKCableTE.EffectLink;
import com.leafia.contents.network.spk_cable.uninos.ISPKConnector;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class SPKCableRender extends TileEntitySpecialRenderer<SPKCableTE> {
	public static final WaveFrontObjectVAO mdl = new HFRWavefrontObject(new ResourceLocation("leafia", "models/leafia/cable_spk.obj")).asVBO();
	public static final ResourceLocation tex = new ResourceLocation("leafia", "textures/blocks/leafia/cable_spk.png");

	@Override
	public void render(SPKCableTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		if (te.getBlockType() != AddonBlocks.spk_cable) return;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		bindTexture(tex);

		boolean pX = te.getWorld().getTileEntity(te.getPos().add(1, 0, 0)) instanceof ISPKConnector;
		boolean nX = te.getWorld().getTileEntity(te.getPos().add(-1, 0, 0)) instanceof ISPKConnector;
		boolean pY = te.getWorld().getTileEntity(te.getPos().add(0, 1, 0)) instanceof ISPKConnector;
		boolean nY = te.getWorld().getTileEntity(te.getPos().add(0, -1, 0)) instanceof ISPKConnector;
		boolean pZ = te.getWorld().getTileEntity(te.getPos().add(0, 0, 1)) instanceof ISPKConnector;
		boolean nZ = te.getWorld().getTileEntity(te.getPos().add(0, 0, -1)) instanceof ISPKConnector;
		te.pX = pX;
		te.pY = pY;
		te.pZ = pZ;
		te.nX = nX;
		te.nY = nY;
		te.nZ = nZ;
		te.isCorner = false;

		if(pX && nX && !pY && !nY && !pZ && !nZ)
			mdl.renderPart("CX");
		else if(!pX && !nX && pY && nY && !pZ && !nZ)
			mdl.renderPart("CY");
		else if(!pX && !nX && !pY && !nY && pZ && nZ)
			mdl.renderPart("CZ");
		else{
			te.isCorner = true;
			mdl.renderPart("Core");
			if(pX) mdl.renderPart("posX");
			if(nX) mdl.renderPart("negX");
			if(pY) mdl.renderPart("posY");
			if(nY) mdl.renderPart("negY");
			if(pZ) mdl.renderPart("negZ");
			if(nZ) mdl.renderPart("posZ");
            //mlbv: i really don't know how i am supposed to port this
			for (EffectLink link : te.links) {
				if (link.link == null || !link.emit) continue;
				double distance = Math.sqrt(te.getPos().distanceSq(link.link));
				Vec3d look = new Vec3d(link.direction.getDirectionVec());
				LeafiaGls.inLocalSpace(()->{
					double offset = 3/16d;
					LeafiaGls.translate(look.scale(offset));
					LCEBeamPronter.prontBeam(
							look.scale(distance-offset*(link.nonCable ? 1 : 2)),
							EnumWaveType.RANDOM,EnumBeamType.SOLID,
							0x64001e,0x9A9A9A,
							(int)(getWorld().getTotalWorldTime()%1000),
							(int)(distance*5),1/16f,
							2,0.666f/16f
					);
				});
			}
		}

		GL11.glTranslated(-x - 0.5F, -y - 0.5F, -z - 0.5F);
		GL11.glPopMatrix();
	}
}
