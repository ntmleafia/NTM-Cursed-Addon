package com.leafia.contents.machines.reactors.pwr.blocks.components.terminal;

import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.IGUIProvider;
import com.leafia.contents.machines.reactors.pwr.PWRData;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRAssignableEntity;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentBlock;
import com.leafia.contents.machines.reactors.pwr.container.PWRTerminalContainer;
import com.leafia.contents.machines.reactors.pwr.container.PWRTerminalUI;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public class PWRTerminalTE extends PWRAssignableEntity implements LeafiaPacketReceiver, IGUIProvider, IFluidStandardReceiverMK2, IFluidStandardSenderMK2, ITickable {
	//static {
	//	MainRegistry.registerTileEntities.put(TileEntityPWRTerminal.class,"pwr_terminal"); // didnt work. I hate this game
	//}


	public PWRData getLinkedCoreDiagnosis() {
		PWRData data = getLinkedCore();
		if (data == null) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof PWRComponentBlock pwr) {
				pwr.beginDiagnosis(world,pos,pos);
				data = getLinkedCore();
			}
		}
		return data;
	}

	/*@Nullable
	PWRData gatherData() {
		if (this.corePos != null) {
			TileEntity entity = world.getTileEntity(corePos);
			if (entity != null) {
				if (entity instanceof PWRComponentEntity) {
					return ((PWRComponentEntity) entity).getCore();
				}
			}
		}
		return null;
	}*/
	@Override
	public void onDiagnosis() {
		LeafiaPacket._start(this).__write(0,corePos).__sendToAffectedClients();
	}
/*
	// redirects
	@Override
	public IFluidTankProperties[] getTankProperties() {
		PWRData gathered = gatherData();
		return (gathered != null) ? gathered.getTankProperties() : new IFluidTankProperties[0];
	}
	@Override
	public int fill(FluidStack resource,boolean doFill) {
		PWRData gathered = gatherData();
		return (gathered != null) ? gathered.fill(resource,doFill) : 0;
	}
	@Nullable
	@Override
	public FluidStack drain(FluidStack resource,boolean doDrain) {
		PWRData gathered = gatherData();
		return (gathered != null) ? gathered.drain(resource,doDrain) : null;
	}
	@Nullable
	@Override
	public FluidStack drain(int maxDrain,boolean doDrain) {
		PWRData gathered = gatherData();
		return (gathered != null) ? gathered.drain(maxDrain,doDrain) : null;
	}*/


	@Override
	public void validate() {
		super.validate();
		if (world.isRemote)
			LeafiaPacket._validate(this);
	}

	/*@Override
	public boolean hasCapability(Capability<?> capability,@Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability,facing);
	}
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability,@Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
		}
		return super.getCapability(capability,facing);
	}*/

	@Override
	public String getPacketIdentifier() {
		return "PWR_TERMINAL";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0) {
			//if (value.equals(false))
			//	corePos = null;
			//else
				corePos = (BlockPos)value; // Now supports null values!
		}
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket._start(this).__write(0,/*(corePos == null) ? false : */corePos).__sendToClient(plr);
	}

	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		PWRData core = getLinkedCoreDiagnosis();
		if (core != null)
			return new PWRTerminalContainer(entityPlayer.inventory,this,core);
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		PWRData core = getLinkedCoreDiagnosis();
		if (core != null)
			return new PWRTerminalUI(entityPlayer.inventory,this,core);
		return null;
	}


	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		PWRData core = getLinkedCore();
		if (core != null) {
			return new FluidTankNTM[]{
					core.tanks[0],
					core.tanks[3]
			};
		}
		return new FluidTankNTM[0];
	}

	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		PWRData core = getLinkedCore();
		if (core != null) {
			return new FluidTankNTM[]{
					core.tanks[1],
					core.tanks[4]
			};
		}
		return new FluidTankNTM[0];
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		PWRData core = getLinkedCore();
		if (core != null)
			return core.tanks;
		return new FluidTankNTM[0];
	}

	boolean loaded = true;
	@Override
	public void onChunkUnload() {
		loaded = false;
		super.onChunkUnload();
	}
	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			PWRData core = getLinkedCore();
			if (core != null) {
				for (EnumFacing facing : EnumFacing.values()) {
					trySubscribe(core.tankTypes[0],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
					trySubscribe(core.tankTypes[3],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
					tryProvide(core.tanks[1],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
					tryProvide(core.tanks[4],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
				}
			}
		}
	}
}
