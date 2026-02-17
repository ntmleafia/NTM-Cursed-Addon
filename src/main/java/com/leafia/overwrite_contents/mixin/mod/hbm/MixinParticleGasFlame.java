package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.particle.ParticleGasFlame;
import com.hbm.util.RenderUtil;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ParticleGasFlame.class)
public class MixinParticleGasFlame extends ParticleSmokeNormal {
	protected MixinParticleGasFlame(World worldIn,double xCoordIn,double yCoordIn,double zCoordIn,double p_i46348_8_,double p_i46348_10_,double p_i46348_12_,float p_i46348_14_) {
		super(worldIn,xCoordIn,yCoordIn,zCoordIn,p_i46348_8_,p_i46348_10_,p_i46348_12_,p_i46348_14_);
	}
	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ) {
		Tessellator tes = Tessellator.getInstance();
		tes.draw();
		boolean prevBlend = RenderUtil.isBlendEnabled();
		LeafiaGls.enableBlend();
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE);
		LeafiaGls.alphaFunc(GL11.GL_GREATER, 0);
		buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		super.renderParticle(buffer,entityIn,partialTicks,rotationX,rotationZ,rotationYZ,rotationXY,rotationXZ);
		tes.draw();
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
		if (!prevBlend)
			LeafiaGls.disableBlend();
		buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}
}
