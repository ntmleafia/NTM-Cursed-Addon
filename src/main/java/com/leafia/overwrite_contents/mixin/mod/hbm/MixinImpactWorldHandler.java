package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.handler.ImpactWorldHandler;
import com.leafia.database.ImpactSeismic;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImpactWorldHandler.class)
public class MixinImpactWorldHandler {
	@Inject(method = "impactEffects",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getPos()Lnet/minecraft/util/math/ChunkPos;",remap = true),remap = false)
	private static void leafia$onImpactEffects(World world,CallbackInfo ci,@Local(name = "chunk") Chunk chunk) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					BlockPos pos = new BlockPos(chunk.getPos().getXStart()+x,y,chunk.getPos().getZStart()+z);
					IBlockState state = world.getBlockState(pos);
					Block block = state.getBlock();
					ImpactSeismic.tryCollapseBlock(world,pos,state,block);
				}
			}
		}
	}
}
