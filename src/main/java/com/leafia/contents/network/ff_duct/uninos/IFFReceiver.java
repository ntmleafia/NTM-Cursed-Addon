// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.ff_duct.uninos;

import com.hbm.lib.ForgeDirection;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.Compat;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public interface IFFReceiver extends IFFHandler {
	default void trySubscribe(FluidTank tank,FluidStack defaultStack,World world,BlockPos pos,ForgeDirection dir) {
		if (world.isRemote) throw new LeafiaDevFlaw("screw you don't fucking trySubscribe on remote");
		FluidStack stack = tank.getFluid() == null ? defaultStack : tank.getFluid();
		if (!this.canConnect(stack,dir)) return;
		TileEntity te = Compat.getTileStandard(world,pos.getX(),pos.getY(),pos.getZ());
		if (te instanceof IFFConductor conductor) {
			if (!conductor.canConnect(stack,dir.getOpposite())) return;
			FFNode node = UniNodespace.getNode(world,pos,FFNet.PROVIDER);
			if (node != null && node.net != null)
				node.net.addReceiver(this);
		}
	}
	/// The corresponding tank for input
	FluidTank getCorrespondingTank(FluidStack stack);
}
