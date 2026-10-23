// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.fluids;

import com.leafia.contents.AddonBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import java.awt.*;

public class FluorideFluid extends Fluid {
	public static class FluorideFluidBlock extends BlockFluidClassic {

		private DamageSource damageSource;

		public FluorideFluidBlock(Fluid fluid,Material material,String s) {
			super(fluid, material);
            //mlbv: the cast here must not be removed, bug within RetroFuturaGradle reobfuscation
			((Block)this).setTranslationKey(s);
			this.setRegistryName(s);
			((Block)this).setCreativeTab(null);
			//this.setQuantaPerBlock(4);
			this.damageSource = DamageSource.ON_FIRE;
			this.displacements.put(this, false);
			//this.tickRate = 30;

			AddonBlocks.ALL_BLOCKS.add(this);
		}

		@Override
		public Vec3d getFogColor(World world,BlockPos pos,IBlockState state,Entity entity,Vec3d originalColor,float partialTicks) {
			return new Vec3d(0.851,0.867,0.765);
		}

		@Override
		public boolean canDisplace(IBlockAccess world,BlockPos pos) {
			//if(world.getBlockState(pos).getMaterial().isLiquid())
			//	return true;
			return super.canDisplace(world, pos);
		}

		// @Override
		// public boolean displaceIfPossible(World world, BlockPos pos) {
		// 	return super.displaceIfPossible(world, pos);
		// }

		@Override
		public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
			//entity.setInWeb();
			entity.setFire(3);
		}

		@Override
		public int tickRate(World world) {
			return 30;
		}
	}
	public FluorideFluid(String name){
		super(name, new ResourceLocation("leafia","blocks/forgefluid/fluoride_still"), new ResourceLocation("leafia","blocks/forgefluid/fluoride_flow"), Color.white);
	}
	public String getUnlocalizedName() {
		return "hbmfluid.flibe";
	}
}
