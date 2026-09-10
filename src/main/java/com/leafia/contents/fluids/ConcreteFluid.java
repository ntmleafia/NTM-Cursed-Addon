package com.leafia.contents.fluids;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.fluid.IFluidFog;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;

public class ConcreteFluid extends Fluid {
	public static class ConcreteFluidBlock extends BlockFluidFinite implements IFluidFog {

		private final Random rand = new Random();

		public ConcreteFluidBlock(Fluid fluid,Material material,String s) {
			super(fluid, material);
			this.setTranslationKey(s);
			this.setRegistryName(s);
			this.tickRate = 40;
			this.setTickRandomly(true);
			displacements.put(this, false);

			AddonBlocks.ALL_BLOCKS.add(this);
		}

		@Override
		@NotNull
		public IBlockState getStateForPlacement(World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,
		                                        EntityLivingBase placer,EnumHand hand) {
			return getDefaultState().withProperty(LEVEL, Math.max(0, quantaPerBlock - 1));
		}

		@Override
		public void onEntityCollision(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Entity entity) {
			if (entity instanceof EntityPlayerMP playerMP && (playerMP.isSpectator() || playerMP.isCreative())) return;
			entity.setInWeb();
			if (entity instanceof EntityLivingBase) {
				if (new BlockPos(entity.posX,entity.posY+entity.getEyeHeight(),entity.posZ).equals(pos))
					entity.attackEntityFrom(DamageSource.IN_WALL, 1F);
			}
		}

		@Override
		public void updateTick(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random random) {
			super.updateTick(world, pos, state, random);
			if (!world.isRemote && random.nextInt(10) == 0)
				world.setBlockState(pos,ModBlocks.concrete_smooth.getDefaultState());
		}

		@Override
		public float getFogDensity() {
			return 2.0F;
		}

		@Override
		public int getFogColor() {
			return 0x797979;
		}
	}
	public ConcreteFluid(String name){
		super(name, new ResourceLocation("leafia","blocks/forgefluid/concrete_static"), new ResourceLocation("leafia","blocks/forgefluid/concrete_flow"), Color.white);
	}
	public String getUnlocalizedName() {
		return "hbmfluid.concrete";
	}
}
