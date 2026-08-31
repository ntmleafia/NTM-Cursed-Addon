package com.leafia.contents.fluids;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.fluid.CoriumFinite;
import com.leafia.contents.AddonBlocks;
import com.leafia.unsorted.ParticleBalefireLava;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public class BaleCoriumFluid extends Fluid {
	public static class BaleCoriumFluidBlock extends CoriumFinite {
		public BaleCoriumFluidBlock(Fluid fluid,Material material,String s) {
			super(fluid,material,s);
			tickRate = 20;
			ModBlocks.ALL_BLOCKS.remove(this);
			AddonBlocks.ALL_BLOCKS.add(this);
		}
		private final Random rand = new Random();
		@Override
		public boolean canDisplace(@NotNull IBlockAccess world,@NotNull BlockPos pos) {
			if (rand.nextInt(3) == 0)
				return false;
			IBlockState state = world.getBlockState(pos);
			Material mat = state.getMaterial();
			Block block = state.getBlock();
			if (mat.isLiquid()) return true;
			float resistance = block.getExplosionResistance(null);
			float scaled = (float) (Math.pow(resistance,0.35));
			if (scaled < 1F) return true;
			return rand.nextInt(Math.max(1, (int) scaled)) == 0;
		}
		@Override
		@SideOnly(Side.CLIENT)
		public void randomDisplayTick(IBlockState stateIn,World world,BlockPos pos,Random rand) {
			if (rand.nextInt(5) == 0) {
				if (rand.nextInt(20) == 0)
					world.playSound(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.7F + rand.nextFloat() * 0.25F, false);
				{
					int amt = rand.nextInt(2)+1;
					for (int i = 0; i < amt; i++)
						Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBalefireLava(world,pos.getX()+rand.nextFloat(),pos.getY()+rand.nextFloat(),pos.getZ()+rand.nextFloat()));
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
	public BaleCoriumFluid(String name){
		super(name, new ResourceLocation("leafia","blocks/forgefluid/corium_bale_still"), new ResourceLocation("leafia","blocks/forgefluid/corium_bale_flow"), Color.white);
	}
	public String getUnlocalizedName() {
		return "hbmfluid.balecorium";
	}
}
