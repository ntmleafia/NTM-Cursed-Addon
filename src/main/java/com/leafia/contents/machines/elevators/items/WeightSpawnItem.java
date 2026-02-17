package com.leafia.contents.machines.elevators.items;

import com.leafia.dev.items.itembase.AddonItemBase;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeightSpawnItem extends AddonItemBase {
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addWIP(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
	public WeightSpawnItem(String s) {
		super(s);
		setMaxStackSize(1);
	}
}
