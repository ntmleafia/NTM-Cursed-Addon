package com.leafia.contents.machines.powercores.dfc.debris;

import com.hbm.main.ResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.powercores.dfc.debris.AbsorberShrapnelEntity.DebrisType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

public class AbsorberShrapnelRender extends Render<AbsorberShrapnelEntity> {

	public static final IRenderFactory<AbsorberShrapnelEntity> FACTORY = man -> new AbsorberShrapnelRender(man);

	static final WaveFrontObjectVAO core = new HFRWavefrontObject(
			new ResourceLocation("leafia", "models/leafia/dfc_receiver_shrapnels/core.obj")).asVBO();
	static final WaveFrontObjectVAO framebeam = new HFRWavefrontObject(
			new ResourceLocation("leafia", "models/leafia/dfc_receiver_shrapnels/framebeam.obj")).asVBO();
	static final WaveFrontObjectVAO framecable = new HFRWavefrontObject(
			new ResourceLocation("leafia", "models/leafia/dfc_receiver_shrapnels/framecable.obj")).asVBO();
	static final WaveFrontObjectVAO framecorner = new HFRWavefrontObject(
			new ResourceLocation("leafia", "models/leafia/dfc_receiver_shrapnels/framecorner.obj")).asVBO();
	static final WaveFrontObjectVAO framefront = new HFRWavefrontObject(
			new ResourceLocation("leafia", "models/leafia/dfc_receiver_shrapnels/framefront.obj")).asVBO();

	protected AbsorberShrapnelRender(RenderManager renderManager){
		super(renderManager);
	}

	@Override
	public void doRender(AbsorberShrapnelEntity entity,double x,double y,double z,float entityYaw,float partialTicks){
		GL11.glPushMatrix();
		GL11.glTranslated(x, y + 0.125D, z);

		AbsorberShrapnelEntity debris = (AbsorberShrapnelEntity)entity;

		GL11.glRotatef(debris.getEntityId() % 360, 0, 1, 0); //rotate based on entity ID to add unique randomness
		GL11.glRotatef(debris.lastRot + (debris.rot - debris.lastRot) * partialTicks, 1, 1, 1);
		
		DebrisType type = debris.getType();
		bindTexture(ResourceManager.dfc_receiver_tex);

		switch(type) {
			case CABLE: framecable.renderAll(); break;
			case CORE: GL11.glShadeModel(GL11.GL_SMOOTH); core.renderAll(); GL11.glShadeModel(GL11.GL_FLAT); break;
			case CORNER: framecorner.renderAll(); break;
			case FRONT: framefront.renderAll(); break;
			case BEAM: framebeam.renderAll(); break;
			default: break;
		}
		
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(AbsorberShrapnelEntity entity){
		return ResourceManager.dfc_receiver_tex;
	}

}
