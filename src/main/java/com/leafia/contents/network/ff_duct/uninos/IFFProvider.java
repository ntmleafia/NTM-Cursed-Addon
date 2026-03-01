package com.leafia.contents.network.ff_duct.uninos;

import com.hbm.lib.ForgeDirection;
import com.hbm.uninos.UniNodespace;
import com.leafia.contents.network.FFNBT;
import com.leafia.dev.LeafiaUtil;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public interface IFFProvider extends IFFHandler {
	default void tryProvide(FluidTank sending,World world,BlockPos pos,ForgeDirection dir) {
		if (world.isRemote) throw new LeafiaDevFlaw("screw you don't fucking tryProvide on remote");
		if (sending.getFluid() == null) return;
		if (!this.canConnect(sending.getFluid(),dir)) return;
		TileEntity targetTE = world.getTileEntity(pos);
		if (targetTE == null) return;
		ForgeDirection dirOpposite = dir.getOpposite();
		if (targetTE instanceof IFFConductor conductor) {
			if (conductor.canConnect(sending.getFluid(),dirOpposite)) {
				FFNode node = UniNodespace.getNode(world,pos,FFNet.PROVIDER);
				if (node != null && node.net != null) {
					node.net.addTank(this,sending);
					node.net.addProvider(this);
				}
			}
		}
		if (targetTE instanceof IFFReceiver rec && targetTE != this) {
			if (rec.canConnect(sending.getFluid(),dirOpposite)) {
				FluidTank receiving = rec.getCorrespondingTank(sending.getFluid());
				if (FFNBT.areTagsCompatible(sending.getFluid(),receiving)) {
					int demand = receiving.getCapacity()-receiving.getFluidAmount();
					int toTransfer = Math.min(demand,sending.getFluidAmount());
					if (toTransfer > 0) {
						// at this point, transferring is confirmed, no turning back
						int sent = LeafiaUtil.fillFF(sending,receiving,toTransfer);
						if (sent != toTransfer)
							throw new LeafiaDevFlaw("prov: confirmed transfer amount ("+toTransfer+"mB) and actual amount transferred ("+sent+"mB) doesn't match, wtf");
					}
				}
			}
		}
	}
	/// The corresponding tank for input
	FluidTank getSendingTank(FluidStack stack);
}
