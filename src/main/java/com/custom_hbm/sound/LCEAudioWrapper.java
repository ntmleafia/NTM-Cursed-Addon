// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.sound;

import java.util.function.BiFunction;

public class LCEAudioWrapper {
	
	public LCEAudioWrapper updatePosition(float x,float y,float z) { return this; }
	
	public LCEAudioWrapper updateVolume(float volume) { return this; }
	
	public LCEAudioWrapper updatePitch(float pitch) { return this; }
	
	public float getVolume() { return 0F; }
	
	public float getPitch() { return 0F; }
	
	public LCEAudioWrapper startSound() { return this; }
	
	public LCEAudioWrapper stopSound() { return this; }

	public LCEAudioWrapper setCustomAttenuation(BiFunction<Float,Double,Double> attentuationFunction) { return this; }

	public LCEAudioWrapper setLooped(boolean looped) { return this; }
}