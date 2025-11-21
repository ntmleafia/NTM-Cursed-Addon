package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.tileentity.machine.TileEntityChungus;
import com.leafia.contents.gear.utility.IFuzzyCompatible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TileEntityChungus.class)
public abstract class MixinTileEntityChungus extends TileEntityLoadedBase implements IFuzzyCompatible {
	@Shadow(remap = false) public FluidTankNTM[] tanks;
	@Override
	public FluidType getOutputType() {
		return tanks[1].getTankType();
	}
}
