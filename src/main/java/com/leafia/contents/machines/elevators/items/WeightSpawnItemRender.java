package com.leafia.contents.machines.elevators.items;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.elevators.car.ElevatorRender;
import com.leafia.contents.machines.elevators.weight.EvWeightRender;
import com.leafia.dev.LeafiaItemRenderer;
import net.minecraft.util.ResourceLocation;

public class WeightSpawnItemRender extends LeafiaItemRenderer {
	@Override
	protected double _sizeReference() {
		return 4.2;
	}
	@Override
	protected double _itemYoffset() {
		return -0.22;
	}
	@Override
	protected ResourceLocation __getTexture() {
		return ElevatorRender.support;
	}
	@Override
	protected WaveFrontObjectVAO __getModel() {
		return EvWeightRender.mdl;
	}
}
