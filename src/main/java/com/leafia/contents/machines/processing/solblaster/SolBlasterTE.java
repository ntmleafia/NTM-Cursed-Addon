package com.leafia.contents.machines.processing.solblaster;

import com.hbm.handler.threading.PacketThreading;
import com.hbm.items.ModItems;
import com.hbm.tileentity.IGUIProvider;
import com.leafia.CommandLeaf;
import com.leafia.contents.machines.processing.solblaster.container.SolBlasterContainer;
import com.leafia.contents.machines.processing.solblaster.container.SolBlasterGUI;
import com.leafia.contents.machines.processing.solblaster.recipes.SolBlasterRecipes;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import com.leafia.dev.optimization.LeafiaParticlePacket.VanillaExt;
import com.leafia.init.LeafiaSoundEvents;
import com.llib.group.LeafiaSet;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolBlasterTE extends LCETileEntityMachineBase implements ITickable, LeafiaPacketReceiver, IGUIProvider {
	public int doorPos = 0;
	public int prevDoorPos = 0;
	public int nextDoorPos = 0;
	public Set<BlockPos> activePoses = new HashSet<>();
	public SolBlasterTE() {
		super(20);
		inventory = getNewInventory(20,1);
	}
	@Override
	public boolean isItemValidForSlot(int i,ItemStack stack) {
		if (i == 0)
			return stack.getItem() == ModItems.solinium_core;
		if (i >= 1 && i <= 4)
			return stack.getItem() == ModItems.early_explosive_lenses || stack.getItem() == ModItems.explosive_lenses;
		return super.isItemValidForSlot(i,stack);
	}
	@Override
	public boolean canInsertItemHopper(int slot,ItemStack itemStack,int amount) {
		if (slot >= 5)
			return SolBlasterRecipes.isValidInput(itemStack);
		return isItemValidForSlot(slot,itemStack);
	}
	@Override
	public boolean canExtractItemHopper(int slot,ItemStack itemStack,int amount) {
		if (slot >= 5)
			return !SolBlasterRecipes.isValidInput(itemStack);
		return false;
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		activePoses.clear();
		if (compound.hasKey("activePoses")) {
			NBTTagList list = compound.getTagList("activePoses",11);
			for (NBTBase base : list) {
				if (base instanceof NBTTagIntArray array) {
					int[] a = array.getIntArray();
					activePoses.add(new BlockPos(a[0],a[1],a[2]));
				}
			}
		}
	}
	@Override
	public @NotNull NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList active = new NBTTagList();
		for (BlockPos pos : activePoses)
			active.appendTag(new NBTTagIntArray(new int[]{pos.getX(),pos.getY(),pos.getZ()}));
		compound.setTag("activePoses",active);
		return super.writeToNBT(compound);
	}
	public boolean checkForItem(Item item,int slot) {
		return inventory.getStackInSlot(slot).getItem() == item;
	}
	public boolean isArmed() {
		if (!checkForItem(ModItems.solinium_core,0))
			return false;
		for (int i = 1; i <= 4; i++) {
			if (!checkForItem(ModItems.early_explosive_lenses,i) && !checkForItem(ModItems.explosive_lenses,i))
				return false;
		}
		return true;
	}
	public int cooldown = 0;
	public static int doorDuration = 50;
	@Override
	public void update() {
		if (!world.isRemote) {
			if (listeners.isEmpty()) {
				if (doorPos > 0) {
					if (doorPos == 20)
						world.playSound(null,pos,LeafiaSoundEvents.reactor_door_close,SoundCategory.BLOCKS,0.75f,1);
					doorPos = doorPos-1;
					if (doorPos == 0) {
						PacketThreading.createSendToAllTrackingThreadedPacket(
								new CommandLeaf.ShakecamPacket(
										new String[]{
												"type=smooth",
												"intensity=0.15",
												"duration=0.5",
												"speed=8",
												"blurDulling=50",
												"bloomDulling=50",
												"range=12",
												"curve=0.5"
										}).setPos(pos),
								new NetworkRegistry.TargetPoint(
										world.provider.getDimension(),pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,10)
						);
					}
				}
			} else {
				if (doorPos == 0)
					world.playSound(null,pos,LeafiaSoundEvents.reactor_door_open,SoundCategory.BLOCKS,0.75f,1);
				doorPos = Math.min(doorDuration,doorPos+1);
			}
			LeafiaPacket._start(this)
					.__write(SolBlasterPackets.DOOR_SYNC.ordinal(),doorPos)
					.__sendToClients(256);
			if (doorPos > 0)
				cooldown = 15;
			else {
				if (cooldown == 0) {
					if (!activePoses.isEmpty() && isArmed()) {
						for (int i = 0; i <= 4; i++)
							inventory.setStackInSlot(i,ItemStack.EMPTY);
						world.playSound(null,pos.up(3),SoundEvents.ENTITY_GENERIC_EXPLODE,SoundCategory.BLOCKS,4,1);
						world.playSound(null,pos.up(3),SoundEvents.ENTITY_LIGHTNING_THUNDER,SoundCategory.BLOCKS,20,1);
						VanillaExt.LargeExplode(5,4).emit(new Vec3d(pos).add(0.5,3,0.5),new Vec3d(0,1,0),world.provider.getDimension(),512);
						for (int i = 5; i < 20; i++)
							inventory.setStackInSlot(i,SolBlasterRecipes.processItem(inventory.getStackInSlot(i)));
					}
				}
				cooldown = Math.max(0,cooldown-1);
			}
		} else {
			prevDoorPos = doorPos;
			doorPos = nextDoorPos;
		}
	}
	public void updateRedstonePower(BlockPos pos) {
		if (world.isBlockPowered(pos))
			activePoses.add(pos);
		else
			activePoses.remove(pos);
	}
	@Override
	public String getDefaultName() {
		return "tile.sol_blaster.name";
	}
	@Override
	public String getPacketIdentifier() {
		return "SOL_BLAST";
	}
	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new SolBlasterContainer(entityPlayer,this);
	}
	@SideOnly(Side.CLIENT)
	@Override
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		return new SolBlasterGUI(entityPlayer,this);
	}
	public enum SolBlasterPackets {
		DOOR_SYNC;
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == SolBlasterPackets.DOOR_SYNC.ordinal())
			nextDoorPos = (int)value;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 3,
					pos.getY(),
					pos.getZ() - 3,
					pos.getX() + 4,
					pos.getY() + 6,
					pos.getZ() + 4
			);
		}
		return bb;
	}
	public LeafiaSet<EntityPlayer> listeners = new LeafiaSet<>();
	@Override
	public List<EntityPlayer> getListeners() {
		return listeners;
	}
}
