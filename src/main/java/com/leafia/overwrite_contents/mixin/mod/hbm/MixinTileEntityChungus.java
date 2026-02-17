package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.DataValue;
import com.hbm.inventory.control_panel.DataValueFloat;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FT_Coolable;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.tileentity.machine.TileEntityChungus;
import com.leafia.contents.gear.utility.IFuzzyCompatible;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = TileEntityChungus.class)
public abstract class MixinTileEntityChungus extends TileEntityLoadedBase implements IFuzzyCompatible, IControllable {
	@Shadow(remap = false) public FluidTankNTM[] tanks;
	@Override
	public FluidType getOutputType() {
		return tanks[1].getTankType();
	}
	@Unique public long[] generateds = new long[20];
	@Unique public int generatedIndex = 0;
	@Inject(method = "update",at = @At(value = "HEAD"),require = 1)
	public void onUpdate(CallbackInfo ci) {
		generatedIndex = Math.floorMod(generatedIndex+1,20);
		generateds[generatedIndex] = 0;
	}
	@Inject(method = "update",at = @At(value = "FIELD", target = "Lcom/hbm/tileentity/machine/TileEntityChungus;power:J",opcode = Opcodes.PUTFIELD,shift = Shift.AFTER,ordinal = 1,remap = false),require = 1)
	public void onAddEnergy(CallbackInfo ci,@Local(name = "ops") int ops,@Local(type = FT_Coolable.class) FT_Coolable trait,@Local(name = "eff") double eff) {
		generateds[generatedIndex] = (long)((double)(ops * trait.heatEnergy) * eff);
	}
	@Inject(method = "invalidate",at = @At(value = "TAIL"))
	public void onInvalidate(CallbackInfo ci) {
		ControlEventSystem.get(world).removeControllable(this);
	}
	@Override
	public void validate() {
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}



	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		float generated = 0;
		for (long gen : generateds)
			generated += gen;
		map.put("generated",new DataValueFloat(generated/20f));
		return map;
	}
	@Override
	public BlockPos getControlPos() {
		return getPos();
	}
	@Override
	public World getControlWorld() {
		return getWorld();
	}
}
