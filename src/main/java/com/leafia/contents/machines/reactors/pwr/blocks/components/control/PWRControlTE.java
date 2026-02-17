package com.leafia.contents.machines.reactors.pwr.blocks.components.control;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.inventory.control_panel.*;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRAssignableEntity;
import com.leafia.dev.LeafiaDebug.Tracker;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.init.LeafiaSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class PWRControlTE extends PWRAssignableEntity implements ITickable, IControllable {
	public PWRControlTE() {
		super();
	}
	public int height = 1;
	public double position = 0;
	public String name = defaultName;
	public static final String defaultName = "00.Unsorted";
	LCEAudioWrapper sound = null;

	public double speed = -1;
	public double targetPosition = 0;
	List<Block> bluk = new ArrayList<>();
	boolean initialTick = true;

	public void updateHeight() {
		if (!this.isInvalid() && world.isBlockLoaded(pos)) {
			Chunk chunk = world.getChunk(pos);
			BlockPos downPos = pos.down();
			height = 1;
			bluk.clear();
			while (world.isValid(downPos)) {
				bluk.add(world.getBlockState(downPos).getBlock());
				if (world.getBlockState(downPos).getBlock() instanceof PWRControlBlock) {
					height++;
					if (world.isRemote) { // manually kill TEs below
						TileEntity entity = chunk.getTileEntity(downPos,Chunk.EnumCreateEntityType.CHECK);
						if (entity != null) {
							if (entity instanceof PWRControlTE) {
								((PWRControlTE)entity).connectUpper();
							}
						}
					}
				} else
					break;
				downPos = downPos.down();
			}
		}
	}

	int lastBBHeight = 0;
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (lastBBHeight != height) {
			bb = null;
			lastBBHeight = height;
		}
		if (bb == null) {
			bb = new AxisAlignedBB(
					pos.getX(),
					pos.getY()+1-height,
					pos.getZ(),
					pos.getX()+1,
					pos.getY()+1,
					pos.getZ()+1
			);
		}
		return bb;
	}

	public void connectUpper() { // For clients, called only on validate()
		if (!this.isInvalid() && world.isBlockLoaded(pos)) {
			if (world.getBlockState(pos.up()).getBlock() instanceof PWRControlBlock)
				this.invalidate();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("rodP"))
			position = compound.getDouble("rodP");
		if (compound.hasKey("rodD"))
			targetPosition = compound.getDouble("rodD");
		if (compound.hasKey("name"))
			name = compound.getString("name");
		super.readFromNBT(compound);
	}

	public NBTTagCompound writeControlDateToNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("rodP", position);
		nbt.setDouble("rodD", targetPosition);
		nbt.setString("name", name);
		nbt.setInteger("x", pos.getX());
		nbt.setInteger("y", pos.getY());
		nbt.setInteger("z", pos.getZ());
		return nbt;
	}

	public void readControlDataFromNBT(NBTTagCompound nbt)
	{
		position = nbt.getDouble("rodP");
		targetPosition = nbt.getDouble("rodD");
		name = nbt.getString("name");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble("rodP",position);
		compound.setDouble("rodD",targetPosition);
		compound.setString("name",name);
		return super.writeToNBT(compound);
	}

	@Override
	public void invalidate() {
		if (sound != null)
			sound.stopSound();
		sound = null;
		ControlEventSystem.get(world).removeControllable(this);
		super.invalidate();
	}

	@Override
	public void validate() {
		super.validate();
		ControlEventSystem.get(world).addControllable(this);
		//if (world.isRemote) {
		//LeafiaPacket._validate(this); //LeafiaPacket._start(this).__write((byte)0,true).__setTileEntityQueryType(Chunk.EnumCreateEntityType.CHECK).__sendToServer();
		//}
	}

	@Override
	public void onChunkUnload() {
		if (sound != null) {
			sound.stopSound();
		}
		sound = null;
		super.onChunkUnload();
	}

	@Override
	public void update() {
		if (initialTick) {
			initialTick = false;
			connectUpper();
			updateHeight();
		}
		super.update();
		if (speed <= 0) { // check block type
			Block block = world.getBlockState(pos).getBlock();
			if (block == PWR.control)
				speed = 0.22/20;
			else
				speed = 0.1/20;
		}
		if (world.isRemote) {
			if (!(world.getBlockState(pos.up()).getBlock() instanceof PWRControlBlock)) {
				if (sound != null) {
					if (targetPosition == position) {
						sound.stopSound();
						sound = null;
					}
				} else if (sound == null) {
					if (targetPosition != position) {
						sound = AddonBase.proxy.getLoopedSoundStartStop(world,LeafiaSoundEvents.pwrRodLoop,LeafiaSoundEvents.pwrRodStart,LeafiaSoundEvents.pwrRodStop,SoundCategory.BLOCKS,pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,0.0175f,0.75f);
						sound.startSound();
					}
				}
			}
		} else {
			Tracker._startProfile(this,"update");
			if (targetPosition != position) {
				if (Math.abs(targetPosition - position) < speed/height)
					position = targetPosition;
				else {
					position += Math.signum(targetPosition - position) * speed/height;
				}
				syncLocals();
				this.markDirty();
			}
			int offset = 1;
			for (Block block : bluk) {
				Tracker._tracePosition(this,pos.down(offset),block);
				offset++;
			}
			Tracker._endProfile(this);
		}
	}
	public LeafiaPacket generateSyncPacket() {
		return LeafiaPacket._start(this).__write(0,position).__write(1,targetPosition);
	}
	public void syncLocals() {
		generateSyncPacket().__sendToAffectedClients();//.__setTileEntityQueryType(Chunk.EnumCreateEntityType.CHECK).__sendToAllInDimension();
	}
	@Override
	public String getPacketIdentifier() {
		return "PWR_CONTROL";
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0:
				position = (double)value;
				break;
			case 1:
				targetPosition = (double)value;
				break;
			case 2:
				name = (String)value;
				break;
		}
		super.onReceivePacketLocal(key,value);
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {/*
        if (key == 0) {
            generateSyncPacket().__sendToClient(plr);
        }*/
		super.onReceivePacketServer(key,value,plr);
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket packet = generateSyncPacket().__write(!name.equals(defaultName) ? 2 : -1,name);
		addDataToPacket(packet);
		packet.__sendToClient(plr);
	}

	@Override
	public BlockPos getControlPos() {
		return pos;
	}

	@Override
	public World getControlWorld() {
		return world;
	}

	@Override
	public void receiveEvent(BlockPos from,ControlEvent e) {
		if (e.name.equals("pwr_ctrl_set_level"))
			targetPosition = MathHelper.clamp(e.vars.get("level").getNumber()/100d,0,1);
	}
	@Override
	public Map<String,DataValue> getQueryData() {
		Map<String,DataValue> map = new HashMap<>();
		map.put("level",new DataValueFloat((float)(position*100)));
		return map;
	}

	@Override
	public List<String> getInEvents() {
		return Collections.singletonList("pwr_ctrl_set_level");
	}
}
