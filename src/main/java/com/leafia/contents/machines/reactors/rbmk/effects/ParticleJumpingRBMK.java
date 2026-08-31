package com.leafia.contents.machines.reactors.rbmk.effects;

import com.hbm.blocks.machine.rbmk.RBMKBase;
import com.hbm.render.NTMRenderHelper;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.dev.render.LeafiaBrush.BrushMode;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityRBMKBase;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleJumpingRBMK extends Particle {
	IBlockState state;
	IBlockState topState;
	TileEntityRBMKBase rbmk;
	BlockPos pos;
	BlockPos topPos;
	BlockPos abovePos;
	TileEntitySpecialRenderer<TileEntityRBMKBase> renderer;
	IMixinTileEntityRBMKBase mixin;
	boolean jumped = false;
	double lastJump = 0;
	double newJump = 0;
	public ParticleJumpingRBMK(World world,BlockPos rbmkPos) {
		super(world,rbmkPos.getX()+0.5,rbmkPos.getY()+RBMKDials.getColumnHeight(world)-1,rbmkPos.getZ()+0.5);
		pos = rbmkPos;
		state = world.getBlockState(rbmkPos);
		topPos = pos;
		while (true) {
			if (world.getBlockState(topPos.up()).getBlock() == state.getBlock())
				topPos = topPos.up();
			else
				break;
		}
		topState = world.getBlockState(topPos);
		abovePos = topPos.up();
		if (world.getTileEntity(rbmkPos) instanceof TileEntityRBMKBase te) {
			rbmk = te;
			mixin = (IMixinTileEntityRBMKBase)rbmk;
			if (TileEntityRendererDispatcher.instance.renderers.containsKey(te.getClass()))
				renderer = (TileEntitySpecialRenderer<TileEntityRBMKBase>)TileEntityRendererDispatcher.instance.renderers.get(te.getClass());
		} else
			setExpired();
		particleMaxAge = 20*3; // test
	}
	@Override
	public int getFXLayer() {
		return 3;
	}
	@Override
	public void onUpdate() {
		lastJump = newJump;
		newJump = mixin.leafia$jumpHeight();
		if (!jumped) {
			particleAge++;
			if (particleAge > particleMaxAge)
				setExpired(); // too long without jumping
		} else {
			if (lastJump <= 0 && newJump <= 0)
				setExpired();
		}
	}
	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ) {
		if (!(world.getBlockState(pos).getBlock() instanceof RBMKBase))
			setExpired();
		if (isExpired) return;
		double jump = lastJump+(newJump-lastJump)*partialTicks;
		if (jump > 0) jumped = true;
		NTMRenderHelper.resetParticleInterpPos(entityIn, partialTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		int height = topPos.getY()-pos.getY();
		LeafiaGls.pushMatrix();
		LeafiaGls._push();
		LeafiaGls.translate(
				pos.getX()-(entityIn.prevPosX+(entityIn.posX-entityIn.prevPosX)*partialTicks),
				pos.getY()-(entityIn.prevPosY+(entityIn.posY-entityIn.prevPosY)*partialTicks)+jump,
				pos.getZ()-(entityIn.prevPosZ+(entityIn.posZ-entityIn.prevPosZ)*partialTicks)
		);
		LeafiaGls.depthMask(true);
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
		{
			BlockRendererDispatcher blocker = Minecraft.getMinecraft().getBlockRendererDispatcher();
			BlockModelRenderer modeler = blocker.getBlockModelRenderer();
			IBakedModel baked = blocker.getModelForState(state);
			IBakedModel bakedTop = blocker.getModelForState(topState);
			LeafiaBrush brush = LeafiaBrush.instance;
			{
				// render block
				LeafiaGls.pushMatrix();
				LeafiaGls.translate(-abovePos.getX(),-abovePos.getY(),-abovePos.getZ());
				{
					brush.startDrawing(BrushMode.QUADS,DefaultVertexFormats.BLOCK);
					modeler.renderModel(world,baked,state,abovePos,brush.buf,true);
					brush.draw();
				}
				{
					LeafiaGls.translate(0,height,0);
					brush.startDrawing(BrushMode.QUADS,DefaultVertexFormats.BLOCK);
					modeler.renderModel(world,bakedTop,topState,abovePos,brush.buf,true);
					brush.draw();
				}
				LeafiaGls.popMatrix();
			}
			/*
			// vibecoding
			int light = world.getCombinedLight(abovePos,0);
			int lx = light & 0xFFFF;
			int ly = light >> 16;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,lx,ly);*/ // apparently the columns already do that
			RenderHelper.enableStandardItemLighting();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			if (renderer != null) // render te
				renderer.render(rbmk,0,0,0,partialTicks,-14,1);
			// negative destroyStage is a signal that it should bypass jumping rod check (it will not render otherwise)
		}
		LeafiaGls._pop();
		LeafiaGls.popMatrix();
	}
}
