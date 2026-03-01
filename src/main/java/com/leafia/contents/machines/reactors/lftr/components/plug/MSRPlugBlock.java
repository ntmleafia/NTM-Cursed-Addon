package com.leafia.contents.machines.reactors.lftr.components.plug;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.BlockMachineBase;
import com.hbm.handler.radiation.RadiationSystemNT;
import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.fluids.traits.FT_LFTRCoolant;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityTEBase;
import com.leafia.dev.machine.MachineTooltip;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MSRPlugBlock extends BlockMachineBase implements ILookOverlay, IRadResistantBlock, ITooltipProvider {
	public MSRPlugBlock(Material materialIn,String s) {
		super(materialIn,0,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.onBlockAdded(worldIn, pos, state);
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addMultiblock(tooltip);
		MachineTooltip.addModular(tooltip);
		addStandardInfo(tooltip);
		super.addInformation(stack,worldIn,tooltip,flagIn);
		tooltip.add("§2[" + I18nUtil.resolveKey("trait.radshield") + "]");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return new MSRPlugTE();
	}
	@Override
	protected boolean rotatable() {
		return true;
	}
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getItem() instanceof IItemFluidIdentifier identifier) {
			FluidType ntmf = identifier.getType(worldIn,pos.getX(),pos.getY(),pos.getZ(),stack);
			if (!ntmf.hasTrait(FT_LFTRCoolant.class)) return false;
			if (!worldIn.isRemote) {
				TileEntity te = worldIn.getTileEntity(pos);
				if (te instanceof MSRPlugTE plug)
					plug.setType(ntmf);
			}
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void printHook(RenderGameOverlayEvent.Pre event,World world,BlockPos pos) {
		List<String> texts = new ArrayList<>();
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof MSRPlugTE plug) {
			texts.add("&[" + plug.inputType.getColor() + "&]" + plug.inputType.getLocalizedName());
			if (plug.molten)
				texts.add(TextFormatting.DARK_RED+I18nUtil.resolveKey("tile.msr_plug.molten"));
		}
		MSRTEBase.appendPrintHook(texts,world,pos);
		LeafiaGls.pushMatrix();
		LeafiaGls.scale(0.5);
		ScaledResolution resolution = event.getResolution();
		LeafiaGls.translate(resolution.getScaledHeight_double(),resolution.getScaledHeight_double()/2,0);
		ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xFF55FF, 0x3F153F, texts);
		LeafiaGls.popMatrix();
	}
}
