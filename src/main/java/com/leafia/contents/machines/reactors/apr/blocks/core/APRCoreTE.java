package com.leafia.contents.machines.reactors.apr.blocks.core;

import com.hbm.tileentity.IGUIProvider;
import com.leafia.contents.AddonBlocks.APR;
import com.leafia.contents.machines.reactors.apr.blocks.APRComponentBlock;
import com.leafia.contents.machines.reactors.apr.blocks.APRComponentBlock.APRComponentType;
import com.leafia.contents.machines.reactors.apr.blocks.core.container.APRMBUI;
import com.leafia.dev.LeafiaDebug.Tracker.Action;
import com.leafia.dev.LeafiaDebug.Tracker.LeafiaTrackerPacket;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import com.llib.technical.FifthString;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;

public class APRCoreTE extends LCETileEntityMachineBase implements IGUIProvider,LeafiaPacketReceiver,ITickable {
	static final byte idChambers = 0;
	static final byte idAssembled = 1;
	static final byte idHighlight = 2;
	public static final int minChamberRadius = 6;
	public static final int maxChamberRadius = 32;
	public final Map<BlockPos,IBlockState> mbRequirement = new HashMap<>();
	public final List<Integer> chambers = new ArrayList<>();
	public boolean assembled = false;
	public APRCoreTE() {
		super(6);
	}
	public void sortChambers() {
		chambers.sort(Comparator.naturalOrder());
	}
	static int[] toArray(List<Integer> radii) {
		int[] array = new int[radii.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = radii.get(i);
		return array;
	}
	public void rebuildMBRequirement() {
		sortChambers();
		mbRequirement.clear();
		if (!chambers.isEmpty()) {
			// DISC
			IBlockState plate = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.PLATING);
			IBlockState dark = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.PLATING_DARK);
			IBlockState parts = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.MECHANICAL);
			int radius = chambers.get(chambers.size()-1);
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos p = new BlockPos(pos.getX()+x,pos.getY(),pos.getZ()+z);
					if (Math.sqrt(p.distanceSq(getPos())) <= radius) {
						IBlockState cover = (x == 0 || z == 0) ? dark : plate;
						mbRequirement.put(p.down(),cover);
						mbRequirement.put(p.down(2),parts);
						mbRequirement.put(p.down(3),parts);
						mbRequirement.put(p.down(4),parts);
						mbRequirement.put(p.down(5),cover);
					}
				}
			}
			// COILS
			IBlockState coil = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.COIL);
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y >= -4; y--) {
					mbRequirement.put(getPos().add(x,y,2),coil);
					mbRequirement.put(getPos().add(x,y,-2),coil);
				}
			}
			for (int z = -1; z <= 1; z++) {
				for (int y = -1; y >= -4; y--) {
					mbRequirement.put(getPos().add(2,y,z),coil);
					mbRequirement.put(getPos().add(-2,y,z),coil);
				}
			}
			// FUNNEL CHAMBER
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int y = -1; y >= -4; y--)
						mbRequirement.put(getPos().add(x,y,z),Blocks.AIR.getDefaultState());
				}
			}
		}
		for (Integer r : chambers)
			APRCoreBlock.generateTorus(getPos(),r,mbRequirement);
	}
	@Override
	public String getDefaultName() {
		return "container.apr";
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		assembled = nbt.getBoolean("assembled");
		setChambers(nbt.getIntArray("chambers"),false);
	}
	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("assembled",assembled);
		nbt.setIntArray("chambers",toArray(chambers));
		return super.writeToNBT(nbt);
	}
	@Override
	public String getPacketIdentifier() {
		return "APR_CORE";
	}
	LeafiaPacket writeState(LeafiaPacket packet) {
		return packet.__write(idAssembled,assembled).__write(idChambers,toArray(chambers));
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		writeState(LeafiaPacket._start(this)).__sendToClient(plr);
	}
	public void requestChambers(List<Integer> edited,boolean highlightOnClose) {
		LeafiaPacket._start(this).__write(idChambers,toArray(edited)).__write(idHighlight,highlightOnClose).__sendToServer();
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch (key) {
			case idAssembled -> assembled = (boolean)value;
			case idChambers -> setChambers(value,false);
		}
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == idChambers) {
			if (assembled) return;
			setChambers(value,true);
			markDirty();
			writeState(LeafiaPacket._start(this)).__sendToAffectedClients();
			rebuildMBRequirement();
		} else if (key == idHighlight && (boolean)value) {
			int highlightsEmitted = 0;
			for (Entry<BlockPos,IBlockState> entry : mbRequirement.entrySet()) {
				if (plr.isCreative()) {
					world.setBlockState(entry.getKey(),entry.getValue());
					continue;
				}
				if (!world.isAirBlock(entry.getKey())) {
					if (!isValidBlock(entry.getKey())) {
						LeafiaTrackerPacket packet = new LeafiaTrackerPacket();
						packet.mode = Action.SHOW_BOX;
						packet.writer = (buf)->{
							buf.writeFloat(10);
							buf.writeInt(0xFF0000);
							buf.writeByte((byte)1);
							buf.writeFifthString(new FifthString("Invalid Block"));
							buf.writeVec3i(entry.getKey());
						};
						LeafiaPacket._sendToClient(packet,plr);
						highlightsEmitted++;
						//if (highlightsEmitted > 50)
						//	break;
					}
				}
			}
		}
	}
	public boolean checkAssembled() {
		if (chambers.isEmpty()) return false;
		for (BlockPos bp : mbRequirement.keySet()) {
			if (!isValidBlock(bp))
				return false;
		}
		return true;
	}
	void setChambers(Object received,boolean canReject) {
		this.chambers.clear();
		List<Integer> chambers = new ArrayList<>();
		if (received instanceof int[] radii) {
			for (int r : radii) {
				//if (chambers.size() >= maxChambers) break;
				chambers.add(MathHelper.clamp(r,1,maxChamberRadius));
			}
		}
		chambers.sort(Comparator.naturalOrder());
		int lastR = 0;
		for (Integer r : chambers) {
			if (r-lastR <= 3 && canReject)
				return;
			lastR = r;
		}
		this.chambers.addAll(chambers);
		rebuildMBRequirement();
	}
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (!assembled) return INFINITE_EXTENT_AABB;
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 3,
					pos.getY() - 2,
					pos.getZ() - 3,
					pos.getX() + 4,
					pos.getY() + 3,
					pos.getZ() + 4
			);
		}
		return bb;
	}
	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		if (!assembled)
			return null;
		// TODO: add actual container
		return null;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		if (!assembled)
			return new APRMBUI(this);
		// TODO: add actual gui
		return null;
	}
	@Override
	public double affectionRange() {
		return 256;
	}
	public boolean isValidBlock(BlockPos pos) {
		BlockPos center = getPos();
		Vec3i relative = pos.subtract(center);
		if (Math.abs(relative.getX()) <= 1 && Math.abs(relative.getZ()) <= 1 && relative.getY() <= -1 && relative.getY() >= -4) {
			Material mat = getWorld().getBlockState(pos).getMaterial();
			return mat.isReplaceable() && !mat.isSolid() && !mat.isLiquid();
		}
		IBlockState desired = mbRequirement.get(pos);
		if (desired != null) {
			IBlockState state = getWorld().getBlockState(pos);
			if (state.equals(desired)) return true;
			if (desired.getBlock() == APR.apr_component) {
				if (desired.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING || desired.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING_DARK) {
					if (state.getBlock() == APR.apr_component) {
						if (state.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING || state.getValue(APRComponentBlock.VARIANT) == APRComponentType.PLATING_DARK)
							return true;
					}
				} else if (desired.getValue(APRComponentBlock.VARIANT) == APRComponentType.SUPPORT) {
					// TODO: account for fluid ports (which havent been added yet)
				}
			}
			return false;
		} else
			return true;
	}
	@Override
	public void update() {
		if (!world.isRemote) return;
	}
}
