package com.leafia.contents.machines.reactors.rbmk.debris;

import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RBMKDebrisSmokeTE extends TileEntity implements ITickable, LeafiaPacketReceiver {
	float scale = 0;
	public void setScale(float scale) {
		this.scale = scale;
		if (getWorld() != null)
			LeafiaPacket._start(this).__write(0,this.scale).__sendToClients(4096);
		else {
			LeafiaPassiveServer.queueFunction(()->{
				if (!isInvalid())
					LeafiaPacket._start(this).__write(0,this.scale).__sendToClients(4096);
			});
		}
	}
	int timer = 0;
	@SideOnly(Side.CLIENT)
	void addSmoke() {
		double t = Math.pow(scale,0.5);
		if (timer++ > t) {
			timer = 0;
			ParticleRBMKSmoke smoke = new ParticleRBMKSmoke(world,pos.getX()+0.5,pos.getY()+1,pos.getZ()+0.5);
			smoke.setBaseScale(scale);
			smoke.setMaxScale(scale*3);
			double sc = Math.pow(scale,0.75);
			smoke.setLife((int) (100*sc)+world.rand.nextInt((int) (50*sc))+1);
			smoke.setLift(0.65f);
			Minecraft.getMinecraft().effectRenderer.addEffect(smoke);
		}
	}
	boolean firstTick = true;
	@Override
	public void update() {
		if (world.isRemote) {
			if (firstTick) {
				firstTick = false;
				LeafiaPacket._start(this).__write(0,true).__sendToServer();
			}
			if (scale > 0)
				addSmoke();
		} else {
			// yeah fuck off
			LeafiaPacket._start(this).__write(0,scale).__sendToClients(4096);
		}
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		setScale(compound.getFloat("scale"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setFloat("scale",scale);
		return super.writeToNBT(compound);
	}
	@Override
	public String getPacketIdentifier() {
		return "PRIBRIS_SMOKE";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 0)
			scale = (float)value;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == 0)
			LeafiaPacket._start(this).__write(0,scale).__sendToClient(plr);
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
	@Override
	public double affectionRange() {
		return 0;
	}
}
