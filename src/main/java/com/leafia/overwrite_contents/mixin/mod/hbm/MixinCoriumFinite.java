package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.fluid.CoriumFinite;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(value = CoriumFinite.class)
public class MixinCoriumFinite extends BlockFluidFinite {
	public MixinCoriumFinite(Fluid fluid,Material material) {
		super(fluid,material);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn,World world,BlockPos pos,Random rand) {
		super.randomDisplayTick(stateIn,world,pos,rand);
		if (rand.nextInt(5) == 0) {
			if (rand.nextInt(20) == 0)
				world.playSound(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.7F + rand.nextFloat() * 0.25F, false);
			{
				int amt = rand.nextInt(2)+1;
				for (int i = 0; i < amt; i++)
					world.spawnParticle(
							EnumParticleTypes.LAVA,
							pos.getX()+rand.nextFloat(),pos.getY()+rand.nextFloat(),pos.getZ()+rand.nextFloat(),
							0,0,0
					);
			}
			{
				int amt = rand.nextInt(3);
				for (int i = 0; i < amt; i++)
					world.spawnParticle(
							EnumParticleTypes.SMOKE_NORMAL,
							pos.getX()+rand.nextFloat(),pos.getY()+rand.nextFloat(),pos.getZ()+rand.nextFloat(),
							rand.nextDouble()/2-0.25,rand.nextDouble()/2-0.25,rand.nextDouble()/2-0.25
					);
			}
			{
				int amt = rand.nextInt(3);
				for (int i = 0; i < amt; i++)
					world.spawnParticle(
							EnumParticleTypes.SMOKE_LARGE,
							pos.getX()+rand.nextFloat(),pos.getY()+rand.nextFloat(),pos.getZ()+rand.nextFloat(),
							rand.nextDouble()/2-0.25,rand.nextDouble()/2-0.25,rand.nextDouble()/2-0.25
					);
			}
		}
		if (rand.nextInt(100) == 0)
			world.playSound(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.7F + rand.nextFloat() * 0.25F, false);
	}
}
