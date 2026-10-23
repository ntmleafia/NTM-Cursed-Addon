// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.energymk2.IEnergyReceiverMK2.ConnectionPriority;
import com.hbm.blocks.network.FluidPump.TileEntityFluidPump;
import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(value = TileEntityFluidPump.class)
public class MixinTileEntityFluidPump extends TileEntityLoadedBase implements IControllable {
	@Shadow(remap = false)
	public int bufferSize;
	@Shadow(remap = false)
	public FluidTankNTM[] tank;
	@Shadow(remap = false)
	public ConnectionPriority priority;
	@Override
	public BlockPos getControlPos() {
		return getPos();
	}
	@Override
	public World getControlWorld() {
		return getWorld();
	}
	@Override
	public void receiveEvent(BlockPos from, ControlEvent e) {
		switch(e.name) {
			case "set_pump_buffer" ->
					bufferSize = MathHelper.clamp(Math.round(e.vars.get("size").getNumber()),0,10_000);
			case "set_pump_pressure" ->
					tank[0].withPressure(MathHelper.clamp(Math.round(e.vars.get("pu").getNumber()),0,5));
			case "set_pump_priority" ->
					priority = ConnectionPriority.VALUES[MathHelper.clamp(Math.round(e.vars.get("priority").getNumber()),0,ConnectionPriority.VALUES.length-1)];
		}
	}
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("fill",new DataValueFloat(tank[0].getFill()));
		return map;
	}
	@Override
	public List<String> getInEvents() {
		return Arrays.asList("set_pump_buffer","set_pump_pressure","set_pump_priority");
	}
	@Override
	public void validate(){
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}
	@Override
	public void invalidate(){
		super.invalidate();
		ControlEventSystem.get(world).removeControllable(this);
	}
}
