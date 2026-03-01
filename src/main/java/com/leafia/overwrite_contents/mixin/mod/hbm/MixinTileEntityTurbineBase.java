package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.DataValue;
import com.hbm.inventory.control_panel.DataValueFloat;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.tileentity.machine.TileEntityTurbineBase;
import com.leafia.contents.gear.utility.IFuzzyCompatible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = TileEntityTurbineBase.class)
public abstract class MixinTileEntityTurbineBase extends TileEntityLoadedBase implements IFuzzyCompatible, IControllable {
	@Shadow(remap = false) public FluidTankNTM[] tanks;
	@Shadow(remap = false)
	public long powerBuffer;

	@Override
	public FluidType getOutputType() {
		return tanks[1].getTankType();
	}
	@Unique public long[] leafia$generateds = new long[20];
	@Unique public int leafia$generatedIndex = 0;
	@Inject(method = "update",at = @At(value = "TAIL"),require = 1)
	public void leafia$onUpdate(CallbackInfo ci) {
		leafia$generatedIndex = Math.floorMod(leafia$generatedIndex+1,20);
		leafia$generateds[leafia$generatedIndex] = 0;
		leafia$generateds[leafia$generatedIndex] = powerBuffer;
	}

	@Override
	public void invalidate() {
		super.invalidate();
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
		for (long gen : leafia$generateds)
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
