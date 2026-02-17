package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.machine.TileEntityHeatBoilerIndustrial;
import com.leafia.contents.gear.utility.IFuzzyCompatible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityHeatBoilerIndustrial.class)
public class MixinTileEntityHeatBoilerIndustrial implements IFuzzyCompatible {
	@Shadow(remap = false) public FluidTankNTM[] tanks;

	//@Shadow(remap = false)
	//public static int maxHeat;

	@Inject(method = "<init>",at = @At(value = "TAIL"),require = 1,remap = false)
	public void onInit(CallbackInfo ci) {
		this.tanks[0] = new FluidTankNTM(Fluids.WATER, 64_000*5);
		this.tanks[1] = new FluidTankNTM(Fluids.STEAM, 64_000*5 * 100);
	}

	@Override
	public FluidType getOutputType() {
		return tanks[1].getTankType();
	}

	/*@Redirect(method = "tryPullHeat",at = @At(value = "FIELD", target = "Lcom/hbm/tileentity/machine/TileEntityHeatBoilerIndustrial;maxHeat:I"),require = 1,remap = false)
	protected int onTryPullHeat() {
		return maxHeat*3;
	}

	@Redirect(method = "update",at = @At(value = "FIELD", target = "Lcom/hbm/tileentity/machine/TileEntityHeatBoilerIndustrial;maxHeat:I"),require = 1,remap = false)
	protected int onUpdate() {
		return maxHeat*3;
	}*/
}
