// SPDX-FileCopyrightText: 2023 Team Galacticraft
// SPDX-FileContributor: Leafia
// SPDX-License-Identifier: MIT

package micdoodle8.mods.galacticraft.api.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPartialSealableBlock
{

    boolean isSealed(World world, BlockPos pos, EnumFacing direction);
}
