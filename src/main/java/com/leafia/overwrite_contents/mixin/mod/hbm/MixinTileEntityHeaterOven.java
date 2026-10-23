// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.tileentity.machine.TileEntityFireboxBase;
import com.hbm.tileentity.machine.TileEntityHeaterOven;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = TileEntityHeaterOven.class)
public abstract class MixinTileEntityHeaterOven extends TileEntityFireboxBase implements IControllable {
	@Shadow(remap = false) public static int maxHeatEnergy;
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("heat",new DataValueFloat(heatEnergy));
		map.put("maxHeat",new DataValueFloat(maxHeatEnergy));
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
	@Override
	public void validate() {
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}
	@Override
	public void invalidate() {
		ControlEventSystem.get(world).removeControllable(this);
		super.invalidate();
	}
}
