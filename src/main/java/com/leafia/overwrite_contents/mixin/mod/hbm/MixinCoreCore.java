package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.machine.CoreCore;
import com.leafia.dev.MachineTooltip;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = CoreCore.class)
public abstract class MixinCoreCore extends BlockContainer {
	protected MixinCoreCore(Material materialIn) {
		super(materialIn);
	}
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState,IBlockAccess worldIn,BlockPos pos)
	{
		return NULL_AABB;
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World player,List<String> tooltip,ITooltipFlag advanced) {
		MachineTooltip.addMultiblock(tooltip);
		MachineTooltip.addModular(tooltip);
		MachineTooltip.addCore(tooltip);
		super.addInformation(stack,player,tooltip,advanced);
	}
}
