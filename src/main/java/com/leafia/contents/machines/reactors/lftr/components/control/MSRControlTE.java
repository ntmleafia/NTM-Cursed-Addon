package com.leafia.contents.machines.reactors.lftr.components.control;

import com.hbm.blocks.generic.BlockPipe;
import com.hbm.inventory.control_panel.*;
import com.leafia.contents.AddonBlocks.LFTR;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE;
import com.leafia.dev.LeafiaDebug.Tracker;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.llib.group.LeafiaSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSRControlTE extends TileEntity implements ITickable, LeafiaPacketReceiver, IControllable {
	public double insertion = 0;
	public double targetInsertion = 0;
	public int length = 0;

	@Override
	public String getPacketIdentifier() {
		return "MSRControl";
	}

	public EnumFacing getDirection() {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof MSRControlBlock)
			return state.getValue(MSRControlBlock.FACING);
		return EnumFacing.NORTH;
	}

	public int getExtensionLength(EnumFacing facing) {
		Tracker._startProfile(this,"getExtensionLength");
		facing = facing.getOpposite();
		BlockPos pos = this.pos;
		pos = pos.add(facing.getDirectionVec());
		int length = 0;
		while (world.isValid(pos)) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == LFTR.extension) {
				if (state.getValue(BlockPipe.AXIS).equals(facing.getAxis())) {
					pos = pos.add(facing.getDirectionVec());
					length++;
					continue;
				}
			}
			break;
		}
		Tracker._endProfile(this);
		return length;
	}

	public static final double speed = 0.1/20;
	LeafiaSet<MSRElementTE> TEs = new LeafiaSet<>();
	void clearTEs() {
		for (MSRElementTE te : TEs) {
			if (te.control != null && te.control.equals(getPos())) {
				te.control = null;
				te.restriction = 0;
			}
		}
		TEs.clear();
	}
	@Override
	public void update() {
		if (!world.isRemote) {
			EnumFacing direction = getDirection();
			length = getExtensionLength(direction);
			if (targetInsertion != insertion) {
				if (Math.abs(targetInsertion - insertion) < speed)
					insertion = targetInsertion;
				else {
					insertion += Math.signum(targetInsertion - insertion) * speed;
				}
				clearTEs();
				int actualLength = 0;
				for (int i = 1; i <= length; i++) {
					BlockPos pos1 = pos.offset(direction,i);
					if (world.getTileEntity(pos1) instanceof MSRElementTE te) {
						if (te.control == null || te.control.equals(getPos())) {
							if (Math.ceil(insertion) >= i) {
								te.control = getPos();
								TEs.add(te);
								te.restriction = MathHelper.clamp(insertion-i+1,0,1);
							}
							actualLength++;
						} else
							break;
					}
				}
				if (insertion > actualLength) {
					insertion = actualLength;
				}
				//syncLocals();
				this.markDirty();
			}
			LeafiaPacket._start(this)
					.__write(0,insertion)
					.__sendToAffectedClients();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		insertion = compound.getDouble("rodI");
		targetInsertion = compound.getDouble("rodD");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("rodI",insertion);
		compound.setDouble("rodD",targetInsertion);
		return super.writeToNBT(compound);
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0:
				insertion = (double)value;
				break;
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }

	@Override
	public BlockPos getControlPos() {
		return pos;
	}

	@Override
	public World getControlWorld() {
		return world;
	}

	@Override
	public void validate(){
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
	}

	@Override
	public void invalidate(){
		//if (!(world.getBlockState(pos).getBlock() instanceof MSRControlBlock))
		//	clearTEs(); fuck off asshole
		super.invalidate();
		ControlEventSystem.get(world).removeControllable(this);
	}

	@Override
	public void receiveEvent(BlockPos from,ControlEvent e) {
		if (e.name.equals("lftr_ctrl_set_level")) {
			double position = MathHelper.clamp(e.vars.get("level").getNumber()/100d,0,1);
			targetInsertion = length-position*length;
		}
	}
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		double position = length > 0 ? 1-insertion/length : 0;
		map.put("level",new DataValueFloat((float)(position*100)));
		return map;
	}

	@Override
	public List<String> getInEvents() {
		return Collections.singletonList("lftr_ctrl_set_level");
	}
}
