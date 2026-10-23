// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.world.gen.component.BunkerComponents.PowerRoom;
import com.hbm.world.gen.component.Component;
import com.leafia.contents.AddonBlocks.PWR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PowerRoom.class)
public abstract class MixinBunkerComponents$PowerRoom extends Component {
	@Redirect(method = "addComponentParts",at = @At(value = "INVOKE", target = "Lcom/hbm/world/gen/component/BunkerComponents$PowerRoom;setBlockState(Lnet/minecraft/world/World;Lnet/minecraft/block/state/IBlockState;IIILnet/minecraft/world/gen/structure/StructureBoundingBox;)V"),require = 1)
	void leafia$onAddComponentParts(PowerRoom instance,World world,IBlockState iBlockState,int x,int y,int z,StructureBoundingBox structureBoundingBox) {
		if (iBlockState.getBlock() == ModBlocks.pwr_fuelrod)
			iBlockState = PWR.element_old.getDefaultState();
		if (iBlockState.getBlock() == ModBlocks.pwr_control)
			iBlockState = PWR.control.getDefaultState();
		if (iBlockState.getBlock() == ModBlocks.pwr_channel)
			iBlockState = PWR.channel.getDefaultState();
		setBlockState(world,iBlockState,x,y,z,boundingBox);
	}
}
