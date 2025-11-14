package com.leafia.init.proxy;

import com.custom_hbm.contents.torex.LCETorex;
import com.custom_hbm.contents.torex.LCETorexRender;
import com.custom_hbm.render.tileentity.LCERenderSpinnyLight;
import com.custom_hbm.sound.LCEAudioWrapper;
import com.custom_hbm.sound.LCEAudioWrapperClient;
import com.custom_hbm.sound.LCEAudioWrapperClientStartStop;
import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.tileentity.deco.TileEntitySpinnyLight;
import com.hbm.tileentity.machine.*;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.effects.folkvangr.visual.RenderCloudFleija;
import com.leafia.contents.machines.powercores.dfc.render.DFCComponentRender;
import com.leafia.contents.machines.powercores.dfc.debris.AbsorberShrapnelEntity;
import com.leafia.contents.machines.powercores.dfc.debris.AbsorberShrapnelRender;
import com.leafia.contents.machines.powercores.dfc.render.DFCCoreRender;
import com.leafia.contents.network.spk_cable.SPKCableRender;
import com.leafia.contents.network.spk_cable.SPKCableTE;
import com.leafia.eventbuses.LeafiaClientListener;
import com.leafia.init.ItemRendererInit;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class LeafiaClientProxy extends LeafiaServerProxy {
	@Override
	public void registerRenderInfo() {
		for (Class<?> cl : LeafiaClientListener.class.getClasses()) {
			try {
				MinecraftForge.EVENT_BUS.register(cl.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LeafiaDevFlaw flaw = new LeafiaDevFlaw(e.getMessage());
				flaw.setStackTrace(e.getStackTrace());
				throw flaw;
			}
		}
        ModelLoader.setCustomStateMapper(AddonBlocks.door_fuckoff, new StateMap.Builder().ignore(BlockDoor.POWERED).build());

		RenderingRegistry.registerEntityRenderingHandler(EntityCloudFleija.class, RenderCloudFleija.FACTORY);
		RenderingRegistry.registerEntityRenderingHandler(LCETorex.class, LCETorexRender.FACTORY);

		RenderingRegistry.registerEntityRenderingHandler(AbsorberShrapnelEntity.class, AbsorberShrapnelRender.FACTORY);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpinnyLight.class,new LCERenderSpinnyLight());

		ClientRegistry.bindTileEntitySpecialRenderer(SPKCableTE.class,new SPKCableRender());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCore.class,new DFCCoreRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoreEmitter.class,new DFCComponentRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoreReceiver.class,new DFCComponentRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoreStabilizer.class,new DFCComponentRender());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoreInjector.class,new DFCComponentRender());
	}
	@Override
	public File getDataDir() {
		return Minecraft.getMinecraft().gameDir;
	}

	@Override
	public LCEAudioWrapper getLoopedSound(SoundEvent sound,SoundCategory cat,float x,float y,float z,float volume,float pitch) {
		LCEAudioWrapperClient audio = new LCEAudioWrapperClient(sound, cat);
		audio.updatePosition(x, y, z);
		return audio;
	}

	@Override
	public LCEAudioWrapper getLoopedSoundStartStop(World world,SoundEvent sound,SoundEvent start,SoundEvent stop,SoundCategory cat,float x,float y,float z,float volume,float pitch) {
		LCEAudioWrapperClientStartStop audio = new LCEAudioWrapperClientStartStop(world, sound, start, stop, volume, cat);
		audio.updatePosition(x, y, z);
		if (pitch != 1)
			audio.updatePitch(pitch);
		return audio;
	}

	@Override
	public void onLoadComplete(FMLLoadCompleteEvent event){
		if (!Loader.isModLoaded("backups")) LeafiaClientListener.HandlerClient.backupsWarning = true;
	}

	@Override
	public void preInit(FMLPreInitializationEvent evt) {
		ItemRendererInit.preInit();
		ItemRendererInit.apply();
	}
}
