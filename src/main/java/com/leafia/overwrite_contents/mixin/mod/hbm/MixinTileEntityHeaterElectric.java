// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlEvent;
import com.hbm.inventory.control_panel.ControlEventSystem;
import com.hbm.inventory.control_panel.IControllable;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.tileentity.machine.TileEntityHeaterElectric;
import com.leafia.dev.LeafiaDebug;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = TileEntityHeaterElectric.class)
public class MixinTileEntityHeaterElectric extends TileEntity implements IControllable {
	@Override
	public List<String> getInEvents() {
		return Collections.singletonList("heater_set");
	}
	@Override
	public void receiveEvent(BlockPos from,ControlEvent e) {
		if (e.name.equals("heater_set")) {
			float set = e.vars.get("heatPerTick").getNumber()/100;
			setting = MathHelper.clamp((int)Math.ceil(set),0,10);
		}
	}

	@Shadow(remap = false) public int heatEnergy;
	@Shadow(remap = false) protected int setting;

	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("heat",new DataValueFloat(heatEnergy));
		map.put("maxHeat",new DataValueFloat(2147483647));
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
	@Inject(method = "invalidate",at = @At(value = "HEAD"),require = 1)
	void leafia$onInvalidate(CallbackInfo ci) {
		ControlEventSystem.get(world).removeControllable(this);
	}
}
