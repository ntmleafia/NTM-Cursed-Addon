package com.leafia.contents.machines.processing.solblaster;

import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class SolBlasterTE extends LCETileEntityMachineBase implements ITickable, LeafiaPacketReceiver {
	public int doorPos = 0;
	public SolBlasterTE() {
		super(20);
	}
	@Override
	public void update() {

	}
	@Override
	public String getDefaultName() {
		return "tile.solblaster.name";
	}
	@Override
	public String getPacketIdentifier() {
		return "SOL_BLAST";
	}
	public enum SolBlasterPackets {
		DOOR_SYNC;
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == SolBlasterPackets.DOOR_SYNC.ordinal())
			doorPos = (int)value;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 3,
					pos.getY(),
					pos.getZ() - 3,
					pos.getX() + 4,
					pos.getY() + 6,
					pos.getZ() + 4
			);
		}
		return bb;
	}
}
