package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.BlockBase;
import com.hbm.blocks.generic.BlockHazard;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockHazard.class)
public class MixinBlockHazard extends BlockBase {
	@Shadow(remap = false)
	private float rad3d;

	@Inject(method = "updateTick",at = @At(value = "INVOKE", target = "Lcom/hbm/handler/radiation/ChunkRadiationManager$ProxyClass;incrementRad(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;DD)V",remap = false),require = 1)
	public void leafia$onUpdateTick(World worldIn,BlockPos pos,IBlockState state,Random rand,CallbackInfo ci) {
		if (rad3d <= 0)
			worldIn.scheduleUpdate(pos,this,this.tickRate(worldIn));
	}
}
