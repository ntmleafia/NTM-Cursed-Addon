package com.custom_hbm.sound;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class LCEAudioWrapperClientStartStop extends LCEAudioWrapperClient {

	public SoundEvent start;
	public SoundEvent stop;
	public World world;

	public LCEAudioWrapperClientStartStop(World world,SoundEvent source,SoundEvent start,SoundEvent stop,float vol,SoundCategory cat){
		super(source, cat);
		updateVolume(vol);
		this.world = world;
		this.start = start;
		this.stop = stop;
	}

	@Override
	public LCEAudioWrapperClientStartStop startSound(){
		super.startSound();
		if(start != null)
			world.playSound(x, y, z, start, category, volume, getPitch(), false);
		return this;
	}

	@Override
	public LCEAudioWrapperClientStartStop stopSound(){
		if(stop != null && sound != null && sound.isPlaying())
			world.playSound(x, y, z, stop, category, volume, getPitch(), false);
		super.stopSound();
		return this;
	}
}
