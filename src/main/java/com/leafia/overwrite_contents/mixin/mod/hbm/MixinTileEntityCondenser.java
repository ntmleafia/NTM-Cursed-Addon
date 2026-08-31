package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.tileentity.machine.TileEntityCondenser;
import com.leafia.contents.gear.utility.fuzzy.IFuzzyCompatible;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TileEntityCondenser.class)
public class MixinTileEntityCondenser implements IFuzzyCompatible {
	@Override
	public FluidType getOutputType() {
		return Fluids.WATER;
	}
}
