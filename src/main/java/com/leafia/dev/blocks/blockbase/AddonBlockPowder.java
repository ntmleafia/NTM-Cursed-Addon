// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.blocks.blockbase;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockFallout;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class AddonBlockPowder extends BlockFallout {
    private static final AxisAlignedBB LAYER = new AxisAlignedBB(0, 0, 0, 1, 0.125, 1);

    public AddonBlockPowder(Material material, SoundType sound, String name) {
        super(material, name);
        setSoundType(sound);
        ModBlocks.ALL_BLOCKS.remove(this);
        AddonBlocks.ALL_BLOCKS.add(this);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return LAYER;
    }
}
