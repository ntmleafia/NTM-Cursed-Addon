// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.pwr.blocks;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockRadResistant;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PWRReflectorBlock extends BlockRadResistant implements ITooltipProvider {
    public PWRReflectorBlock() {
        super(Material.IRON,"lwr_reflector");
        setSoundType(SoundType.METAL);
        ModBlocks.ALL_BLOCKS.remove(this);
        AddonBlocks.ALL_BLOCKS.add(this);
    }
    @Override
    public void addInformation(ItemStack stack,@Nullable World player,List<String> tooltip,ITooltipFlag advanced) {
        MachineTooltip.addMultiblock(tooltip);
        MachineTooltip.addModular(tooltip);
        addStandardInfo(tooltip);
        super.addInformation(stack,player,tooltip,advanced);
    }
}
