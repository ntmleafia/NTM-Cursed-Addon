package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.machine.MachineArcWelder;
import com.hbm.util.I18nUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = MachineArcWelder.class)
public abstract class MixinMachineArcWelder extends BlockDummyable {
	public MixinMachineArcWelder(Material materialIn,String s) {
		super(materialIn,s);
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.RED+I18nUtil.resolveKey("tile.machine_arc_welder.warn"));
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
}
