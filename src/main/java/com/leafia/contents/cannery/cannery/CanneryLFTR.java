package com.leafia.contents.cannery.cannery;

import com.hbm.api.energymk2.IEnergyConnectorMK2;
import com.hbm.api.fluidmk2.IFluidConnectorMK2;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.util.I18nUtil;
import com.hbm.wiaj.JarScene;
import com.hbm.wiaj.JarScript;
import com.hbm.wiaj.WorldInAJar;
import com.hbm.wiaj.actions.*;
import com.hbm.wiaj.actors.ActorFancyPanel;
import com.hbm.wiaj.actors.ActorFancyPanel.Orientation;
import com.hbm.wiaj.actors.ActorTileEntity;
import com.hbm.wiaj.actors.ITileActorRenderer;
import com.hbm.wiaj.cannery.CanneryBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.LFTR;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.AddonItems.LeafiaRods;
import com.leafia.contents.cannery.actions.ActionOffsetAndZoom;
import com.leafia.contents.cannery.actors.ActorVisFrame;
import com.leafia.contents.cannery.actors.ActorVisText;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRender;
import com.leafia.contents.network.ff_duct.FFDuctTE;
import com.leafia.contents.network.ff_duct.uninos.IFFConnector;
import com.leafia.contents.network.ff_duct.uninos.IFFHandler;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.transformer.LeafiaGls;
import com.llib.math.LeafiaColor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public class CanneryLFTR extends CanneryBase {
	@Override
	public ItemStack getIcon() {
		return new ItemStack(LFTR.element);
	}
	@Override
	public String getName() {
		return "cannery.leafia.lftr";
	}
	@Override
	public JarScript createScript() {
		WorldInAJar world = new WorldInAJar(9, 12, 9);
		JarScript script = new JarScript(world);
		int center = 4;
		{
			// INTRODUCTION
			JarScene scene = new JarScene(script);
			scene.add(new ActionSetZoom(1.5,0));
			for (int x = world.sizeX-1; x >= 0; x--) {
				for (int z = 0; z < world.sizeZ; z++)
					scene.add(new ActionSetBlock(x,0,z,ModBlocks.concrete_smooth));

				scene.add(new ActionWait(2));
			}
			{
				// build example LFTR
				for (int y = 1; y <= 3; y++) {
					scene.add(new ActionSetBlock(center-1,y,center-1,ModBlocks.deco_pipe_framed,0));
					scene.add(new ActionSetBlock(center+1,y,center-1,ModBlocks.deco_pipe_framed,0));
					scene.add(new ActionSetBlock(center-1,y,center+1,ModBlocks.deco_pipe_framed,0));
					scene.add(new ActionSetBlock(center+1,y,center+1,ModBlocks.deco_pipe_framed,0));
				}
				for (int rx = -1; rx <= 1; rx++) {
					for (int rz = -1; rz <= 1; rz++) {
						scene.add(new ActionSetBlock(center+rx,4,center+rz,ModBlocks.brick_compound));
						scene.add(new ActionSetBlock(center+rx,8,center+rz,ModBlocks.brick_compound));
					}
				}
				scene.add(new ActionSetBlock(center,4,center,LFTR.plug,2));
				scene.add(new ActionSetBlock(center,4,center-1,AddonBlocks.ff_duct_solid_shielded));
				for (int y = 5; y <= 7; y++) {
					Block hull = (y == 6 ? ModBlocks.brick_concrete : ModBlocks.brick_compound);
					for (int rx = -1; rx <= 1; rx++) {
						scene.add(new ActionSetBlock(center+rx,y,center-2,hull));
						scene.add(new ActionSetBlock(center+rx,y,center+2,hull));
					}
					for (int rz = -1; rz <= 1; rz++) {
						scene.add(new ActionSetBlock(center-2,y,center+rz,hull));
						scene.add(new ActionSetBlock(center+2,y,center+rz,hull));
					}
					scene.add(new ActionSetBlock(center,y,center,LFTR.element));
					scene.add(new ActionSetBlock(center-1,y,center-1,LFTR.element));
					scene.add(new ActionSetBlock(center+1,y,center-1,LFTR.element));
					scene.add(new ActionSetBlock(center+1,y,center+1,LFTR.element));
					scene.add(new ActionSetBlock(center-1,y,center+1,LFTR.element));
					addArbitrary(scene,center-1,y,center,0);
					addArbitrary(scene,center+1,y,center,0);
					addArbitrary(scene,center,y,center-1,0);
					addArbitrary(scene,center,y,center+1,0);
				}
				for (int y = 8; y <= 11; y++) {
					Block block = (y == 8 ? LFTR.control : LFTR.extension);
					scene.add(new ActionSetBlock(center,y,center,block,0));
					scene.add(new ActionSetBlock(center-1,y,center-1,block,0));
					scene.add(new ActionSetBlock(center+1,y,center-1,block,0));
					scene.add(new ActionSetBlock(center-1,y,center+1,block,0));
					scene.add(new ActionSetBlock(center+1,y,center+1,block,0));
				}
				for (int y = 1; y <= 3; y++)
					placePipe(scene,center,y,center);
				scene.add(new ActionSetTile(center,0,center,generateDuctTE()));
				scene.add(new ActionSetBlock(center,0,center,AddonBlocks.ff_duct_solid_shielded));
				scene.add(new ActionSetBlock(center,6,center,LFTR.ejector,3));
			}
			showText(scene,0,-30,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.0"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			showText(scene,0,-30,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.1"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			showText(scene,0,-30,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.2"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));
			script.addScene(scene);
		}
		{
			// MIXING VAT
			JarScene scene = new JarScene(script);
			for (int y = 11; y >= 0; y--) {
				scene.add(new ActionWait(3));
				for (int x = center-2; x <= center+2; x++) {
					for (int z = center-2; z <= center+2; z++) {
						removeArbitrary(scene,x,y,z);
						scene.add(new ActionSetTile(x,y,z,null));
						scene.add(new ActionSetBlock(x,y,z,y == 0 ? ModBlocks.concrete_smooth : Blocks.AIR));
					}
				}
			}
			scene.add(new ActionWait(20));
			scene.add(new ActionOffsetAndZoom(0,3.5,0,0.5,20));
			scene.add(new ActionWait(2));

			showText(scene,0,0,Orientation.CENTER,I18nUtil.resolveKey("cannery.leafia.lftr.3"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			NBTTagCompound data = new NBTTagCompound();
			data.setDouble("x",center);
			data.setDouble("y",1);
			data.setDouble("z",center-1);
			data.setInteger("meta",12);
			data.setBoolean("spin",false);
			scene.add(new ActionCreateActor(1,new ActorTileEntity(new ActorMixingVat(),data)));

			scene.add(new ActionWait(10));
			showText(scene,0,-15,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.4"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			showText(scene,40,9,Orientation.LEFT,new ItemStack(ModItems.nugget_th232,4),new ItemStack(ModItems.nugget_u235,2));
			scene.add(new ActionWait(30));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(10));
			scene.add(new ActionUpdateActor(1,"spin",true));
			scene.add(new ActionWait(20*3));
			scene.add(new ActionUpdateActor(1,"spin",false));
			scene.add(new ActionWait(20));

			showText(scene,0,-15,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.5"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			{
				scene.add(new ActionSetTile(center-1,1,center+1,new FFDummyConnector()));
				placePipe(scene,center-2,1,center+1);
				placePipe(scene,center-2,1,center+2);
				placePipe(scene,center-2,1,center+3);
			}
			scene.add(new ActionWait(10));

			showText(scene,0,-15,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.6"));
			scene.add(new ActionWait(20*7));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			scene.add(new ActionRemoveActor(1));
			scene.add(new ActionSetTile(center-1,1,center+1,null));
			removePipe(scene,center-2,1,center+1);
			removePipe(scene,center-2,1,center+2);
			removePipe(scene,center-2,1,center+3);

			scene.add(new ActionWait(20));

			scene.add(new ActionOffsetBy(0,-1.5,0,20));
			scene.add(new ActionWait(2));
			scene.add(new ActionSetBlock(center,3,center,LFTR.element));

			scene.add(new ActionWait(10));
			for (EnumFacing face : EnumFacing.values()) {
				for (int i = 1; i <= 4; i++) {
					BlockPos p = new BlockPos(center,3,center).offset(face,i);
					if (p.getY() < 1) continue;
					addFlux(scene,script,p.getX(),p.getY(),p.getZ());
				}
			}
			scene.add(new ActionWait(10));

			script.addScene(scene);
		}
		{
			// FLUX
			JarScene scene = new JarScene(script);
			scene.add(new ActionWait(10));
			showText(scene,0,-15,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.7"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			for (EnumFacing face : EnumFacing.values()) {
				for (int i = 1; i <= 4; i++) {
					BlockPos p = new BlockPos(center,3,center).offset(face,i);
					if (p.getY() < 1) continue;
					removeFlux(scene,p.getX(),p.getY(),p.getZ());
				}
			}
			scene.add(new ActionWait(20));
			FiaMatrix mat = new FiaMatrix(new Vec3d(center+0.5,3.5,center+0.5));
			doCornerFlux2D(scene,script,mat,false);
			doCornerFlux2D(scene,script,mat.rotateY(90),false);
			doCornerFlux2D(scene,script,mat.rotateX(90),false);

			scene.add(new ActionWait(10));
			scene.add(new ActionRotateBy(30,0,20));
			scene.add(new ActionWait(2));
			showText(scene,0,-15,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.8"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			scene.add(new ActionWait(10));
			scene.add(new ActionRotateBy(-30,0,20));

			scene.add(new ActionWait(10));
			doCornerFlux2D(scene,script,mat,true);
			doCornerFlux2D(scene,script,mat.rotateY(90),true);
			doCornerFlux2D(scene,script,mat.rotateX(90),true);
			scene.add(new ActionWait(20));

			script.addScene(scene);
		}
		{
			// MELTDOWN
			JarScene scene = new JarScene(script);

			showText(scene,0,-15,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.9"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));

			script.addScene(scene);
		}
		{
			// CONTROL RODS
			JarScene scene = new JarScene(script);
			scene.add(new ActionSetBlock(center,3,center-1,LFTR.control,EnumFacing.SOUTH.getIndex()));
			scene.add(new ActionSetBlock(center,3,center-2,LFTR.extension,8));
			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center,3,center-1,Blocks.AIR));
			scene.add(new ActionSetBlock(center,3,center-2,Blocks.AIR));
			scene.add(new ActionSetBlock(center-1,3,center,LFTR.control,EnumFacing.EAST.getIndex()));
			scene.add(new ActionSetBlock(center-2,3,center,LFTR.extension,4));
			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center-1,3,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center-2,3,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center,4,center,LFTR.control,0));
			scene.add(new ActionSetBlock(center,5,center,LFTR.extension,0));
			scene.add(new ActionWait(10));

			showText(scene,0,-32,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.a"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));
			showText(scene,0,-32,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.b"));
			scene.add(new ActionWait(20*6));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionSetBlock(center,4,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center,5,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center,3,center,Blocks.AIR));
			scene.add(new ActionWait(20));
			scene.add(new ActionOffsetBy(0,1.5,0,20));
			scene.add(new ActionWait(20));

			script.addScene(scene);
		}
		{
			JarScene scene = new JarScene(script);
			scene.add(new ActionSetBlock(center,1,center,ModBlocks.block_graphite));
			scene.add(new ActionWait(10));

			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.c"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center,1,center,Blocks.AIR));
			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center,1,center,PWR.reflector));
			scene.add(new ActionWait(10));

			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.d"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));
			scene.add(new ActionSetBlock(center,1,center,Blocks.AIR));
			scene.add(new ActionWait(20));

			script.addScene(scene);
		}
		{
			JarScene scene = new JarScene(script);

			scene.add(new ActionSetBlock(center,1,center,LFTR.arbitrary));
			scene.add(new ActionWait(10));

			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.e"));
			scene.add(new ActionWait(20*6));
			scene.add(new ActionRemoveActor(0));
			showText(scene,14,18,Orientation.LEFT,new ItemStack(ModBlocks.block_graphite));
			scene.add(new ActionWait(15));
			addArbitrary(scene,center,1,center,0);
			scene.add(new ActionWait(15));
			scene.add(new ActionRemoveActor(0));
			scene.add(new ActionWait(20));
			removeArbitrary(scene,center,1,center);
			scene.add(new ActionWait(20));

			script.addScene(scene);
		}
		{
			JarScene scene = new JarScene(script);
			scene.add(new ActionSetBlock(center,1,center,LFTR.plug,2));
			scene.add(new ActionWait(10));

			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.f"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));
			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.g"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));
			scene.add(new ActionWait(20));
			scene.add(new ActionRotateBy(0,60,20));
			scene.add(new ActionWait(2));
			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.lftr.h"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			script.addScene(scene);
		}

		return script;
	}
	public void showText(JarScene scene,int x,int y,Orientation orient,Object... text) {
		scene.add(new ActionCreateActor(0,new ActorFancyPanel(Minecraft.getMinecraft().fontRenderer,x,y,new Object[][] {text},200).setColors(colorCopper).setOrientation(orient)));
	}
	public void doCornerFlux2D(JarScene scene,JarScript script,FiaMatrix mat,boolean remove) {
		for (int rx = -1; rx <= 1; rx+=2) {
			for (int ry = -1; ry <= 1; ry+=2) {
				FiaMatrix offset = mat.translate(rx,ry,0);
				if (remove)
					removeFlux(scene,(int)offset.position.x,(int)offset.position.y,(int)offset.position.z);
				else
					addFlux(scene,script,(int)offset.position.x,(int)offset.position.y,(int)offset.position.z);
			}
		}
	}
	public void addFlux(JarScene scene,JarScript script,int x,int y,int z) {
		scene.add(new ActionCreateActor(getHash(scene,x,y,z),new ActorVisFrame(script,x,y,z,0xFFFF00,0.25f)));
	}
	public void removeFlux(JarScene scene,int x,int y,int z) {
		scene.add(new ActionRemoveActor(getHash(scene,x,y,z)));
	}
	public FFDuctTE generateDuctTE() {
		FFDuctTE ductTE = new FFDuctTE();
		ductTE.type = AddonFluids.FLUORIDE;
		return ductTE;
	}
	public void placePipe(JarScene scene,int x,int y,int z) {
		scene.add(new ActionSetTile(x,y,z,generateDuctTE()));
		scene.add(new ActionSetBlock(x,y,z,AddonBlocks.ff_duct,2));
	}
	public void removePipe(JarScene scene,int x,int y,int z) {
		scene.add(new ActionSetTile(x,y,z,null));
		scene.add(new ActionSetBlock(x,y,z,Blocks.AIR));
	}
	public int getHash(JarScene scene,int x,int y,int z) {
		int sizeX = scene.script.world.sizeX;
		int sizeY = scene.script.world.sizeY;
		int sizeZ = scene.script.world.sizeZ;
		return x+y*sizeX+z*sizeX*sizeY+300;
	}
	public void addArbitrary(JarScene scene,int x,int y,int z,int variant) {
		int hash = getHash(scene,x,y,z);
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("x",x);
		data.setDouble("y",y);
		data.setDouble("z",z);
		data.setInteger("variant",variant);
		scene.add(new ActionSetBlock(x,y,z,LFTR.arbitrary));
		scene.add(new ActionCreateActor(hash,new ActorTileEntity(new ActorArbitrary(),data)));
	}
	public void removeArbitrary(JarScene scene,int x,int y,int z) {
		int hash = getHash(scene,x,y,z);
		scene.add(new ActionSetBlock(x,y,z,Blocks.AIR));
		scene.add(new ActionRemoveActor(hash));
	}
	public static class FFDummyConnector extends TileEntity implements IFFHandler {
		@Override
		public boolean hasCapability(Capability<?> capability,@Nullable EnumFacing facing) {
			if (capability.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY))
				return true;
			return super.hasCapability(capability,facing);
		}
		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new IFluidTankProperties[0];
		}
		@Override
		public int fill(FluidStack resource,boolean doFill) {
			return 0;
		}
		@Override
		public @Nullable FluidStack drain(FluidStack resource,boolean doDrain) {
			return null;
		}
		@Override
		public @Nullable FluidStack drain(int maxDrain,boolean doDrain) {
			return null;
		}
	}
	public static class ActorMixingVat implements ITileActorRenderer {
		float mixerRot = 115;
		float prevRot = mixerRot;
		@Override
		public void renderActor(WorldInAJar world, int ticks, float interp, NBTTagCompound data) {
			double x = data.getDouble("x");
			double y = data.getDouble("y");
			double z = data.getDouble("z");
			int metadata = data.getInteger("meta");
			WaveFrontObjectVAO mdl = MixingVatRender.mdl;
			ResourceLocation tex = MixingVatRender.tex;
			ResourceLocation rsc = MixingVatRender.rsc;
			LeafiaGls.translate(x+0.5,y,z+0.5);
			switch(metadata - BlockDummyable.offset) {
				case 2: LeafiaGls.rotate(180, 0F, 1F, 0F); break;
				case 4: LeafiaGls.rotate(270, 0F, 1F, 0F); break;
				case 3: LeafiaGls.rotate(0, 0F, 1F, 0F); break;
				case 5: LeafiaGls.rotate(90, 0F, 1F, 0F); break;
			}
			LeafiaGls.translate(0.5,0,-1);
			ITileActorRenderer.bindTexture(tex);
			mdl.renderPart("Base");

			LeafiaGls.pushMatrix();
			float rot = prevRot+(mixerRot-prevRot)*interp;
			LeafiaGls.translate(0,0,-0.5);
			LeafiaGls.rotate(-rot,0,1,0);
			LeafiaGls.translate(0,0,0.5);
			mdl.renderPart("MixingBlade");
			LeafiaGls.popMatrix();

			mdl.renderPart("VatGlass");
			LeafiaGls.shadeModel(GL11.GL_SMOOTH);
			mdl.renderPart("Vat");
			mdl.renderPart("Tanks");
			ITileActorRenderer.bindTexture(rsc);

			LeafiaGls.pushMatrix();
			float level = 1;
			FluidType ntmf = AddonFluids.FLUORIDE;
			LeafiaColor color = new LeafiaColor(ntmf.getColor());
			LeafiaGls.color(color.getRed(),color.getGreen(),color.getBlue());
			LeafiaGls.translate(0,-0.65+0.65*level,0);
			mdl.renderPart("VatLiquid");
			LeafiaGls.popMatrix();

			LeafiaGls.color(1,1,1);
			LeafiaGls.shadeModel(GL11.GL_FLAT);
		}
		@Override
		public void updateActor(int ticks,NBTTagCompound nbtTagCompound) {
			prevRot = mixerRot;
			if (nbtTagCompound.getBoolean("spin")) {
				this.mixerRot += 9F;
				if (this.mixerRot >= 360F) {
					this.mixerRot -= 360F;
					this.prevRot -= 360F;
				}
			}
		}
	}
	public static class ActorArbitrary implements ITileActorRenderer {
		void tryBindQuads(IBakedModel baked,IBlockState display,EnumFacing face) {
			try {
				List<BakedQuad> quads = baked.getQuads(display,face,0);
				if (quads.size() > 0)
					bindByIconName(quads.get(0).getSprite().getIconName());
				else
					bindByIconName(baked.getParticleTexture().getIconName());
			} catch (IllegalArgumentException ignored) {} // FUCK YOUU
		}
		void bindByIconName(String resource) {
			// convert format like "hbm:         blocks/brick_concrete    "
			//                  to "hbm:textures/blocks/brick_concrete.png"
			ITileActorRenderer.bindTexture(new ResourceLocation(resource.replaceFirst("(\\w+:)?(.*)","$1textures/$2.png")));
		}
		@Override
		public void renderActor(WorldInAJar world, int ticks, float interp, NBTTagCompound data) {
			double x = data.getDouble("x");
			double y = data.getDouble("y");
			double z = data.getDouble("z");
			GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
			IBlockState display = ModBlocks.block_graphite.getDefaultState();
			IBakedModel baked = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(display);
			tryBindQuads(baked,display,EnumFacing.NORTH);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GlStateManager.color(1, 1, 1, 1);
			//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buf = tessellator.getBuffer();
			BlockPos pos = new BlockPos(x+.5,y+.5,z+.5);
			for (EnumFacing facing : EnumFacing.values()) {
				int light = world.getCombinedLight(pos.offset(facing.getOpposite()), 0);
				int lx = light & 0xFFFF;
				int ly = light >> 16;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lx, ly);

				buf.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX);
				FiaMatrix mat = new FiaMatrix(new Vec3d(0,0,0),new Vec3d(facing.getDirectionVec())).translate(0,0,0.502);
				Vec3d vec0 = mat.translate(-6/16d,-6/16d,0).position;
				Vec3d vec1 = mat.translate(6/16d,-6/16d,0).position;
				Vec3d vec2 = mat.translate(6/16d,6/16d,0).position;
				Vec3d vec3 = mat.translate(-6/16d,6/16d,0).position;
				buf.pos(vec0.x,vec0.y,vec0.z).tex(0,1).endVertex();
				buf.pos(vec1.x,vec1.y,vec1.z).tex(1,1).endVertex();
				buf.pos(vec2.x,vec2.y,vec2.z).tex(1,0).endVertex();
				buf.pos(vec3.x,vec3.y,vec3.z).tex(0,0).endVertex();
				tessellator.draw();
			}
		}
		@Override
		public void updateActor(int ticks,NBTTagCompound nbtTagCompound) {

		}
	}
}
