package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.leafia.contents.AddonFluids;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Fluids.class)
public class MixinFluids {
	@Inject(method = "registerForgeFluidCompat",at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidRegistry;registerFluid(Lnet/minecraftforge/fluids/Fluid;)Z", shift = Shift.AFTER),remap = false,require = 1)
	private static void onInitForgeFluidCompat(CallbackInfo ci,@Local(type = FluidType.class) FluidType fluid) {
		AddonFluids.addCompatFluid(fluid);
	}
	@Redirect(method = "alreadyRegistered",at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidRegistry;getFluid(Ljava/lang/String;)Lnet/minecraftforge/fluids/Fluid;"),remap = false,require = 1)
	private static Fluid onGetFF(String s,@Local(type = FluidType.class) FluidType fluid) {
		Fluid existingFluid = FluidRegistry.getFluid(s);
		if (existingFluid != null)
			AddonFluids.addCompatFluid(fluid);
		return existingFluid;
	}
}
