// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockBakeBase;
import com.hbm.blocks.generic.BlockSpeedy;
import com.leafia.contents.building.generic.lined_asphalt.AsphaltBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BlockSpeedy.class)
public abstract class MixinBlockSpeedy extends BlockBakeBase {
	public MixinBlockSpeedy(Material m,String s) {
		super(m,s);
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (this.equals(ModBlocks.asphalt))
			return AsphaltBlock.paintBlock(worldIn,pos,state,playerIn,hand,facing,hitX,hitY,hitZ);
		return super.onBlockActivated(worldIn,pos,state,playerIn,hand,facing,hitX,hitY,hitZ);
	}
	@Inject(method = "onPlayerStep",at = @At(value = "HEAD"),require = 1,cancellable = true,remap = false)
	public void leafia$onOnPlayerStep(World world,int x,int y,int z,EntityPlayer player,CallbackInfo ci) {
		if (this.equals(ModBlocks.asphalt))
			ci.cancel();
	}
	@SideOnly(Side.CLIENT)
	@Inject(method = "addInformation",at = @At(value = "HEAD"),require = 1,cancellable = true)
	public void leafia$onAddInformation(ItemStack stack,World player,List<String> tooltip,ITooltipFlag advanced,CallbackInfo ci) {
		if (this.equals(ModBlocks.asphalt))
			ci.cancel();
	}
}
