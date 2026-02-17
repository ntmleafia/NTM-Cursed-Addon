package com.leafia.contents.machines.elevators.floors;

import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks.Elevators;
import com.hbm.main.MainRegistry;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.llib.technical.FiaLatch;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class EvFloorTE extends TileEntity implements ITickable,LeafiaPacketReceiver {
	static final byte idGui = 31;
	static final byte idFloor = 0;
	static final byte idOpen = 1;

	public Block paintBlock = null;
	public int paintMeta = 0;
	public LeafiaPacket writePaint(LeafiaPacket packet) {
		if (paintBlock != null) {
			packet.__write(25,paintBlock.getRegistryName().toString()).__write(26,paintMeta);
		}
		return packet;
	}
	public void syncPaint() {
		writePaint(LeafiaPacket._start(this)).__sendToAffectedClients();
	}

	public int floor = 1;
	public FiaLatch<Float> open = new FiaLatch<>(0f);
	public void openGui(EntityPlayer player) {
		LeafiaPacket._start(this).__write(idFloor,floor).__write(idGui,0).__sendToClient(player);
	}
	@Override
	public String getPacketIdentifier() {
		return "EV_FLOOR";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case idFloor: floor = (int)value; break;
			case idGui: Minecraft.getMinecraft().player.openGui(AddonBase.instance,Elevators.guiIdFloor,world,pos.getX(),pos.getY(),pos.getZ()); break;
			case idOpen: open.set((float)value).update(); break;
			case 25:
				paintBlock = Block.getBlockFromName((String)value);
				break;
			case 26:
				paintMeta = (int)value;
				markDirty();
				world.markChunkDirty(pos,this);
				world.notifyBlockUpdate(pos,world.getBlockState(pos),world.getBlockState(pos),3);
				break;
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == 0) {
			floor = (int)value;
			LeafiaPacket._start(this).__write(idFloor,floor).__sendToAffectedClients();
		}
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		writePaint(LeafiaPacket._start(this).__write(idOpen,open.cur)).__sendToClient(plr);
	}
	@Override
	public double affectionRange() {
		return 1024;
	}

	@Override
	public void update() {
		if (open.needsUpdate()) {
			LeafiaDebug.debugLog(world,"fuck you "+open.cur);
			LeafiaPacket._start(this).__write(idOpen,open.update()).__sendToAffectedClients();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte("floor",(byte)floor);
		if (paintBlock != null) {
			compound.setString("paintBlock",paintBlock.getRegistryName().toString());
			compound.setByte("paintMeta",(byte)paintMeta);
		}
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.floor = compound.getByte("floor");
		if (compound.hasKey("paintBlock")) {
			this.paintBlock = Block.getBlockFromName(compound.getString("paintBlock"));
			this.paintMeta = compound.getByte("paintMeta");
		}
	}
}
