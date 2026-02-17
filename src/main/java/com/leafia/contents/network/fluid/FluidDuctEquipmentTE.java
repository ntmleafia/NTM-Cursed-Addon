package com.leafia.contents.network.fluid;

import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public abstract class FluidDuctEquipmentTE extends TileEntityPipeBaseNT implements LeafiaPacketReceiver {
	public EnumFacing direction = EnumFacing.NORTH;
	public boolean vertical = false;
	public int face = 0;

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 29: direction = EnumFacing.byHorizontalIndex((int)value); break;
			case 30: vertical = (boolean)value; break;
			case 31: face = (int)value; break;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		direction = EnumFacing.byHorizontalIndex(compound.getInteger("direction"));
		vertical = compound.getBoolean("vertical");
		face = compound.getByte("face");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("direction",direction.getHorizontalIndex());
		compound.setBoolean("vertical",vertical);
		compound.setByte("face",(byte)face);
		return super.writeToNBT(compound);
	}

	public LeafiaPacket startPacket() {
		return LeafiaPacket._start(this).__write(29,direction.getHorizontalIndex()).__write(30,vertical).__write(31,face);
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }

	@Override
	public double affectionRange() {
		return 256;
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		startPacket().__sendToClient(plr);
	}
	// since shit can't have more than 16 metadatas, here we are some shit
}
