package com.leafia.contents.machines.panel.controltorch;

import com.hbm.inventory.control_panel.*;
import com.leafia.contents.AddonBlocks;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.block.BlockTorch;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlTorchTE extends TileEntity implements IControllable {
	boolean isOn;
	boolean loadedByWorld;
	ControlTorchTE(boolean isOn) {
		this.isOn = isOn;
		loadedByWorld = false;
	}
	public ControlTorchTE() {
		isOn = false;
		loadedByWorld = true;
	}

	@Override
	public List<String> getInEvents() {
		return Collections.singletonList("torch_set_state");
	}

	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("isOn",new DataValueFloat(isOn ? 1 : 0));
		return map;
	}

	@Override
	public void receiveEvent(BlockPos from,ControlEvent e) {
		if (e.name.equals("torch_set_state")) {
			boolean newState = e.vars.get("isOn").getNumber() >= 1f;
			if (newState != isOn || loadedByWorld)
				world.setBlockState(pos, (newState ? AddonBlocks.control_torch : AddonBlocks.control_torch_unlit).getDefaultState().withProperty(BlockTorch.FACING, world.getBlockState(pos).getValue(BlockTorch.FACING)), 3);
		}
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
	public void invalidate() {
		ControlEventSystem.get(world).removeControllable(this);
		super.invalidate();
	}

	@Override
	public void validate() {
		ControlEventSystem.get(world).addControllable(this);
		super.validate();
		if (loadedByWorld) {
			LeafiaPassiveServer.queueFunction(()->{
				isOn = world.getBlockState(pos).getBlock() == AddonBlocks.control_torch;
				loadedByWorld = false;
			});
		}
	}
}
