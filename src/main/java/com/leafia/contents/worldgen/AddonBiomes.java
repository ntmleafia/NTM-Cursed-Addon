// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.worldgen;

import com.leafia.contents.worldgen.biomes.Barrens;
import com.leafia.contents.worldgen.biomes.Desolation;
import com.leafia.contents.worldgen.biomes.Ruins;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class AddonBiomes {
	public static final List<AddonBiome> ALL_BIOMES = new ArrayList<>();

	public static final AddonBiome barrens = new Barrens("barrens");
	public static final AddonBiome desolation = new Desolation("outer_barrens");
	public static final AddonBiome ruins = new Ruins("iturnedmcto7dtd");

	public static final AddonBiome digamma = new DigammaCrater("digamma_crater");

	public static void init(){
		for (AddonBiome biome : ALL_BIOMES) {
			ForgeRegistries.BIOMES.register(biome);
			biome.postInit.run();
		}
	}
}
