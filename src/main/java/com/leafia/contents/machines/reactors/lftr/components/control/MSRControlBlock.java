// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.lftr.components.control;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.handler.radiation.RadiationSystemNT;
import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.items.tool.ItemTooling;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.util.I18nUtil;
import com.leafia.dev.blocks.blockbase.AddonBlockContainer;
import com.leafia.dev.blocks.blockbase.DirectionalBlockStates;
import com.leafia.dev.machine.MachineTooltip;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MSRControlBlock extends AddonBlockContainer implements ILookOverlay, IRadResistantBlock, ITooltipProvider, IToolable {

	public static final PropertyDirection FACING = DirectionalBlockStates.FACING;

	public MSRControlBlock(Material materialIn,String s) {
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
		worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer).getOpposite()));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new MSRControlTE();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void printHook(Pre event,World world,BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof MSRControlTE control) {
			List<String> texts = new ArrayList<>();
			texts.add(I18nUtil.resolveKey("tile.msr_control.insertion",String.format("%01.2f",control.insertion)));
			ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xFF55FF, 0x3F153F, texts);
		}
	}

	@Override
	public boolean onScrew(World world, EntityPlayer player, int x, int y, int z, EnumFacing side, float fX, float fY, float fZ, EnumHand hand, ToolType tool) {
		return onScrew(world, player, new BlockPos(x, y, z), side, fX, fY, fZ, hand, tool);
	}

	@Override
	public boolean onScrew(World world,EntityPlayer player,BlockPos pos,EnumFacing side,float fX,float fY,float fZ,EnumHand hand,ToolType tool) {
		TileEntity entity = world.getTileEntity(pos);
		if (!(entity instanceof MSRControlTE control))
			return true;
		if (tool.equals(IToolable.ToolType.SCREWDRIVER)) {
			if (!world.isRemote) {
				double ogPos = control.targetInsertion;
				if (player.isSneaking())
					control.targetInsertion = Math.min(control.targetInsertion+0.25,control.length);
				else
					control.targetInsertion = Math.max(control.targetInsertion-0.25,0);
				if (control.targetInsertion == ogPos)
					return false;
				world.playSound(null,pos,HBMSoundHandler.lockOpen,SoundCategory.BLOCKS,0.5f,1);
			}
			return true;
		}
		return false;
	}
}
