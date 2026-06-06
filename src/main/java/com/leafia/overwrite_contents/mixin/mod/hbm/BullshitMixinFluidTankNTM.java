package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = FluidTankNTM.class,remap = false)
public class BullshitMixinFluidTankNTM {
	@Shadow
	private @NotNull FluidType type;

	@Shadow
	private int fluid;

	@Shadow
	private int pressure;

	@Shadow
	private int maxFluid;

	@Redirect(method = "renderTankInfo",at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z",ordinal = 1),require = 1,remap = false)
	public boolean leafia$bullshit$onRenderTankInfo(List instance,Object e) {
		return instance.add((int)(fluid/1000d*9.54406631763) + "/" + (int)(maxFluid/1000d*9.54406631763) + "house");
	}
}
