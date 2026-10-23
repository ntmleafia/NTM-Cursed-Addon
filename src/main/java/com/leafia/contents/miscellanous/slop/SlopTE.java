// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.slop;

import com.hbm.api.energymk2.IEnergyProviderMK2;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.DelayedTick;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.miscellanous.slop.container.SlopContainer;
import com.leafia.contents.miscellanous.slop.container.SlopGUI;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaDebug.Tracker.Action;
import com.leafia.dev.LeafiaDebug.Tracker.LeafiaTrackerPacket;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import com.llib.technical.FifthString;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SlopTE extends LCETileEntityMachineBase implements IGUIProvider, LeafiaPacketReceiver, ITickable, IEnergyProviderMK2 {
	public SlopTE() {
		super(2,false,true);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(EnumFacing e) {
		if (e == EnumFacing.DOWN)
			return new int[]{0};
		return new int[]{1};
	}

	@Override
	public boolean canExtractItemHopper(int slot,ItemStack itemStack,int amount) {
		return false;
	}

	@Override
	public String getDefaultName() {
		return "tile.slop_reactor.name";
	}

	public int abs(int a) {
		return Math.abs(a);
	}

	public void print(BlockPos p) {
		LeafiaTrackerPacket packet = new LeafiaTrackerPacket();
		packet.mode = Action.SHOW_BOX;
		packet.writer = (buf)->{
			buf.writeFloat(5);
			buf.writeInt(0xFF0000);
			buf.writeByte(1);
			buf.writeFifthString(new FifthString("Invalid Block"));
			buf.writeVec3i(p);
		};
		for (EntityPlayer plr : world.playerEntities) {
			if (new Vec3d(plr.posX,plr.posY,plr.posZ).distanceTo(new Vec3d(pos).add(0.5,0.5,0.5)) < 30)
				LeafiaPacket._sendToClient(packet,plr);
		}
	}
	public boolean formed = false;
	public boolean checkFormed(boolean printInvalidBlocks) {
		EnumFacing face = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
		EnumFacing forward = face.getOpposite();
		BlockPos center = pos.offset(forward,2).up(2);
		boolean correct = true;
		for (int xo = -2; xo <= 2; xo++) {
			for (int zo = -2; zo <= 2; zo++) {
				for (int yo = -2; yo <= 2; yo++) {
					BlockPos p = new BlockPos(center.add(xo,yo,zo));
					if (abs(xo) <= 1 && abs(yo) <= 1 && abs(zo) <= 1) { // inside
						if (world.getBlockState(p).getBlock() != ModBlocks.toxic_block && !world.isAirBlock(p)) {
							correct = false;
							if (printInvalidBlocks)
								print(p);
						}
					} else {
						if (    (!p.equals(pos)) &&
								((abs(xo) == 2 && abs(yo) == 2)
								|| (abs(zo) == 2 && abs(yo) == 2)
								|| (abs(xo) == 2 && abs(zo) == 2))
						) {
							if (world.getBlockState(p).getBlock() != AddonBlocks.slop_reactor_casing) {
								correct = false;
								if (printInvalidBlocks)
									print(p);
							}
						} else if (!p.equals(pos)) {
							if (world.getBlockState(p).getBlock() != AddonBlocks.slop_reactor_glass) {
								correct = false;
								if (printInvalidBlocks)
									print(p);
							}
						}
					}
				}
			}
		}
		return correct;
	}
	public boolean isValidFuel(BlockPos p) {
		IBlockState state = world.getBlockState(p);
		if (state.getBlock() == ModBlocks.toxic_block)
			return state.getValue(BlockFluidClassic.LEVEL) == 0;
		return false;
	}
	public void updateFuel() {
		EnumFacing face = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
		EnumFacing forward = face.getOpposite();
		EnumFacing right = forward.rotateY();
		fuel = 0;
		for (int xo = -1; xo <= 1; xo++) {
			for (int yo = 1; yo <= 3; yo++) {
				for (int zo = 1; zo <= 3; zo++) {
					BlockPos p = pos.offset(forward,zo).offset(right,xo).up(yo);
					if (isValidFuel(p))
						fuel++;
				}
			}
		}
	}
	public void clearFuelLayer() {
		EnumFacing face = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
		EnumFacing forward = face.getOpposite();
		EnumFacing right = forward.rotateY();
		int yo = (int)Math.ceil(fuel/9f);
		for (int xo = -1; xo <= 1; xo++) {
			for (int zo = 1; zo <= 3; zo++)
				world.setBlockToAir(pos.offset(forward,zo).offset(right,xo).up(yo));
		}
	}
	public void fillFuelLayer() {
		EnumFacing face = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
		EnumFacing forward = face.getOpposite();
		EnumFacing right = forward.rotateY();
		int yo = fuel/9+1;
		for (int xo = -1; xo <= 1; xo++) {
			for (int zo = 1; zo <= 3; zo++)
				world.setBlockState(pos.offset(forward,zo).offset(right,xo).up(yo),ModBlocks.toxic_block.getDefaultState());
		}
	}
	public void meltdown() {
		EnumFacing face = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
		EnumFacing forward = face.getOpposite();
		BlockPos center = pos.offset(forward,2).up(2);
		int range = 8;
		for (int xo = -range; xo <= range; xo++) {
			for (int yo = -range; yo <= range; yo++) {
				for (int zo = -range; zo <= range; zo++)
					world.setBlockToAir(center.add(xo,yo,zo));
			}
		}
		for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null,new AxisAlignedBB(center.getX()-range,center.getY()-range,center.getZ()-range,center.getX()+range+1,center.getY()+range+1,center.getZ()+range+1)))
			tryKill(entity);
	}
	public static void tryKill(final Entity toKill) { // copied from IF null grenade
		if (toKill instanceof MultiPartEntityPart part) {
			if (part.parent instanceof Entity parent) {
				tryKill(parent);
				return;
			}
		}
		if (toKill instanceof EntityLivingBase livingBase) livingBase.setHealth(0f);
		else toKill.setDead();
		DelayedTick.nextWorldTickEnd(toKill.world,w -> {
			if (!toKill.isDead && toKill.isEntityAlive()) {
				WorldServer ws = (WorldServer) w;
				if (toKill instanceof EntityPlayer) {
					ws.removeEntity(toKill);
					return;
				}
				if (toKill.isBeingRidden()) toKill.removePassengers();
				if (toKill.isRiding()) toKill.dismountRidingEntity();
				Entity[] parts = toKill.getParts();
				if (parts != null) {
					for (Entity p : parts) {
						if (p != null) {
							if (p.isBeingRidden()) p.removePassengers();
							if (p.isRiding()) p.dismountRidingEntity();
							ws.removeEntityDangerously(p);
						}
					}
				}
				ws.removeEntityDangerously(toKill);
			}
		});
	}

	public int damage = 0;
	public static int maxDamage = 20*30;
	public int partialConsumption = 0;
	public static int maxPartialConsumption = 20*50*9/3;
	public int dynaMaxPartialConsumption = 0;
	public int fuel = 0;
	public boolean on = false;
	public long power = 0;
	public static long generation = 50000/20;
	public static long maxPower = generation*30*20;
	public int cool = 0;
	public static int maxCool = 20*5;

	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("damage",damage);
		compound.setInteger("partial",partialConsumption);
		compound.setLong("power",power);
		compound.setBoolean("on",on);
		compound.setInteger("cool",cool);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		damage = compound.getInteger("damage");
		partialConsumption = compound.getInteger("partial");
		power = compound.getLong("power");
		on = compound.getBoolean("on");
		cool = compound.getInteger("cool");
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			formed = checkFormed(false);
			if (formed) {
				updateFuel();
				ItemStack stack = inventory.getStackInSlot(0);
				if (stack.getItem() == ModItems.ingot_uranium && fuel < 27) {
					int demand = 9-fuel%9;
					if (stack.getCount() >= demand) {
						stack.shrink(demand);
						fillFuelLayer();
					}
				}
				int mod = fuel%9;
				if (fuel == 0) mod = 0;
				else if (mod == 0) mod = 9;
				dynaMaxPartialConsumption = maxPartialConsumption*mod/9;
				if (on) {
					if (fuel > 0) {
						power = Math.min(power+generation,maxPower);
						partialConsumption++;
						if (partialConsumption > dynaMaxPartialConsumption) {
							partialConsumption = 0;
							clearFuelLayer();
						}
					}
					if (cool <= 0) {
						ItemStack stack1 = inventory.getStackInSlot(1);
						if (stack1.getItem() == Items.SNOWBALL) {
							stack1.shrink(1);
							cool = maxCool;
						}
					} else
						cool--;
					if (cool <= 0) {
						damage++;
						if (damage > maxDamage) {
							meltdown();
							return;
						}
					} else
						damage = 0;
				}
				EnumFacing face = world.getBlockState(pos).getValue(BlockHorizontal.FACING);
				tryProvide(world,pos.offset(face),ForgeDirection.getOrientation(face));
				tryProvide(world,pos.down(),ForgeDirection.DOWN);
				LeafiaPacket._start(this)
						.__write(0,fuel)
						.__write(1,damage)
						.__write(2,partialConsumption)
						.__write(3,dynaMaxPartialConsumption)
						.__write(4,on)
						.__write(5,power)
						.__sendToListeners();
			} else
				on = false;
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return formed && super.isUseableByPlayer(player);
	}

	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new SlopContainer(entityPlayer.inventory,this);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new SlopGUI(entityPlayer.inventory,this);
	}

	@Override
	public String getPacketIdentifier() {
		return "SLOP_NUKE";
	}

	@Override
	public double affectionRange() {
		return 0;
	}

	public final List<EntityPlayer> listeners = new ArrayList<>();

	@Override
	public List<EntityPlayer> getListeners() {
		return listeners;
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		switch(key) {
			case 0 -> fuel = (int)value;
			case 1 -> damage = (int)value;
			case 2 -> partialConsumption = (int)value;
			case 3 -> dynaMaxPartialConsumption = (int)value;
			case 4 -> on = (boolean)value;
			case 5 -> power = (long)value;
		}
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) {
		if (key == 0)
			on = (boolean)value;
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) { }

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
		return maxPower;
	}
}
