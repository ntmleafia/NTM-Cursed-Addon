package com.leafia.contents.machines.powercores.dfc;

import com.hbm.util.ContaminationUtil;
import com.hbm.util.I18nUtil;
import com.leafia.dev.blocks.blockbase.AddonBlockBaked;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class OsmiridiumBlock extends AddonBlockBaked {
	public OsmiridiumBlock(Material m,String s) {
		super(m,s);
		this.needsRandomTick = true;
	}
	@Override
	public void addInformation(ItemStack stack,World player,List<String> list,ITooltipFlag advanced) {
		float hardness = this.getExplosionResistance(null);
		if (hardness > 50) {
			list.add(TextFormatting.GOLD + I18nUtil.resolveKey("trait.blastres", hardness));
		}
	}
	@Override
	public void updateTick(World world,BlockPos pos,IBlockState state,Random rand) {
		if (world.isRemote) return;
		ContaminationUtil.radiate(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 32, 0, 40, 0, 0, 0);
	}
}
