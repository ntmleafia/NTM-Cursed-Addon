package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.Balefire;
import com.hbm.blocks.fluid.CoriumFinite;
import com.hbm.blocks.gas.BlockGasMeltdown;
import com.hbm.blocks.gas.BlockGasRadon;
import com.hbm.blocks.gas.BlockGasRadonDense;
import com.hbm.blocks.gas.BlockGasRadonTomb;
import com.hbm.blocks.machine.rbmk.RBMKDebris;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import com.leafia.contents.bomb.balefire.AshBalefire;
import com.leafia.contents.bomb.balefire.BaleoniteBlock;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreck;
import com.leafia.contents.machines.reactors.pwr.blocks.wreckage.PWRMeshedWreckEntity;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSand.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = ExplosionNukeGeneric.class)
public class MixinExplosionNukeGeneric {
	@Shadow(remap = false)
	@Final
	private static Random random;

	@Inject(method = "solinium",at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;",remap = true,shift = Shift.AFTER),remap = false,require = 1,cancellable = true)
	private static void leafia$onSolinium(World world,BlockPos pos,CallbackInfo ci,@Local(type = IBlockState.class) IBlockState state) {
		if (state.getBlock() == LegacyBlocks.waste_dirt || state.getBlock() == LegacyBlocks.scorched_earth) {
			if (random.nextInt(5) < 2) world.setBlockState(pos, Blocks.DIRT.getStateFromMeta(1));
			else world.setBlockState(pos, Blocks.DIRT.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() == LegacyBlocks.waste_gravel) {
			world.setBlockState(pos,Blocks.GRAVEL.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() == LegacyBlocks.waste_ice) {
			world.setBlockState(pos,Blocks.ICE.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() == LegacyBlocks.waste_sand) {
			world.setBlockState(pos,Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT,EnumType.SAND));
			ci.cancel();
			return;
		}
		if (state.getBlock() == LegacyBlocks.waste_sandstone) {
			world.setBlockState(pos,Blocks.SANDSTONE.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() == LegacyBlocks.waste_sand_red) {
			world.setBlockState(pos,Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT,EnumType.RED_SAND));
			ci.cancel();
			return;
		}
		if (state.getBlock() == LegacyBlocks.waste_red_sandstone) {
			world.setBlockState(pos,Blocks.RED_SANDSTONE.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() == LegacyBlocks.waste_snow) {
			world.setBlockState(pos,Blocks.SNOW_LAYER.getDefaultState());
			ci.cancel();
			return;
		}if (state.getBlock() == LegacyBlocks.waste_snow_block) {
			world.setBlockState(pos,Blocks.SNOW.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() == LegacyBlocks.waste_terracotta) {
			world.setBlockState(pos,Blocks.HARDENED_CLAY.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() instanceof CoriumFinite) {
			world.setBlockState(pos,ModBlocks.sellafield_slaked.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() == ModBlocks.block_corium || state.getBlock() == ModBlocks.block_corium_cobble || state.getBlock() == ModBlocks.sellafield) {
			world.setBlockState(pos,ModBlocks.sellafield_slaked.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() instanceof BlockGasRadon || state.getBlock() instanceof BlockGasRadonDense || state.getBlock() instanceof BlockGasRadonTomb || state.getBlock() instanceof BlockGasMeltdown) {
			world.setBlockToAir(pos);
			ci.cancel();
			return;
		}
		if (state.getBlock() != ModBlocks.pribris && state.getBlock() != ModBlocks.pribris_digamma && state.getBlock() instanceof RBMKDebris) {
			world.setBlockState(pos,ModBlocks.pribris.getDefaultState());
			ci.cancel();
			return;
		}
		if (state.getBlock() instanceof PWRMeshedWreck) {
			if (world.getTileEntity(pos) instanceof PWRMeshedWreckEntity te) {
				te.scorch = 0;
				te.markDirty();
			}
		}
		if (state.getBlock() instanceof AshBalefire)
			world.setBlockToAir(pos);
		if (state.getBlock() instanceof Balefire)
			world.setBlockState(pos,Blocks.FIRE.getDefaultState());
		if (state.getBlock() instanceof BaleoniteBlock)
			world.setBlockState(pos,ModBlocks.sellafield_slaked.getDefaultState());
	}
}
