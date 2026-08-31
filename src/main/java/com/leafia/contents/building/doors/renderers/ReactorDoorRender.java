package com.leafia.contents.building.doors.renderers;

import com.hbm.interfaces.IDoor.DoorState;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.render.tileentity.door.IRenderDoors;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.leafia.contents.building.doors.AddonDoorDecl;
import com.leafia.contents.building.doors.special.reactor_door.ReactorDoorTE;
import com.leafia.dev.render.LeafiaItemRenderer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;
import java.util.List;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class ReactorDoorRender implements IRenderDoors {
	public ResourceLocation formatRsc(String s) {
		return new ResourceLocation(s.replaceFirst("(\\w+:)?(.*)","$1textures/$2.png"));
	}
	public ResourceLocation getRsc(Block block,int meta) {
		IBlockState display = block.getStateFromMeta(meta);
		IBakedModel baked = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(display);
		try {
			List<BakedQuad> quads = baked.getQuads(display,EnumFacing.NORTH,0);
			if (quads.size() > 0)
				return formatRsc(quads.get(0).getSprite().getIconName());
			else
				return formatRsc(baked.getParticleTexture().getIconName());
		} catch (IllegalArgumentException ignored) {} // FUCK YOUU
		return test;
	}
	public static final ReactorDoorRender INSTANCE = new ReactorDoorRender();
	public static final WaveFrontObjectVAO mdl = getVAO(getIntegrated("decoration/doors/reactordoor/reactordoorfinal.obj"));
	public static final ResourceLocation test = new ResourceLocation("hbm","textures/blocks/brick_concrete.png");
	public static class ReactorDoorItemRender extends LeafiaItemRenderer {
		@Override
		protected double _sizeReference() {
			return 5.6;
		}
		@Override
		protected double _itemYoffset() {
			return -0.1;
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
			GL11.glScaled(0.5, 0.5, 0.5);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			bindTexture(test);
			mdl.renderPart("FrameA");
			mdl.renderPart("FrameB");
			bindTexture(AddonDoorDecl.REACTOR_DOOR.getCyclingSkins());
			mdl.renderAllExcept("FrameA","FrameB");
			GlStateManager.shadeModel(GL11.GL_FLAT);;
		}
	}
	@Override
	public void render(TileEntityDoorGeneric te,DoubleBuffer bufForClipping) {
		LeafiaGls.rotate(90,0,1,0);
		LeafiaGls.translate(0,0,-0.5);
		ReactorDoorTE reac = (ReactorDoorTE)te;
		EnumFacing face = EnumFacing.byIndex(te.getBlockMetadata()-10);
		int lightA = te.getWorld().getCombinedLight(te.getPos().offset(face),0);
		int lxA = lightA & 0xFFFF;
		int lyA = lightA >> 16;
		int lightB = te.getWorld().getCombinedLight(te.getPos().offset(face,-2),0);
		int lxB = lightB & 0xFFFF;
		int lyB = lightB >> 16;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,lxA,lyA);
		Minecraft.getMinecraft().getTextureManager().bindTexture(getRsc(reac.skinA,reac.metaA));
		mdl.renderPart("FrameA");
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,lxB,lyB);
		Minecraft.getMinecraft().getTextureManager().bindTexture(getRsc(reac.skinB,reac.metaB));
		mdl.renderPart("FrameB");
		Minecraft.getMinecraft().getTextureManager().bindTexture(AddonDoorDecl.REACTOR_DOOR.getSkinFromIndex(te.getSkinIndex()));
		mdl.renderPart("Wall");

		double handleRatio = 0;
		double doorRatio = 0;
		if (te.state == DoorState.OPEN) {
			handleRatio = 1;
			doorRatio = 1;
		}
		if (te.currentAnimation != null) {
			handleRatio = IRenderDoors.getRelevantTransformation("HANDLE",te.currentAnimation)[1];
			doorRatio = IRenderDoors.getRelevantTransformation("DOOR",te.currentAnimation)[1];
		}

		LeafiaGls.translate(-0.625,0,0.5);
		LeafiaGls.rotate((float)(-140*doorRatio),0,1,0);
		LeafiaGls.translate(0.625,0,-0.5);
		mdl.renderPart("Door");

		LeafiaGls.translate(0,1.0625,0);
		LeafiaGls.rotate((float)(-90*handleRatio),0,0,1);
		LeafiaGls.translate(0,-1.0625,0);
		mdl.renderPart("Handle");
	}
}
