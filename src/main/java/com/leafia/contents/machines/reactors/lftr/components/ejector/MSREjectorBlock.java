// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.lftr.components.ejector;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.handler.radiation.RadiationSystemNT;
import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.util.I18nUtil;
import com.leafia.dev.blocks.blockbase.AddonBlockContainer;
import com.leafia.dev.blocks.blockbase.DirectionalBlockStates;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.machines.reactors.lftr.components.plug.MSRPlugTE;
import com.leafia.dev.machine.MachineTooltip;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MSREjectorBlock extends AddonBlockContainer implements ITooltipProvider, IRadResistantBlock, ILookOverlay {

	public static final PropertyDirection FACING = DirectionalBlockStates.FACING;

	public MSREjectorBlock(Material materialIn,String s) {
		super(materialIn, s);
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
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new MSREjectorTE();
	}

	@Override
	protected BlockStateContainer createBlockState() {
        return DirectionalBlockStates.createContainer(this);
    }

	@Override
	public int getMetaFromState(IBlockState state) {
        return DirectionalBlockStates.metadata(state);
    }

	@Override
	public IBlockState getStateFromMeta(int meta) {
        return DirectionalBlockStates.fromMetadata(getDefaultState(), meta);
    }

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
        return DirectionalBlockStates.rotate(state, rot);
    }

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return DirectionalBlockStates.mirror(state, mirrorIn);
    }

	@Override
	public void onBlockPlacedBy(World worldIn,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void printHook(RenderGameOverlayEvent.Pre event,World world,BlockPos pos) {
		List<String> texts = new ArrayList<>();
		MSRTEBase.appendPrintHook(texts,world,pos);
		LeafiaGls.pushMatrix();
		LeafiaGls.scale(0.5);
		ScaledResolution resolution = event.getResolution();
		LeafiaGls.translate(resolution.getScaledHeight_double(),resolution.getScaledHeight_double()/2,0);
		ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xFF55FF, 0x3F153F, texts);
		LeafiaGls.popMatrix();
	}
}
