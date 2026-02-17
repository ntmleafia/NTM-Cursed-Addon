package com.leafia.contents.cannery.cannery;

import com.hbm.blocks.ModBlocks;
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
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.AddonItems.LeafiaRods;
import com.leafia.contents.cannery.actors.ActorVisText;
import com.leafia.contents.machines.reactors.pwr.blocks.components.control.PWRControlRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CanneryPWR extends CanneryBase {
	@Override
	public ItemStack getIcon() {
		return new ItemStack(PWR.element);
	}
	@Override
	public String getName() {
		return "cannery.pwr";
	}
	@Override
	public JarScript createScript() {
		WorldInAJar world = new WorldInAJar(9, 5, 9);
		JarScript script = new JarScript(world);
		int center = 4;
		{
			// INTRO
			JarScene scene = new JarScene(script);
			scene.add(new ActionSetZoom(2, 0));
			for(int x = world.sizeX - 1; x >= 0 ; x--) {
				for (int z = 0; z < world.sizeZ; z++)
					scene.add(new ActionSetBlock(x,0,z,ModBlocks.concrete_smooth));
				scene.add(new ActionWait(2));
			}
			{
				// build example PWR
				for (int xr = -2; xr <= 2; xr+=4) {
					for (int zr = -1; zr <= 1; zr++) {
						int x = xr+center;
						int z = zr+center;
						scene.add(new ActionSetBlock(x,1,z,zr == 0 ? PWR.port : PWR.reflector));
						scene.add(new ActionSetBlock(x,2,z,PWR.hull));
					}
				}
				for (int xr = -1; xr <= 1; xr++) {
					for (int zr = -2; zr <= 2; zr+=2) {
						int x = xr+center;
						int z = zr+center;
						scene.add(new ActionSetBlock(x,1,z,xr == 0 ? PWR.hull : PWR.reflector));
						scene.add(new ActionSetBlock(x,2,z,PWR.hull));
					}
				}
				for (int xr = -1; xr <= 1; xr++) {
					for (int zr = -1; zr <= 1; zr++) {
						int x = xr+center;
						int z = zr+center;
						scene.add(new ActionSetBlock(x,0,z,PWR.hull));
						scene.add(new ActionSetBlock(x,3,z,PWR.hull));
						scene.add(new ActionSetBlock(x,2,z,PWR.channel));
					}
				}
				scene.add(new ActionSetBlock(center,1,center,Blocks.AIR)); // :lazy:

				addControl(scene,center,1,center,0);
				addControl(scene,center+1,1,center+1,0);
				addControl(scene,center+1,1,center-1,0);
				addControl(scene,center-1,1,center+1,0);
				addControl(scene,center-1,1,center-1,0);

				scene.add(new ActionSetBlock(center+1,1,center,PWR.element));
				scene.add(new ActionSetBlock(center-1,1,center,PWR.element));
				scene.add(new ActionSetBlock(center,1,center+1,PWR.element));
				scene.add(new ActionSetBlock(center,1,center-1,PWR.element));

				scene.add(new ActionSetBlock(center,2,center-2,PWR.terminal,2));
			}
			showText(scene,0,-30,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.0"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			showText(scene,0,-30,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.1"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			script.addScene(scene);
		}
		{
			// SKINS
			JarScene scene = new JarScene(script);
			for (int y = 3; y >= 0; y--) {
				scene.add(new ActionWait(3));
				for (int x = center-2; x <= center+2; x++) {
					for (int z = center-2; z <= center+2; z++) {
						scene.add(new ActionSetBlock(x,y,z,y == 0 ? ModBlocks.concrete_smooth : Blocks.AIR));
						removeControl(scene,x,y,z);
					}
				}
			}
			scene.add(new ActionWait(10));
			scene.add(new ActionSetBlock(center,1,center,PWR.element));
			scene.add(new ActionSetBlock(center+2,1,center,PWR.element_old));
			scene.add(new ActionSetBlock(center-2,1,center,PWR.element_old_blank));
			scene.add(new ActionWait(10));
			showText(scene,0,5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.2"));
			scene.add(new ActionWait(20*3));
			scene.add(new ActionRemoveActor(0));
			showText(scene,0,5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.3"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			scene.add(new ActionWait(10));
			scene.add(new ActionSetBlock(center+2,1,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center-2,1,center,Blocks.AIR));
			scene.add(new ActionWait(10));
			script.addScene(scene);
		}
		{
			// LOADING FUEL
			JarScene scene = new JarScene(script);
			showText(scene,0,5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.4"));
			scene.add(new ActionWait(20*3));
			scene.add(new ActionRemoveActor(0));
			showText(scene,0,5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.5"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			showText(scene,14,18,Orientation.LEFT,new ItemStack(LeafiaRods.leafRodMEU235));
			scene.add(new ActionWait(30));
			scene.add(new ActionRemoveActor(0));

			showText(scene,0,5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.6"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));

			scene.add(new ActionRotateBy(45,-60,20));

			scene.add(new ActionWait(10));

			script.addScene(scene);
		}
		{
			// FLOOR PAINT
			JarScene scene = new JarScene(script);
			scene.add(new ActionSetBlock(center-1,0,center-1,Blocks.WOOL,EnumDyeColor.MAGENTA.getMetadata()));
			scene.add(new ActionSetBlock(center-1,0,center+1,Blocks.WOOL,EnumDyeColor.MAGENTA.getMetadata()));
			scene.add(new ActionSetBlock(center+1,0,center-1,Blocks.WOOL,EnumDyeColor.MAGENTA.getMetadata()));
			scene.add(new ActionSetBlock(center+1,0,center+1,Blocks.WOOL,EnumDyeColor.MAGENTA.getMetadata()));
			scene.add(new ActionWait(10));

			showText(scene,0,-25,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.7"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(10));
			for (int i = 1; i <=4; i++) {
				scene.add(new ActionSetBlock(center+i,0,center,i%2 == 0 ? Blocks.WOOL : Blocks.CONCRETE,EnumDyeColor.LIME.getMetadata()));
				scene.add(new ActionSetBlock(center-i,0,center,i%2 == 0 ? Blocks.WOOL : Blocks.CONCRETE,EnumDyeColor.LIME.getMetadata()));
				scene.add(new ActionSetBlock(center,0,center+i,i%2 == 0 ? Blocks.WOOL : Blocks.CONCRETE,EnumDyeColor.LIME.getMetadata()));
				scene.add(new ActionSetBlock(center,0,center-i,i%2 == 0 ? Blocks.WOOL : Blocks.CONCRETE,EnumDyeColor.LIME.getMetadata()));
			}
			scene.add(new ActionWait(10));
			showText(scene,0,-25,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.8"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(10));

			script.addScene(scene);
		}
		{
			// CORNER FLUX
			JarScene scene = new JarScene(script);
			scene.add(new ActionCreateActor(1,new ActorVisText(script,center-1,1,center-1,0xFFFFFF,1.5f,false,"1/2")));
			scene.add(new ActionCreateActor(2,new ActorVisText(script,center+1,1,center-1,0xFFFFFF,1.5f,false,"1/2")));
			scene.add(new ActionCreateActor(3,new ActorVisText(script,center-1,1,center+1,0xFFFFFF,1.5f,false,"1/2")));
			scene.add(new ActionCreateActor(4,new ActorVisText(script,center+1,1,center+1,0xFFFFFF,1.5f,false,"1/2")));
			scene.add(new ActionWait(20*2));
			scene.add(new ActionRemoveActor(1));
			scene.add(new ActionRemoveActor(2));
			scene.add(new ActionRemoveActor(3));
			scene.add(new ActionRemoveActor(4));
			scene.add(new ActionWait(10));
			script.addScene(scene);
		}
		{
			// LINEAR FLUX
			JarScene scene = new JarScene(script);
			for (int i = 1; i <= 10; i++) {
				scene.add(new ActionCreateActor(i,new ActorVisText(script,center+i*2,1,center,0xFFFFFF,1.5f,false,"1/"+(int)Math.pow(2,i))));
				scene.add(new ActionCreateActor(i+11,new ActorVisText(script,center-i*2,1,center,0xFFFFFF,1.5f,false,"1/"+(int)Math.pow(2,i))));
				scene.add(new ActionCreateActor(i+22,new ActorVisText(script,center,1,center+i*2,0xFFFFFF,1.5f,false,"1/"+(int)Math.pow(2,i))));
				scene.add(new ActionCreateActor(i+33,new ActorVisText(script,center,1,center-i*2,0xFFFFFF,1.5f,false,"1/"+(int)Math.pow(2,i))));
			}
			scene.add(new ActionWait(20*3));
			for (int i = 1; i <= 10; i++) {
				scene.add(new ActionRemoveActor(i));
				scene.add(new ActionRemoveActor(i+11));
				scene.add(new ActionRemoveActor(i+22));
				scene.add(new ActionRemoveActor(i+33));
			}
			scene.add(new ActionWait(10));
			scene.add(new ActionRotateBy(-45,60,20));
			scene.add(new ActionWait(10));
			addControl(scene,center+2,1,center,0);
			addControl(scene,center-2,1,center,0);
			addControl(scene,center,1,center+2,0);
			addControl(scene,center,1,center-2,0);
			scene.add(new ActionWait(10));
			script.addScene(scene);
		}
		{
			// CONTROL RODS
			JarScene scene = new JarScene(script);
			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.9"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			removeControl(scene,center+2,1,center);
			removeControl(scene,center-2,1,center);
			removeControl(scene,center,1,center+2);
			removeControl(scene,center,1,center-2);
			scene.add(new ActionWait(20));
			addControl(scene,center+2,1,center,1);
			addControl(scene,center-2,1,center,1);
			addControl(scene,center,1,center+2,1);
			addControl(scene,center,1,center-2,1);
			scene.add(new ActionWait(10));

			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.a"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			removeControl(scene,center+2,1,center);
			removeControl(scene,center-2,1,center);
			removeControl(scene,center,1,center+2);
			removeControl(scene,center,1,center-2);
			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center+2,1,center,PWR.channel));
			scene.add(new ActionSetBlock(center-2,1,center,PWR.channel));
			scene.add(new ActionSetBlock(center,1,center+2,PWR.channel));
			scene.add(new ActionSetBlock(center,1,center-2,PWR.channel));
			scene.add(new ActionWait(10));

			script.addScene(scene);
		}
		{
			// MODERATING
			JarScene scene = new JarScene(script);

			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.b"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.c"));
			scene.add(new ActionWait(20*5));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center+2,1,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center-2,1,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center,1,center+2,Blocks.AIR));
			scene.add(new ActionSetBlock(center,1,center-2,Blocks.AIR));
			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center+2,1,center,ModBlocks.block_graphite));
			scene.add(new ActionSetBlock(center-2,1,center,ModBlocks.block_graphite));
			scene.add(new ActionSetBlock(center,1,center+2,ModBlocks.block_graphite));
			scene.add(new ActionSetBlock(center,1,center-2,ModBlocks.block_graphite));
			scene.add(new ActionWait(10));

			showText(scene,0,-5,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.d"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));

			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center+2,1,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center-2,1,center,Blocks.AIR));
			scene.add(new ActionSetBlock(center,1,center+2,Blocks.AIR));
			scene.add(new ActionSetBlock(center,1,center-2,Blocks.AIR));
			scene.add(new ActionWait(20));
			for (int x = 0; x < world.sizeX; x++) {
				for (int z = 0; z <= world.sizeZ; z++)
					scene.add(new ActionSetBlock(x,0,z,ModBlocks.concrete_smooth));
			}
			scene.add(new ActionWait(20));
			scene.add(new ActionSetBlock(center,2,center,PWR.element));
			scene.add(new ActionWait(3));
			scene.add(new ActionSetBlock(center,3,center,PWR.element));
			scene.add(new ActionWait(3));

			script.addScene(scene);
		}
		{
			// STACKING
			JarScene scene = new JarScene(script);
			showText(scene,0,-30,Orientation.BOTTOM,I18nUtil.resolveKey("cannery.leafia.pwr.e"));
			scene.add(new ActionWait(20*4));
			scene.add(new ActionRemoveActor(0));
			script.addScene(scene);
		}

		return script;
	}
	public void showText(JarScene scene,int x,int y,Orientation orient,Object text) {
		scene.add(new ActionCreateActor(0,new ActorFancyPanel(Minecraft.getMinecraft().fontRenderer,x,y,new Object[][] {{text}},200).setColors(colorCopper).setOrientation(orient)));
	}
	public int getHash(JarScene scene,int x,int y,int z) {
		int sizeX = scene.script.world.sizeX;
		int sizeY = scene.script.world.sizeY;
		int sizeZ = scene.script.world.sizeZ;
		return x+y*sizeX+z*sizeX*sizeY+300;
	}
	public void addControl(JarScene scene,int x,int y,int z,int variant) {
		int hash = getHash(scene,x,y,z);
		NBTTagCompound data = new NBTTagCompound();
		data.setDouble("x",x);
		data.setDouble("y",y);
		data.setDouble("z",z);
		data.setInteger("variant",variant);
		scene.add(new ActionCreateActor(hash,new ActorTileEntity(new ActorControlRods(),data)));
	}
	public void removeControl(JarScene scene,int x,int y,int z) {
		int hash = getHash(scene,x,y,z);
		scene.add(new ActionRemoveActor(hash));
	}
	public static class ActorControlRods implements ITileActorRenderer {
		@Override
		public void renderActor(WorldInAJar world, int ticks, float interp, NBTTagCompound data) {
			WaveFrontObjectVAO mdl = PWRControlRender.meshPWR;
			ResourceLocation top = PWRControlRender.controlTop;
			ResourceLocation side = PWRControlRender.controlSide;
			if (data.getInteger("variant") == 1) {
				mdl = PWRControlRender.meshOld;
				top = PWRControlRender.oldTop;
				side = PWRControlRender.oldSide;
			}
			double x = data.getDouble("x");
			double y = data.getDouble("y");
			double z = data.getDouble("z");
			GlStateManager.translate(x + 0.5D, y, z + 0.5D);
			ITileActorRenderer.bindTexture(top);
			mdl.renderPart("FrameEndTop");
			mdl.renderPart("FrameEndBtm");
			mdl.renderPart("RodsEndTop");
			mdl.renderPart("RodsEndBtm");
			ITileActorRenderer.bindTexture(side);
			mdl.renderPart("FrameSide");
			mdl.renderPart("FrameEnd");
			mdl.renderPart("RodsSide");
			mdl.renderPart("RodsEnd");
		}
		@Override
		public void updateActor(int ticks,NBTTagCompound nbtTagCompound) {

		}
	}
}
