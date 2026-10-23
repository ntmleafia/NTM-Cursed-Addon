// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockStorageCrate;
import com.hbm.main.MainRegistry;
import com.hbm.util.I18nUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BlockStorageCrate.class)
public abstract class MixinBlockStorageCrate extends BlockContainer implements IToolable {
	public MixinBlockStorageCrate(Material materialIn) { super(materialIn); }
	@SideOnly(Side.CLIENT)
	@Inject(method = "addInformation",at = @At(value = "HEAD"),require = 1)
	public void onAddInformation(ItemStack stack,World worldIn,List<String> list,ITooltipFlag flagIn,CallbackInfo ci) {
		if (this == ModBlocks.crate_tungsten)
			list.add(TextFormatting.RED+I18nUtil.resolveKey("tile.crate_tungsten.desc"));
		if (
				this == ModBlocks.crate_iron
						|| this == ModBlocks.crate_steel
						|| this == ModBlocks.crate_tungsten
						|| this == ModBlocks.crate_desh
		)
			list.add(I18nUtil.resolveKey("desc.leafia.crate_labelling"));
	}

	@Override
	public boolean onScrew(World world, EntityPlayer player, int x, int y, int z, EnumFacing side, float fX, float fY, float fZ, EnumHand hand, ToolType tool) {
		return onScrew(world, player, new BlockPos(x, y, z), side, fX, fY, fZ, hand, tool);
	}

	@Override
	public boolean onScrew(World world,EntityPlayer player,BlockPos pos,EnumFacing side,float fX,float fY,float fZ,EnumHand hand,ToolType tool) {
		if (side.getAxis().isHorizontal() && tool == ToolType.SCREWDRIVER) {
			if (
					this == ModBlocks.crate_iron
					|| this == ModBlocks.crate_steel
					|| this == ModBlocks.crate_tungsten
					|| this == ModBlocks.crate_desh
			) {
				if (!world.isRemote)
					FMLNetworkHandler.openGui(player,MainRegistry.instance,1121+side.getHorizontalIndex(),world,pos.getX(),pos.getY(),pos.getZ());
				return true;
			}
		}
		return false;
	}
}
