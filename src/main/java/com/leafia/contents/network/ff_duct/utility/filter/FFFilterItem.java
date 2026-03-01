package com.leafia.contents.network.ff_duct.utility.filter;

import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FFFilterItem extends ItemBlock {
	public FFFilterItem(Block block) {
		super(block);
	}
	@Override
	public EnumActionResult onItemUse(EntityPlayer player,World worldIn,BlockPos pos,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null)
				return EnumActionResult.FAIL;
			if (compound.hasKey("filter")) {
				return super.onItemUse(player,worldIn,pos,hand,facing,hitX,hitY,hitZ);
			}
		}
		return EnumActionResult.FAIL;
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn,EntityPlayer playerIn,EnumHand handIn) {
		if (!worldIn.isRemote) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null)
				compound = new NBTTagCompound();
			int index = -1;
			if (compound.hasKey("filter")) {
				String filter = compound.getString("filter");
				try {
					index = MSRFuel.valueOf(filter).ordinal();
				} catch (IllegalArgumentException ignored) { }
			}
			index++;
			if (index >= MSRFuel.values().length)
				index = 0;
			String name = MSRFuel.values()[index].name();
			compound.setString("filter",name);
			stack.setTagCompound(compound);
			playerIn.inventoryContainer.detectAndSendChanges();
			playerIn.sendMessage(new TextComponentTranslation("tile.ff_filter.message",new TextComponentTranslation("tile.msr.fuel."+name)).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
		}
		return super.onItemRightClick(worldIn,playerIn,handIn);
	}

	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		if (stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound == null)
				return;
			if (compound.hasKey("filter")) {
				String filter = compound.getString("filter");
				try {
					MSRFuel fuel = MSRFuel.valueOf(filter);
					tooltip.add(TextFormatting.LIGHT_PURPLE+I18nUtil.resolveKey("tile.ff_filter.filter",I18nUtil.resolveKey("tile.msr.fuel."+fuel.name())));
				} catch (IllegalArgumentException ignored) {
					tooltip.add(TextFormatting.LIGHT_PURPLE+I18nUtil.resolveKey("tile.ff_filter.filter",TextFormatting.RED+"[ERROR]"));
				}
			}
		}
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}
}
