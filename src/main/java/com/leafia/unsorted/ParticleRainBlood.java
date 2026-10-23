// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import com.hbm.render.NTMRenderHelper;
import com.leafia.AddonBase;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticleRainBlood extends Particle {
	private static final ResourceLocation texture = new ResourceLocation(AddonBase.MODID + ":textures/particle/leafia/bloodrain.png");

	public ParticleRainBlood(World worldIn,double xCoordIn,double yCoordIn,double zCoordIn) {
		super(worldIn,xCoordIn,yCoordIn,zCoordIn);
		//this.motionX *= 0.30000001192092896D;
		//this.motionY = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
		//this.motionZ *= 0.30000001192092896D;
		this.particleRed = 1.0F;
		this.particleGreen = 1.0F;
		this.particleBlue = 1.0F;
		this.setSize(0.01F, 0.01F);
		this.particleGravity = 0.06F;
		this.particleMaxAge = 3+worldIn.rand.nextInt(3);//(int)(8.0D / (Math.random() * 0.8D + 0.2D));
		//o = worldIn.rand.nextInt(4)/4d;
	}

	@Override
	public void onUpdate() {
		if (++particleAge >= particleMaxAge)
			setExpired();
	}
/*public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (double)this.particleGravity;
		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.particleMaxAge-- <= 0)
		{
			this.setExpired();
		}

		if (this.onGround)
		{
			if (Math.random() < 0.5D)
			{
				this.setExpired();
			}

			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}

		BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
		IBlockState iblockstate = this.world.getBlockState(blockpos);
		Material material = iblockstate.getMaterial();

		if (material.isLiquid() || material.isSolid())
		{
			double d0;

			if (iblockstate.getBlock() instanceof BlockLiquid)
			{
				d0 = (double)(1.0F - BlockLiquid.getLiquidHeightPercent(((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue()));
			}
			else
			{
				d0 = iblockstate.getBoundingBox(this.world, blockpos).maxY;
			}

			double d1 = (double) MathHelper.floor(this.posY) + d0;

			if (this.posY < d1)
			{
				this.setExpired();
			}
		}
	}*/

	@Override
	public int getFXLayer(){
		return 3;
	}

	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ){
		NTMRenderHelper.resetParticleInterpPos(entityIn, partialTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();

		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;

		GlStateManager.glNormal3f(0, 1, 0);
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		float scale = this.particleScale*0.1f;
		float pX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float pY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float pZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

		double o = Math.floorDiv(particleAge*4,particleMaxAge)/4d;

		buf.pos((double) (pX - rotationX * scale - rotationXY * scale), (double) (pY - rotationZ * scale), (double) (pZ - rotationYZ * scale - rotationXZ * scale)).tex(0.25+o, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buf.pos((double) (pX - rotationX * scale + rotationXY * scale), (double) (pY + rotationZ * scale), (double) (pZ - rotationYZ * scale + rotationXZ * scale)).tex(0.25+o, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buf.pos((double) (pX + rotationX * scale + rotationXY * scale), (double) (pY + rotationZ * scale), (double) (pZ + rotationYZ * scale + rotationXZ * scale)).tex(o, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buf.pos((double) (pX + rotationX * scale - rotationXY * scale), (double) (pY - rotationZ * scale), (double) (pZ + rotationYZ * scale - rotationXZ * scale)).tex(o, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		tes.draw();
	}
}
