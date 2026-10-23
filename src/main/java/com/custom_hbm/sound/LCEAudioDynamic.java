// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.sound;

import com.leafia.AddonBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiFunction;

@SideOnly(Side.CLIENT)
public class LCEAudioDynamic extends MovingSound {

	public float intendedVolume = 10.0F;
	public BiFunction<Float,Double,Double> attenuationFunction = null;

	protected LCEAudioDynamic(SoundEvent loc,SoundCategory cat) {
		super(loc, cat);
		this.repeat = true;
		this.attenuationType = AttenuationType.NONE;
	}
	
	public void setPosition(float x, float y, float z) {
		this.xPosF = x;
		this.yPosF = y;
		this.zPosF = z;
	}

	public void setAttenuation(AttenuationType type){
		this.attenuationType = type;
	}
	public void setCustomAttenuation(BiFunction<Float,Double,Double> attenuationFunction) {
		this.attenuationFunction = attenuationFunction;
	}

	public void setLooped(boolean repeat) {
		this.repeat = repeat;
	}
	
	@Override
	public void update() {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if(player != null && attenuationFunction != null) {
			double distance = Math.sqrt(Math.pow(xPosF - player.posX, 2) + Math.pow(yPosF - player.posY, 2) + Math.pow(zPosF - player.posZ, 2));
			volume = attenuationFunction.apply(intendedVolume, distance).floatValue();
		} else {
			volume = intendedVolume;
		}
	}
	
	public void start() {
		try {
			Minecraft.getMinecraft().getSoundHandler().playSound(this);
        } catch (IllegalArgumentException ex) {
            AddonBase.LOGGER.warn(ex);
        }
	}

    public boolean isPlaying() {
        return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this);
    }
	
	public void stop() {
		Minecraft.getMinecraft().getSoundHandler().stopSound(this);
	}
	
	public void setVolume(float volume) {
		this.intendedVolume = volume;
		this.volume = volume;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
