package com.leafia.contents.machines.powercores.dfc.render;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.custom_hbm.render.misc.LCEBeamPronter;
import com.custom_hbm.render.misc.LCEBeamPronter.EnumBeamType;
import com.custom_hbm.render.misc.LCEBeamPronter.EnumWaveType;
import com.hbm.render.tileentity.IItemRendererProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityCoreEmitter;
import com.hbm.tileentity.machine.TileEntityCoreInjector;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.hbm.tileentity.machine.TileEntityCoreStabilizer;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import com.leafia.contents.machines.powercores.dfc.components.cemitter.CoreCEmitterTE;
import com.leafia.contents.machines.powercores.dfc.components.exchanger.CoreExchangerTE;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreEmitter;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreReceiver;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreStabilizer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL11;

import static com.leafia.init.ResourceInit.getVAO;

public class DFCComponentRender extends TileEntitySpecialRenderer<TileEntityMachineBase> implements IItemRendererProvider {
	public Item getItemForRenderer() {
		return null;
	}

	public Item[] getItemsForRenderer() {
		return new Item[]{
				Item.getItemFromBlock(ModBlocks.dfc_emitter),
				Item.getItemFromBlock(ModBlocks.dfc_receiver),
				Item.getItemFromBlock(ModBlocks.dfc_injector),
				Item.getItemFromBlock(ModBlocks.dfc_stabilizer),
				Item.getItemFromBlock(AddonBlocks.dfc_cemitter),
				Item.getItemFromBlock(AddonBlocks.dfc_reinforced),
				Item.getItemFromBlock(AddonBlocks.dfc_exchanger),
		};
	}

