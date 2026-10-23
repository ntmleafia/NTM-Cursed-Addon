// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.contents.oilycoal;

import com.leafia.contents.AddonBlocks;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCoalBurning extends BlockOre {
    public BlockCoalBurning(String name) {
        setTranslationKey(name);
        setRegistryName(name);
        AddonBlocks.ALL_BLOCKS.add(this);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        for (var face : EnumFacing.VALUES) {
            if (face == EnumFacing.DOWN || world.getBlockState(pos.offset(face)).getMaterial() != Material.AIR) continue;
            // Retain all four draws per exposed face so other effects keep the same random stream.
            double alongX = random.nextDouble();
            double alongY = random.nextDouble();
            double alongZ = random.nextDouble();
            double outward = random.nextDouble();
            double x = faceCoordinate(pos.getX(), face.getXOffset(), alongX, outward);
            double y = faceCoordinate(pos.getY(), face.getYOffset(), alongY, outward);
            double z = faceCoordinate(pos.getZ(), face.getZOffset(), alongZ, outward);
            world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0.1, 0);
        }
    }

    private static double faceCoordinate(int position, int normal, double along, double outward) {
        return normal == 0 ? position + 0.5F + along - 0.5D
                : position + 0.5F + normal * 0.5D + outward * 0.125D * normal;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) { return Items.AIR; }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
        super.breakBlock(world, pos, state);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) { entity.setFire(3); }
}
