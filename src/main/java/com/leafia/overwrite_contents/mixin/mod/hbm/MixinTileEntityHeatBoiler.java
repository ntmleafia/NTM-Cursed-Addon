package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.machine.TileEntityHeatBoiler;
import com.leafia.contents.gear.utility.fuzzy.IFuzzyCompatible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TileEntityHeatBoiler.class)
public class MixinTileEntityHeatBoiler implements IFuzzyCompatible {
	@Shadow(remap = false) public FluidTankNTM[] tanks;

	@Override
	public FluidType getOutputType() {
		return tanks[1].getTankType();
	}
}
