// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.network.FluidDuctGauge.TileEntityPipeGauge;
import com.hbm.blocks.network.energy.BlockCableGauge.TileEntityCableGauge;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.inventory.control_panel.IControllable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(TileEntityCableGauge.class)
public class MixinTileEntityCableGauge extends TileEntity implements IControllable {
	@Shadow(remap = false)
	private long deltaTick;
	@Shadow(remap = false)
	public long deltaLastSecond;
	@Override
	public BlockPos getControlPos() {
		return getPos();
	}
	@Override
	public World getControlWorld() {
		return getWorld();
	}
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("perTick",new DataValueFloat(deltaTick));
		map.put("perSecond",new DataValueFloat(deltaLastSecond));
		return map;
	}
	@Override
	public void validate() {
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}
	@Override
	public void invalidate() {
		super.invalidate();
		ControlEventSystem.get(world).removeControllable(this);
	}
}
