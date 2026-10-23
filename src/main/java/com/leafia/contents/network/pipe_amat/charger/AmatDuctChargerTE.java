// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.pipe_amat.charger;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.api.fluidmk2.FluidNetMK2;
import com.hbm.api.fluidmk2.IFluidProviderMK2;
import com.hbm.api.fluidmk2.IFluidStandardSenderMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.uninos.GenNode;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.Compat;
import com.leafia.contents.network.pipe_amat.AmatDuctTE;
import com.leafia.contents.network.pipe_amat.uninos.AmatNet;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AmatDuctChargerTE extends AmatDuctTE implements ITickable, IEnergyReceiverMK2 {
	long power = 0;
	public static long maxPower = 10000;

	@Override
	public void update() {
		if(!world.isRemote) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos offset = pos.offset(facing);
				ForgeDirection dir = ForgeDirection.getOrientation(facing);
				trySubscribe(world,offset,dir);
				//System.out.println(power);
			}
			if (canUpdate()) {
				ensureAmatNode();
				if (node != null && node.net != null) {
					long demand = AmatNet.maxPower-node.net.power;
					long transfer = Math.min(power,demand);
					if (transfer > 0) {
						power -= transfer;
						node.net.power += transfer;
					}
				}
			}
			LeafiaPacket._start(this).__write(31,power).__sendToAffectedClients();
		}
	}
	public void sendTypeUpdatePacket() {
		LeafiaPacket._start(this).__write(30,type.getID()).__sendToAffectedClients();
	}
	public void setType(FluidType type) {
		super.setType(type);
		sendTypeUpdatePacket();
	}
	public FluidType getType() {
		return type;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setLong("power",power);
		compound.setInteger("filter",type.getID());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		type = Fluids.fromID(compound.getInteger("filter"));
		power = compound.getLong("power");
	}

	@Override
	public double affectionRange() {
		return super.affectionRange();
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
		return maxPower;
	}

	@Override
	public String getPacketIdentifier() {
		return "AMAT_CHRGR";
	}

	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 31)
			power = (long)value;
		else if (key == 30)
			type = Fluids.fromID((int)value);
	}

	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }

	@Override
	public void onPlayerValidate(EntityPlayer plr) {
		LeafiaPacket._start(this).__write(30,type.getID()).__sendToClient(plr);
	}
}
