package com.leafia.contents.network.ff_duct.utility.filter;

import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityBase;
import com.leafia.dev.blocks.ICustomItemBlockProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FFFilterBlock extends FFDuctUtilityBase implements ICustomItemBlockProvider {
	public FFFilterBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new FFFilterTE2();
	}
	@Override
	public void onBlockPlacedBy(World worldIn,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof FFFilterTE2 filter) {
			if (stack.hasTagCompound()) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag.hasKey("filter")) {
					try {
						filter.filter = MSRFuel.valueOf(tag.getString("filter"));
						return;
					} catch (IllegalArgumentException ignored) {}
				}
			}
		}
		worldIn.destroyBlock(pos,true);
	}

	@Override
	public ItemBlock provideItem() {
		return new FFFilterItem(this);
	}
}
