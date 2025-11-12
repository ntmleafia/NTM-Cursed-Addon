package com.leafia.init.proxy;

import com.custom_hbm.sound.LCEAudioWrapper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

import java.io.File;

public class LeafiaServerProxy {
	public void registerRenderInfo() {};
	public File getDataDir(){
		return FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
	}

	public LCEAudioWrapper getLoopedSound(SoundEvent sound,SoundCategory cat,float x,float y,float z,float volume,float pitch) { return null; }

	public LCEAudioWrapper getLoopedSoundStartStop(World world,SoundEvent sound,SoundEvent start,SoundEvent stop,SoundCategory cat,float x,float y,float z,float volume,float pitch){return null;}

	public void onLoadComplete(FMLLoadCompleteEvent event){}
}
