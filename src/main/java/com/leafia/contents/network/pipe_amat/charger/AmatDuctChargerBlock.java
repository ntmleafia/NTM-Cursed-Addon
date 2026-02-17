package com.leafia.contents.network.pipe_amat.charger;

import com.hbm.blocks.ILookOverlay;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.util.I18nUtil;
import com.leafia.dev.blocks.blockbase.AddonBlockBaked;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AmatDuctChargerBlock extends AddonBlockBaked implements ITileEntityProvider, ILookOverlay {
	public AmatDuctChargerBlock(Material m,String s) {
		super(m,s);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new AmatDuctChargerTE();
	}
	@Override
	public void printHook(Pre pre,World world,int x,int y,int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		if (!(te instanceof AmatDuctChargerTE charger))
			return;

		List<String> text = new ArrayList<>();
		text.add("&[" + charger.getType().getColor() + "&]" + charger.getType().getLocalizedName());
		text.add(I18nUtil.resolveKey("tile.amat_duct.power",charger.power+" HE"));
		ILookOverlay.printGeneric(pre, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xffff00, 0x404000, text);
	}

	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getItem() instanceof IItemFluidIdentifier identifier) {
			if (!worldIn.isRemote) {
				TileEntity te = worldIn.getTileEntity(pos);
				if (te instanceof AmatDuctChargerTE charger)
					charger.setType(identifier.getType(worldIn,pos.getX(),pos.getY(),pos.getZ(),stack));
			}
			return true;
		}
		return false;
	}
}
