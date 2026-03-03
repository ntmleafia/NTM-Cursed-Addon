package com.leafia.contents.miscellanous.diverter;

import com.leafia.contents.AddonBlocks;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.savedata.PlayerDeathsSavedData;
import com.leafia.settings.AddonConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import java.util.Map.Entry;

public class DiverterTE extends TileEntity implements ITickable, LeafiaPacketReceiver {
	String placerName = "";
	long timeRemaining = 124384;
	boolean isError = false;
	/// actually is this necessary?
	void replaceBlock(boolean state) {
		boolean curState = world.getBlockState(pos).getBlock() == AddonBlocks.diverter;
		if (curState != state) {
			world.setBlockState(pos,state ? AddonBlocks.diverter.getDefaultState() : AddonBlocks.diverter_unlit.getDefaultState(),2);
			this.validate();
			world.setTileEntity(pos,this);
		}
	}
	PlayerDeathsSavedData data;
	@Override
	public void update() {
		if (!world.isRemote) {
			isError = placerName.isEmpty();
			if (data == null)
				data = PlayerDeathsSavedData.forWorld(world);
			long timestamp = data.timestamps.getOrDefault(placerName,0L);
			timeRemaining = 20L*AddonConfig.meteorDiverterMinAliveTime-(world.getTotalWorldTime()-timestamp);
			if (isError)
				timeRemaining = Math.max(timeRemaining,1);
			boolean active = timeRemaining <= 0;
			replaceBlock(active);
			LeafiaPacket._start(this)
					.__write(0,timeRemaining)
					.__write(1,isError)
					.__write(2,placerName)
					.__sendToAffectedClients();
			world.markChunkDirty(this.pos, this);
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("placer",placerName);
		return super.writeToNBT(compound);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("placer"))
			placerName = compound.getString("placer");
	}

	@Override
	public String getPacketIdentifier() {
		return "DIVERTER";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0)
			timeRemaining = (long)value;
		else if (key == 1)
			isError = (boolean)value;
		else if (key == 2)
			placerName = (String)value;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
}
