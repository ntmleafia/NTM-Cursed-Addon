package com.leafia.contents.network.ff_duct.utility.filter;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.lib.ForgeDirection;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import com.leafia.contents.network.ff_duct.uninos.IFFProvider;
import com.leafia.contents.network.ff_duct.uninos.IFFReceiver;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityBase;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityTEBase;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.llib.exceptions.LeafiaDevFlaw;
import com.llib.group.LeafiaMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FFFilterTE2 extends FFDuctUtilityTEBase implements ITickable {
	boolean valid = false;
	MSRFuel filter = MSRFuel.u235;
	@Override
	public void setType(FluidType type) {
		super.setType(type);
	}
	void doTransfer() {
		if (getType().getFF() == null) return;
		if (!world.isRemote) {
			valid = false;
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof FFFilterBlock) {
				EnumFacing facing = state.getValue(FFDuctUtilityBase.FACING);
				TileEntity behind = world.getTileEntity(pos.offset(facing,-1));
				TileEntity ahead = world.getTileEntity(pos.offset(facing));
				if (behind instanceof IFFProvider prov && ahead instanceof IFFReceiver rec) {
					FluidStack virtualStack = new FluidStack(getType().getFF(),1);
					FluidTank sending = prov.getSendingTank(virtualStack);
					FluidTank receiving = rec.getCorrespondingTank(virtualStack);
					if (sending != null && receiving != null) {
						valid = true;
						if (receiving.getFluidAmount() > 0 && sending.getFluidAmount() > 0) {
							assert sending.getFluid() != null;
							assert receiving.getFluid() != null;
							NBTTagCompound tag0 = MSRTEBase.nbtProtocol(sending.getFluid().tag);
							NBTTagCompound tag1 = MSRTEBase.nbtProtocol(receiving.getFluid().tag);
							Map<String,Double> mix0 = MSRTEBase.readMixture(tag0);
							Map<String,Double> mix1 = MSRTEBase.readMixture(tag1);
							if (mix0.containsKey(filter.name())) {
								assert sending.getFluid() != null;
								assert receiving.getFluid() != null;
								double ratio = sending.getFluidAmount()/(double)receiving.getFluidAmount();
								mix1.put(filter.name(),mix1.getOrDefault(filter.name(),0d)+mix0.get(filter.name())*ratio);
								mix0.remove(filter.name());
								MSRTEBase.writeMixture(mix0,tag0);
								MSRTEBase.writeMixture(mix1,tag1);
								sending.getFluid().tag = tag0;
								receiving.getFluid().tag = tag1;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void update() {
		doTransfer();
		LeafiaPacket packet = LeafiaPacket._start(this).__write(0,valid);
		packet.__write(2,filter.ordinal());
		packet.__sendToAffectedClients();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(List<String> info) {
		int color = valid ? 0x55FF55 : 0xFF0000;
		info.add(TextFormatting.GRAY+I18nUtil.resolveKey("tile.ff_filter.filter",I18nUtil.resolveKey("tile.msr.fuel."+filter.name())));
		info.add("&["+color+"&]"+I18nUtil.resolveKey("tile.ff_filter."+(valid ? "valid" : "invalid")));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		try {
			filter = MSRFuel.valueOf(compound.getString("filter"));
		} catch (IllegalArgumentException ignored) {}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("filter",filter.name());
		return super.writeToNBT(compound);
	}

	@Override
	public String getPacketIdentifier() {
		return "FF_FILTER";
	}
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		super.onReceivePacketLocal(key,value);
		if (key == 0) {
			valid = (boolean)value;
		}
		else if (key == 2)
			filter = MSRFuel.values()[(int)value];
	}
}
