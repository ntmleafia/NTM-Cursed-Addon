// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.contents.oilycoal;

import com.hbm.blocks.ModBlocks;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCoalOil extends BlockOre {
    public BlockCoalOil(String name) {
        setTranslationKey(name);
        setRegistryName(name);
        AddonBlocks.ALL_BLOCKS.add(this);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block changed, BlockPos neighbor) {
        var cursor = new BlockPos.MutableBlockPos();
        for (var face : EnumFacing.VALUES) {
            cursor.setPos(pos.getX() + face.getXOffset(), pos.getY() + face.getYOffset(), pos.getZ() + face.getZOffset());
            var adjacent = world.getBlockState(cursor);
            if (ignites(adjacent)) world.scheduleUpdate(pos, this, world.rand.nextInt(20) + 2);
        }
    }

    private static boolean ignites(IBlockState state) {
        var block = state.getBlock();
        return block == LegacyBlocks.ore_coal_oil_burning || block == Blocks.FIRE
                || block == ModBlocks.balefire || state.getMaterial() == Material.LAVA;
    }

    @Override
    public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion) {
        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        world.setBlockState(pos, LegacyBlocks.ore_coal_oil_burning.getDefaultState());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) { return Items.COAL; }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) { return random.nextInt(2) + 2; }
}
