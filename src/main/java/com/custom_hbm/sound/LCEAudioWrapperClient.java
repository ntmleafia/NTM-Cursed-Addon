package com.custom_hbm.sound;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiFunction;

@SideOnly(Side.CLIENT)
public class LCEAudioWrapperClient extends LCEAudioWrapper {

	LCEAudioDynamic sound;
	
	public LCEAudioWrapperClient(SoundEvent source,SoundCategory cat) {
		if(source != null)
			sound = new LCEAudioDynamic(source, cat);
	}
	
	public LCEAudioWrapperClient updatePosition(float x,float y,float z) {
		if(sound != null)
			sound.setPosition(x, y, z);
		return this;
	}
	
	public LCEAudioWrapperClient updateVolume(float volume) {
		if(sound != null)
			sound.setVolume(volume);
		return this;
	}
	
	public LCEAudioWrapperClient updatePitch(float pitch) {
		if(sound != null)
			sound.setPitch(pitch);
		return this;
	}
	
	public float getVolume() {
		if(sound != null)
			return sound.getVolume();
		else
			return 1;
	}
	
	public float getPitch() {
		if(sound != null)
			return sound.getPitch();
		else
			return 1;
	}
	
	public LCEAudioWrapperClient startSound() {
		if(sound != null)
			sound.start();
		return this;
	}
	
	public LCEAudioWrapperClient stopSound() {
		if(sound != null)
			sound.stop();
		return this;
	}

	public LCEAudioWrapperClient setLooped(boolean looped) {
		sound.setLooped(looped);
		return this;
	}

	@Override
	public LCEAudioWrapperClient setCustomAttentuation(BiFunction<Float,Double,Double> attentuationFunction) {
		sound.setCustomAttentuation(attentuationFunction);
		return this;
	}
}
