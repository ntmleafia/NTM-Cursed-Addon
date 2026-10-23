// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2025 Vidarin <smeds.vidar@gmail.com>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.fluidmk2.IFluidConnectorMK2;
import com.hbm.api.fluidmk2.IFluidReceiverMK2;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.lib.ForgeDirection;
import com.hbm.uninos.GenNode;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.Compat;
import com.leafia.contents.network.pipe_amat.uninos.AmatNet;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IFluidReceiverMK2.class,remap = false)
public interface MixinIFluidReceiverMK2 {
	/*
	@Inject(method = "trySubscribe(Lcom/hbm/inventory/fluid/FluidType;Lnet/minecraft/world/World;IIILcom/hbm/lib/ForgeDirection;)V",at = @At(value = "INVOKE", target = "Lcom/hbm/util/Compat;getTileStandard(Lnet/minecraft/world/World;III)Lnet/minecraft/tileentity/TileEntity;",shift = Shift.AFTER),require = 1)
	default void onTrySubscribe(FluidType type,World world,int x,int y,int z,ForgeDirection dir,CallbackInfo ci) {
		TileEntity te = Compat.getTileStandard(world, x, y, z);
		if (te instanceof IFluidConnectorMK2) {
			IFluidConnectorMK2 con = (IFluidConnectorMK2)te;
			if (!con.canConnect(type, dir.getOpposite())) {
				return;
			}

			GenNode node = UniNodespace.getNode(world, new BlockPos(x, y, z),AmatNet.getProvider(type));
			if (node != null && node.net != null)
				node.net.addReceiver((IFluidReceiverMK2)this);
		}
	}*/
	/**
	 * @author ntmleafia
	 * @reason fuck off
	 */
	@Overwrite
	default void trySubscribe(FluidType type, World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity te = Compat.getTileStandard(world, x, y, z);
		boolean red = false;
		if (te instanceof IFluidConnectorMK2) {
			IFluidConnectorMK2 con = (IFluidConnectorMK2)te;
			if (!con.canConnect(type, dir.getOpposite())) {
				return;
			}

			GenNode node = UniNodespace.getNode(world, new BlockPos(x, y, z), type.getNetworkProvider());
			if (node != null && node.net != null) {
				node.net.addReceiver(this);
				red = true;
			}
			GenNode node2 = UniNodespace.getNode(world, new BlockPos(x, y, z),AmatNet.getProvider(type));
			if (node2 != null && node2.net != null)
				node2.net.addReceiver((IFluidReceiverMK2)this);
		}

	}
}
