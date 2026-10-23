// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.fluids;

import com.hbm.blocks.fluid.IFluidFog;
import com.leafia.contents.AddonBlocks;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
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

public class OsmiridiumFluid extends Fluid {
	public static class OsmiridiumFluidBlock extends BlockFluidFinite implements IFluidFog {

		private final Random rand = new Random();

		public OsmiridiumFluidBlock(Fluid fluid,Material material,String s) {
			super(fluid, material);
			this.setTranslationKey(s);
			this.setRegistryName(s);
			this.tickRate = 30;
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
		public boolean canDisplace(@NotNull IBlockAccess world,@NotNull BlockPos pos) {
			IBlockState state = world.getBlockState(pos);
			Material mat = state.getMaterial();
			Block block = state.getBlock();
			if (mat.isLiquid()) return true;
			float resistance = block.getExplosionResistance(null);
			float scaled = (float) (Math.sqrt(resistance) * 1.2);
			if (scaled < 1F) return true;
			return rand.nextInt(Math.max(1, (int) scaled)) == 0;
		}

		@Override
		public boolean displaceIfPossible(@NotNull World world, @NotNull BlockPos pos) {
			if (world.getBlockState(pos).getMaterial().isLiquid()) {
				return false;
			}
			return canDisplace(world, pos);
		}

		@Override
		public void onEntityCollision(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Entity entity) {
			if (entity instanceof EntityPlayerMP playerMP && (playerMP.isSpectator() || playerMP.isCreative())) return;
			entity.setInWeb();
			entity.setFire(3);
			entity.attackEntityFrom(DamageSource.IN_FIRE, 4F);
			if (entity instanceof EntityLivingBase) {
				ContaminationUtil.contaminate((EntityLivingBase) entity, HazardType.DIGAMMA, ContaminationType.CREATIVE, 1F/200);
			}
		}

		@Override
		public void updateTick(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random random) {
			super.updateTick(world, pos, state, random);
			ContaminationUtil.radiate(world,pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f,60,0,85,0);
			if (!world.isRemote && random.nextInt(10) == 0)
				world.setBlockToAir(pos);
		}

		@Override
		public float getFogDensity() {
			return 2.0F;
		}

		@Override
		public int getFogColor() {
			return 0x343435;
		}
	}
	public OsmiridiumFluid(String name){
		super(name, new ResourceLocation("leafia","blocks/forgefluid/osmiridium_still"), new ResourceLocation("leafia","blocks/forgefluid/osmiridium_flow"), Color.white);
	}
	public String getUnlocalizedName() {
		return "hbmfluid.corecomponent";
	}
}
