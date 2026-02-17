package com.leafia.contents.machines.reactors.pwr.blocks.components;

import com.leafia.contents.machines.reactors.pwr.PWRData;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public abstract class PWRAssignableEntity extends TileEntity implements ITickable, PWRComponentEntity, LeafiaPacketReceiver {
	protected BlockPos corePos = null;
	protected PWRData data = null;

	@Override
	public boolean canAssignCore() {
		return true;
	}

	@Override
	public void setCoreLink(@Nullable BlockPos pos) {
		corePos = pos;
	}

	@Override
	public PWRData getLinkedCore() {
		return PWRComponentEntity.getCoreFromPos(world,corePos);
	}

	@Override
	public void assignCore(@Nullable PWRData data) {
		if (this.data != data) {
			PWRData.addDataToPacket(LeafiaPacket._start(this),data).__sendToAffectedClients();
		}
		this.data = data;
	}
	@Override
	public PWRData getCore() {
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("corePosX"))
			corePos = new BlockPos(
					compound.getInteger("corePosX"),
					compound.getInteger("corePosY"),
					compound.getInteger("corePosZ")
			);
		super.readFromNBT(compound);
		if (compound.hasKey("data")) { // DO NOT MOVE THIS ABOVE SUPER CALL! super.readFromNBT() is where this.pos gets initialized!!
			data = new PWRData(this);
			data.readFromNBT(compound);
			//new PWRDiagnosis(world).addPosition(pos);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (corePos != null) {
			compound.setInteger("corePosX",corePos.getX());
			compound.setInteger("corePosY",corePos.getY());
			compound.setInteger("corePosZ",corePos.getZ());
		}
		if (data != null) {
			data.writeToNBT(compound);
		}
		return super.writeToNBT(compound);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (this.data != null)
			this.data.invalidate(world);
	}

	@Override
	public void update() {
		if (this.data != null)
			this.data.update();
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 31)
			data = PWRData.tryLoadFromPacket(this,value);
		if (this.data != null)
			this.data.onReceivePacketLocal(key,value);
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (this.data != null)
			this.data.onReceivePacketServer(key,value,plr);
	}

	protected LeafiaPacket addDataToPacket(LeafiaPacket packet) {
		if (this.data != null)
			PWRData.addDataToPacket(packet,this.data);
		return packet;
	}
}
