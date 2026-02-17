package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.machine.MachineChemicalPlant;
import com.leafia.dev.firestorm.IFirestormBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MachineChemicalPlant.class)
public class MixinMachineChemicalPlant implements IFirestormBlock {
	@Inject(method = "onBlockActivated",at = @At(value = "HEAD"),require = 1,cancellable = true)
	public void onOnBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ,CallbackInfoReturnable<Boolean> cir) {
		if (isDestroyed(world,pos)) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
