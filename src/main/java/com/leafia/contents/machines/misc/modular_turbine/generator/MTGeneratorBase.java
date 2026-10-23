// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.generator;

import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreData.MachineUpgradeSummary;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class MTGeneratorBase extends ModularTurbineBlockBase implements IMTGeneratorBlock {
	public MTGeneratorBase(String s) {
		super(s);
	}
	@Override
	public void contributeMachineUpgradeOffsets(MachineUpgradeSummary summary,MTCoreTE core,ModularTurbineComponentTE component) {
		summary.addGeneratorLoadCoefficientOffset(generatorPower());
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		super.addInformation(stack,worldIn,tooltip,flagIn);
		tooltip.add(TextFormatting.GOLD+I18nUtil.resolveKey("info.turbine._tooltip.power",generatorPower()));
	}
}
