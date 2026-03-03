package com.leafia.contents.machines.reactors.pwr.blocks.components.occs.outlet;

import com.hbm.api.fluidmk2.IFluidStandardReceiverMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.leafia.contents.machines.reactors.pwr.PWRData;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRAssignableEntity;
import com.leafia.contents.machines.reactors.pwr.blocks.components.PWRComponentBlock;
import com.leafia.dev.container_utility.LeafiaPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class PWROCCSOutTE extends PWRAssignableEntity implements IFluidStandardReceiverMK2, IFluidStandardSenderMK2, ITickable {
	public PWRData getLinkedCoreDiagnosis() {
		PWRData data = getCoreByCorePos();
		if (data == null) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof PWRComponentBlock pwr) {
				pwr.beginDiagnosis(world,pos,pos);
				data = getCoreByCorePos();
			}
		}
		return data;
	}

	@Override
	public @NotNull FluidTankNTM[] getReceivingTanks() {
		return new FluidTankNTM[0];
	}

	@Override
	public @NotNull FluidTankNTM[] getSendingTanks() {
		PWRData core = getCoreByCorePos();
		if (core != null) {
			return new FluidTankNTM[]{
					core.tanks[7],
					core.tanks[6]
			};
		}
		return new FluidTankNTM[0];
	}

	@Override
	public FluidTankNTM[] getAllTanks() {
		PWRData core = getCoreByCorePos();
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
		super.update();
		if (!world.isRemote) {
			PWRData core = getCoreByCorePos();
			if (core != null) {
				for (EnumFacing facing : EnumFacing.values()) {
					tryProvide(core.tanks[7],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
					tryProvide(core.tanks[6],world,pos.offset(facing),ForgeDirection.getOrientation(facing));
				}
			}
		}
	}

	@Override
	public void onDiagnosis() {
		LeafiaPacket._start(this).__write(0,corePos).__sendToAffectedClients();
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0)
			corePos = (BlockPos)value; // Now supports null values!
	}

	@Override
	public String getPacketIdentifier() {
		return "PWR_OCCS_OUT";
	}

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		addDataToPacket(LeafiaPacket._start(this).__write(0,corePos)).__sendToClient(plr);
	}
}