	public ItemRenderBase getRenderer(Item item) {
		return new ItemRenderBase() {
			public void renderInventory() {
				GlStateManager.translate((double)0.0F, (double)-2.5F, (double)0.0F);
				double scale = (double)5.0F;
				GlStateManager.scale(scale, scale, scale);
			}

			public void renderCommon(ItemStack item) {
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0,0.5,0);
				GlStateManager.disableCull();
				WaveFrontObjectVAO mdl;
				if (item.getItem() == Item.getItemFromBlock(ModBlocks.dfc_emitter)) {
					NTMRenderHelper.bindTexture(dfc_booster_tex);
					mdl = dfc_booster_mdl;
				} else if (item.getItem() == Item.getItemFromBlock(ModBlocks.dfc_receiver)) {
					NTMRenderHelper.bindTexture(dfc_absorber_tex);
					mdl = dfc_absorber_mdl;
				} else if (item.getItem() == Item.getItemFromBlock(ModBlocks.dfc_stabilizer)) {
					NTMRenderHelper.bindTexture(dfc_stabilizer_tex);
					mdl = dfc_stabilizer_mdl;
				} else if (item.getItem() == Item.getItemFromBlock(ModBlocks.dfc_injector)) {
					NTMRenderHelper.bindTexture(dfc_injector_tex);
					mdl = dfc_injector_mdl;
				} else if (item.getItem() == Item.getItemFromBlock(AddonBlocks.dfc_cemitter)) {
					NTMRenderHelper.bindTexture(dfc_cemitter_tex);
					mdl = dfc_booster_mdl;
				} else if (item.getItem() == Item.getItemFromBlock(AddonBlocks.dfc_exchanger)) {
					NTMRenderHelper.bindTexture(dfc_exchanger_tex);
					mdl = dfc_exchanger_mdl;
				} else if (item.getItem() == Item.getItemFromBlock(AddonBlocks.dfc_reinforced)) {
					NTMRenderHelper.bindTexture(dfc_absorber_tex);
					mdl = dfc_reinforced_mdl;
				} else return;
				mdl.renderPart("Core");
				if (mdl == dfc_reinforced_mdl)
					mdl.renderPart("Fan");
				if (mdl == dfc_stabilizer_mdl) {
					LeafiaGls.enableBlend();
					LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.enableLighting();
					GL11.glAlphaFunc(GL11.GL_ALWAYS, 0);
					mdl.renderPart("Glass");
					GL11.glAlphaFunc(GL11.GL_GREATER, 0);
					LeafiaGls.disableBlend();
				}
				GlStateManager.pushMatrix();
				for (int i = 0; i < 4; i++) {
					mdl.renderPart("Arm");
					GlStateManager.rotate(90,0,0,1);
				}
				GlStateManager.popMatrix();
				mdl.renderPart("Frame");
				GlStateManager.rotate(90,0,1,0);
				mdl.renderPart("Frame");
				GlStateManager.rotate(-180,0,1,0);
				mdl.renderPart("Frame");
				GlStateManager.rotate(90,0,1,0);
				GlStateManager.rotate(90,1,0,0);
				mdl.renderPart("Frame");
				GlStateManager.rotate(-180,1,0,0);
				mdl.renderPart("Frame");
				GlStateManager.enableCull();
			}
		};
	}
	@Override
	public boolean isGlobalRenderer(TileEntityMachineBase te) {
		return true;
	}

	boolean isFace(EnumFacing face, Vec3d direction) {
		double component = 0;
		if (face.getAxis() == Axis.X) component = direction.x;
		else if (face.getAxis() == Axis.Y) component = direction.y;
		else if (face.getAxis() == Axis.Z) component = direction.z;
		if (face.getAxisDirection() == AxisDirection.NEGATIVE) component *= -1;
		return component > 0.15;
	}

	static final ResourceLocation dfc_cemitter_tex = new ResourceLocation("leafia", "textures/models/leafia/dfc/core_cemitter.png");
	static final ResourceLocation dfc_booster_tex = new ResourceLocation("leafia", "textures/models/leafia/dfc/core_emitter.png");
	static final WaveFrontObjectVAO dfc_booster_mdl =
			getVAO(new ResourceLocation("leafia","models/leafia/dfc_rotatable/booster.obj"));

	static final ResourceLocation dfc_absorber_tex = new ResourceLocation("leafia", "textures/models/leafia/dfc/core_receiver.png");
	static final WaveFrontObjectVAO dfc_absorber_mdl =
			getVAO(new ResourceLocation("leafia","models/leafia/dfc_rotatable/absorber.obj"));

	static final ResourceLocation dfc_injector_tex = new ResourceLocation("leafia", "textures/models/leafia/dfc/core_injector.png");
	static final WaveFrontObjectVAO dfc_injector_mdl =
			getVAO(new ResourceLocation("leafia","models/leafia/dfc_rotatable/injector.obj"));

	static final ResourceLocation dfc_stabilizer_tex = new ResourceLocation("leafia", "textures/models/leafia/dfc/test_texture64.png");
	static final WaveFrontObjectVAO dfc_stabilizer_mdl =
			getVAO(new ResourceLocation("leafia","models/leafia/dfc_rotatable/stabilizer.obj"));

	static final WaveFrontObjectVAO dfc_reinforced_mdl =
			getVAO(new ResourceLocation("leafia","models/leafia/dfc_rotatable/reinforced.obj"));

	static final ResourceLocation dfc_exchanger_tex = new ResourceLocation("leafia", "textures/models/leafia/dfc/core_exchanger.png");
	static final WaveFrontObjectVAO dfc_exchanger_mdl =
			getVAO(new ResourceLocation("leafia","models/leafia/dfc_rotatable/exchanger.obj"));

	@Override
	public void render(TileEntityMachineBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (!(te instanceof IDFCBase)) return;
		WaveFrontObjectVAO mdl;
		ResourceLocation tex = null;
		if (te instanceof CoreCEmitterTE) {
			tex = dfc_cemitter_tex;
			mdl = dfc_booster_mdl;
		} else if (te instanceof TileEntityCoreEmitter) {
			tex = dfc_booster_tex;
			mdl = dfc_booster_mdl;
		} else if (te instanceof TileEntityCoreReceiver) {
			tex = dfc_absorber_tex;
			if (getWorld().getBlockState(te.getPos()).getBlock() == AddonBlocks.dfc_reinforced)
				mdl = dfc_reinforced_mdl;
			else
				mdl = dfc_absorber_mdl;
		} else if (te instanceof TileEntityCoreInjector) {
			tex = dfc_injector_tex;
			mdl = dfc_injector_mdl;
		} else if (te instanceof TileEntityCoreStabilizer) {
			tex = dfc_stabilizer_tex;
			mdl = dfc_stabilizer_mdl;
		} else if (te instanceof CoreExchangerTE) {
			tex = dfc_exchanger_tex;
			mdl = dfc_exchanger_mdl;
		} else return;
		bindTexture(tex);
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GL11.glPushMatrix();
		GlStateManager.enableLighting();
		GlStateManager.disableCull();

		int light = te.getWorld().getCombinedLight(te.getPos(), 0);
		int lx = light & 0xFFFF;
		int ly = light >> 16;

		//GL11.glRotatef(90, 0F, 1F, 0F); What is this for bruh
		IDFCBase base = (IDFCBase)te;
		Vec3i relative = base.getTargetPosition().subtract(te.getPos());
		Vec3d unit = new Vec3d(relative).normalize();
		double yaw = Math.toDegrees(Math.atan2(-relative.getX(), -relative.getZ()));
		double pitch = Math.toDegrees(Math.atan2(relative.getY(), Math.sqrt(relative.getX() * relative.getX() + relative.getZ() * relative.getZ())));
		GL11.glRotated(yaw, 0, 1, 0);
		GL11.glRotated(pitch, 1, 0, 0);
		mdl.renderPart("Core");
		if (mdl == dfc_reinforced_mdl) {
			LeafiaGls.pushMatrix();
			assert te instanceof TileEntityCoreReceiver;
			LeafiaGls.rotate(-((IMixinTileEntityCoreReceiver)te).fanAngle()-partialTicks*720,0,0,1);
			mdl.renderPart("Fan");
			LeafiaGls.popMatrix();
		}
        /*
		switch(tileEntity.getBlockMetadata()) {
		case 0:
	        GL11.glTranslated(0.0D, 0.5D, -0.5D);
			GL11.glRotatef(90, 1F, 0F, 0F); break;
		case 1:
	        GL11.glTranslated(0.0D, 0.5D, 0.5D);
			GL11.glRotatef(90, -1F, 0F, 0F); break;
		case 2:
			GL11.glRotatef(0, 0F, 1F, 0F); break;
		case 4:
			GL11.glRotatef(90, 0F, 1F, 0F); break;
		case 3:
			GL11.glRotatef(180, 0F, 1F, 0F); break;
		case 5:
			GL11.glRotatef(-90, 0F, 1F, 0F); break;
		}*/

		//GL11.glTranslated(0.0D, 0D, 0.0D);

		//GL11.glTranslated(0, 0.5, 0);

		double range = 0;
		if (base.lastGetCore() != null)
			range = new Vec3d(te.getPos()).add(0.5, 0.5, 0.5).distanceTo(new Vec3d(base.lastGetCore().getPos()).add(0.5, 0.5, 0.5));
		if (te instanceof TileEntityCoreStabilizer) {
			TileEntityCoreStabilizer stabilizer = (TileEntityCoreStabilizer) te;
			IMixinTileEntityCoreStabilizer mixin = (IMixinTileEntityCoreStabilizer)te;
			int outerColor = mixin.getLens().outerColor;
			int innerColor = mixin.getLens().innerColor;
			if (mixin.hasLens()) {
				LeafiaGls.enableBlend();
				IBakedModel baked = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(new ItemStack(mixin.getLens().item), getWorld(), null);
				bindByIconName(baked.getParticleTexture().getIconName());
				LeafiaGls.blendFunc(SourceFactor.ONE, DestFactor.SRC_COLOR);
				mdl.renderPart("lens");
				bindTexture(dfc_stabilizer_tex);
				LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				LeafiaGls.disableBlend();
				if (range > 0 && stabilizer.isOn) {
					bindTexture(AddonBase.solid_e);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
					LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.STRAIGHT, EnumBeamType.SOLID, outerColor, innerColor, 0, 1, 0F, 2, 0.125F);
					LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.SPIRAL, EnumBeamType.SOLID, outerColor, innerColor, (int) te.getWorld().getTotalWorldTime() * -8 % 360, (int) Math.round(range * 3), 0.125F, 2, 0.04F);
					LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.SPIRAL, EnumBeamType.SOLID, outerColor, innerColor, (int) te.getWorld().getTotalWorldTime() * -8 % 360 + 180, (int) Math.round(range * 3), 0.125F, 2, 0.04F);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lx, ly);
					bindTexture(tex);
				}
			}
			LeafiaGls.enableBlend();
			LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableLighting();
			GL11.glAlphaFunc(GL11.GL_ALWAYS, 0);
			mdl.renderPart("Glass");
			GL11.glAlphaFunc(GL11.GL_GREATER, 0);
			LeafiaGls.disableBlend();
		}

		if (te instanceof TileEntityCoreEmitter) {
			//int range = ((TileEntityCoreEmitter)tileEntity).beam;
			RayTraceResult result = ((IMixinTileEntityCoreEmitter) te).lastRaycast();
			if (result != null) {
				range = new Vec3d(te.getPos()).add(0.5, 0.5, 0.5).distanceTo(result.hitVec);
				if (((IMixinTileEntityCoreEmitter) te).isActive()) {
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
					float width = (float) Math.max(1, Math.log10(((TileEntityCoreEmitter) te).prev) - 6) / 8F;
					int colorA = 0x401500;
					int colorB = 0x5B1D00;
					if (te instanceof CoreCEmitterTE) {
						colorA = 0x281332;
						colorB = 0x110165;
					}
					bindTexture(AddonBase.solid_e);
					LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.STRAIGHT, EnumBeamType.SOLID, colorA, 0x7F7F7F, 0, 1, 0F, 2, width);
					LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.RANDOM, EnumBeamType.SOLID, colorA, 0x7F7F7F, (int) te.getWorld().getTotalWorldTime() % 1000, (int) (0.3F * range / width), width * 0.75F, 2, width * 0.5F);
					LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.RANDOM, EnumBeamType.SOLID, colorB, 0x7F7F7F, (int) te.getWorld().getTotalWorldTime() % 1000 + 1, (int) (0.3F * range / width), width * 0.75F, 2, width * 0.5F);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lx, ly);
					bindTexture(tex);
				}
			}
		}

		if (te instanceof TileEntityCoreInjector) {
			TileEntityCoreInjector injector = (TileEntityCoreInjector) te;
			//int range = injector.beam;

			if (range > 0) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
				bindTexture(AddonBase.solid_e);
				if (injector.tanks[0].getFill() > 0)
					LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.SPIRAL, EnumBeamType.SOLID, injector.tanks[0].getFluid().getFluid().getColor(), 0x7F7F7F, (int) te.getWorld().getTotalWorldTime() * -2 % 360, (int) Math.round(range), 0.09F, 3, 0.0625F);
				if (injector.tanks[1].getFill() > 0)
					LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.SPIRAL, EnumBeamType.SOLID, injector.tanks[1].getFluid().getFluid().getColor(), 0x7F7F7F, (int) te.getWorld().getTotalWorldTime() * -2 % 360 + 180, (int) Math.round(range), 0.09F, 3, 0.0625F);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lx, ly);
			}
			bindTexture(tex);
		}

		if (te instanceof TileEntityCoreReceiver) {
			TileEntityCoreReceiver absorber = (TileEntityCoreReceiver) te;
			IMixinTileEntityCoreReceiver mixin = (IMixinTileEntityCoreReceiver)te;
			if (mixin.getCore() != null) {
				IMixinTileEntityCore core = (IMixinTileEntityCore)mixin.getCore();
				double mspk = core.getDFCExpellingSpk() * 20 / core.getDFCAbsorbers().size() * mixin.getLevel();// /10;
				mspk *= (getWorld().rand.nextDouble() * 99 + 1); // What the fuck why is it not
				mspk = Math.min(100000, mspk);
				int distance = (int) Math.round(Math.sqrt(absorber.getPos().distanceSq(mixin.getCore().getPos())));
				GL11.glTranslated(0, 0, -distance);
				if (mspk > 0) {
					bindTexture(AddonBase.solid_e);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
					for (int i = 0; i < (int) Math.pow(mspk / 200, 0.5) + 1; i++) {
						LCEBeamPronter.prontBeam(
								false,
								new Vec3d(0, 0, distance - 0.5),
								EnumWaveType.RANDOM,
								EnumBeamType.SOLID,
								0x5B1D00, 0x7F7F7F,
								(int) Math.floorMod(absorber.getWorld().getTotalWorldTime() * 3 + (int) (partialTicks / 7) + i + 33, 1500),
								distance * (i + 1),
								0.2F + (float) (Math.pow(mspk / 1000, 0.25) - 1) * 0.025F,
								3,
								0.1F + 0.0666F*(float)(Math.pow(mspk / 1000, 0.25) - 1)
						);
					}
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lx, ly);
					bindTexture(tex);
				}
			}
		}
		if (te instanceof CoreExchangerTE) {
			//int range = injector.beam;

			if (range > 0) {
				//0xffa200
				bindTexture(AddonBase.solid_e);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
				LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0x9b4100, 0x9b4100, (int)te.getWorld().getTotalWorldTime() * -25 % 360, (int)(range * 3), 0.125F/1.5f, 1, 0.01f);
				LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0x9b4100, 0x9b4100, (int)te.getWorld().getTotalWorldTime() * -15 % 360 + 180, (int)(range * 3), 0.125F/1.5f, 1, 0.01f);
				LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.SPIRAL, EnumBeamType.SOLID, 0x9b4100, 0x9b4100, (int)te.getWorld().getTotalWorldTime() * -5 % 360 + 180, (int)(range * 3), 0.125F/1.5f, 1, 0.01f);
				LCEBeamPronter.prontBeam(false,new Vec3d(0, 0, -range), EnumWaveType.STRAIGHT, EnumBeamType.SOLID, 0xffd000, 0xffd000, 0, 1, 0, 1, 0.01f);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lx, ly);
				bindTexture(tex);
			}
			bindTexture(dfc_exchanger_tex);
		}

		GlStateManager.enableLighting();
		GL11.glPopMatrix();
		double maxAbs = Math.max(Math.abs(unit.x), Math.max(Math.abs(unit.y), Math.abs(unit.z)));
		for (EnumFacing face : EnumFacing.values()) {
			GL11.glPushMatrix();
			if (face == EnumFacing.UP)
				GL11.glRotatef(90, 1, 0, 0);
			else if (face == EnumFacing.DOWN)
				GL11.glRotatef(-90, 1, 0, 0);
			else
				GL11.glRotatef(90 * (2 - face.getHorizontalIndex()), 0, 1, 0);
			if (!isFace(face.getOpposite(), unit))
				mdl.renderPart("Frame");

			boolean isFront = false;
			double expected = maxAbs * face.getAxisDirection().getOffset();
			if (face.getAxis() == Axis.X)
				isFront = unit.x == expected;
			else if (face.getAxis() == Axis.Y)
				isFront = unit.y == expected;
			else if (face.getAxis() == Axis.Z)
				isFront = unit.z == expected;
			if (isFront) {
				for (int i = 0; i < 4; i++) {
					if (face == EnumFacing.UP) {
						if (!isFace(EnumFacing.byHorizontalIndex(Math.floorMod(-i, 4)), unit))
							mdl.renderPart("Arm");
					} else if (face == EnumFacing.DOWN) {
						if (!isFace(EnumFacing.byHorizontalIndex(Math.floorMod(2 - i, 4)), unit))
							mdl.renderPart("Arm");
					} else {
						EnumFacing check;
						if (i == 0) check = EnumFacing.UP;
						else if (i == 1) check = face.rotateY().getOpposite();
						else if (i == 2) check = EnumFacing.DOWN;
						else check = face.rotateY();
						if (!isFace(check, unit))
							mdl.renderPart("Arm");
					}
					GL11.glRotatef(90, 0, 0, 1);
				}
			}
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}

	void bindByIconName(String resource) { // copied from RenderPWRMeshedWreck lmao
		// convert format like "hbm:         blocks/brick_concrete    "
		//                  to "hbm:textures/blocks/brick_concrete.png"
		bindTexture(new ResourceLocation(resource.replaceFirst("(\\w+:)?(.*)", "$1textures/$2.png")));
	}
}
