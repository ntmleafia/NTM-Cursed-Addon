package com.leafia.contents.machines.powercores.dfc.components.pulser;

import com.custom_hbm.sound.LCEAudioWrapper;
import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.AddonBase;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.settings.AddonConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class CoreDetonatorTE extends LCETileEntityMachineBase implements IDFCBase, ITickable, IGUIProvider, IEnergyReceiverMK2 {
	public long power = 0;
	public boolean isOn = true;
	String code = "";
	public boolean local$codeSet = false;
	LCEAudioWrapper leafia$sound;
	boolean leafia$isPlaying = false;
	void leafia$playSound() {
		if (leafia$sound == null) {
			leafia$sound = AddonBase.proxy.getLoopedSoundStartStop(
					world,
					LeafiaSoundEvents.hspActive,
					LeafiaSoundEvents.hspIgnite,
					LeafiaSoundEvents.laser1stop,
					SoundCategory.BLOCKS,
					pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,
					0.25f,1
			).setCustomAttenuation((intended,distance)->{
				double div = 4; // alright fUCK OFF
				if (leafia$getCore(AddonConfig.dfcComponentRange) instanceof IMixinTileEntityCore mixin)
					div = Math.max(div,mixin.getDFCPulsers().size());
				return Math.pow(Math.max(0,1-distance/50),6)/div;
			});
		}
		if (!leafia$isPlaying)
			leafia$sound.startSound();
		leafia$isPlaying = true;
	}
	void leafia$stopSound() {
		if (leafia$sound == null) return;
		if (leafia$isPlaying)
			leafia$sound.stopSound();
		leafia$isPlaying = false;
	}
	@Override
	public void invalidate(){
		super.invalidate();
		if (leafia$sound != null) {
			leafia$sound.stopSound();
			leafia$sound = null;
		}
	}
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (leafia$sound != null) {
			leafia$sound.stopSound();
			leafia$sound = null;
		}
	}
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}
	public CoreDetonatorTE() {
		super(0);
	}
	@Override
	public long getPower() {
		return power;
	}
	@Override
	public void setPower(long l) {
		power = l;
	}
	@Override
	public long getMaxPower() {
		return 1_000_000_000_000L*5;
	}
	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new CoreDetonatorContainer(entityPlayer,this);
	}
	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new CoreDetonatorGUI(entityPlayer,this);
	}
	@Override
	public String getDefaultName() {
		return "tile.dfc_pulser.name";
	}
	protected BlockPos targetPosition = new BlockPos(0,0,0);
	public TileEntityCore lastGetCore = null;
	@Override
	public TileEntityCore leafia$lastGetCore() {
		return lastGetCore;
	}
	@Override
	public void leafia$lastGetCore(TileEntityCore core) {
		lastGetCore = core;
	}
	@Override
	public BlockPos leafia$getTargetPosition() {
		return targetPosition;
	}
	@Override
	public void leafia$targetPosition(BlockPos pos) {
		targetPosition = pos;
	}
	final Map<EntityPlayer,Long> authorized = new ConcurrentHashMap<>();
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		IDFCBase.super.onReceivePacketServer(key,value,plr);
		if (key == 30) {
			String c = (String)value;
			if (!code.isEmpty()) {
				if (code.equals(c)) {
					LeafiaPacket._start(this).__write(30,true).__sendToClient(plr);
					return;
				}
			} else {
				code = c;
				LeafiaPacket._start(this).__write(30,true).__sendToClient(plr);
				return;
			}
			LeafiaPacket._start(this).__write(30,false).__sendToClient(plr);
		}
		if (key == 0) {
			if (code.equals((String)value))
				authorized.put(plr,world.getTotalWorldTime());
		}
		if (authorized.containsKey(plr)) {
			switch(key) {
				case 1 -> isOn = (boolean) value;
				case 2 -> {
					TileEntityCore core = leafia$getCore(AddonConfig.dfcComponentRange);
					if (core != null) {
						IMixinTileEntityCore mixin = (IMixinTileEntityCore) core;
						mixin.setDetonation(!mixin.getDetonation());
					}
				}
			}
		}
	}
	boolean isConditionMet() {
		return power >= 1_000_000_000_000L;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void onReceivePacketLocal(byte key,Object value) {
		IDFCBase.super.onReceivePacketLocal(key,value);
		if (key == 30) {
			boolean val = (boolean)value;
			CoreDetonatorGUI gui = CoreDetonatorGUI.openGUI;
			if (gui != null) {
				if (val) {
					gui.playClick(1);
					gui.auth = gui.code;
					gui.unlocked = true;
				} else {
					gui.playDenied();
					gui.code = "";
				}
				gui.blocked = false;
			}
		}
		switch(key) {
			case 0 -> local$codeSet = (boolean)value;
			case 1 -> power = (long)value;
			case 2 -> isOn = (boolean)value;
		}
	}
	@Override
	public String getPacketIdentifier() {
		return "DFC_PULSER";
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		leafia$writeTargetPos(compound);
		return super.writeToNBT(compound);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		leafia$readTargetPos(compound);
		super.readFromNBT(compound);
	}
	@Override
	public void update() {
		TileEntityCore core = leafia$getCore(AddonConfig.dfcComponentRange);
		if (!world.isRemote) {
			for (Entry<EntityPlayer,Long> entry : authorized.entrySet()) {
				if (entry.getValue() < world.getTotalWorldTime()+5)
					authorized.remove(entry.getKey());
			}
			LeafiaPacket._start(this).__write(31,targetPosition).__sendToAffectedClients();
			for (EnumFacing face : EnumFacing.values())
				trySubscribe(world,pos.offset(face),ForgeDirection.getOrientation(face));
			if (isOn && core != null) {
				IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
				mixin.getDFCPulsers().add(this);
				if (mixin.getDetonation()) {
					if (!isConditionMet())
						mixin.setDetonation(false);
					else
						power -= 1_000_000_000_000L;
				}
			}
			LeafiaPacket._start(this)
					.__write(0,!code.isEmpty())
					.__write(1,power)
					.__write(2,isOn)
					.__sendToAffectedClients();
		} else {
			boolean active = false;
			if (isOn) {
				if (core != null) {
					IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
					if (mixin.getDetonation())
						active = true;
				}
			}
			if (active)
				leafia$playSound();
			else
				leafia$stopSound();
		}
	}
	public long getPowerScaled(long i) {
		return (power * i) / getMaxPower();
	}
	public int getDetScaled(int i) {
		int det = 0;
		if (leafia$getCore(AddonConfig.dfcComponentRange) instanceof IMixinTileEntityCore core)
			det = core.getDetonationTimer();
		return (det * i) / (20*30);
	}
}
