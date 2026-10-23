// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.debug.rbmk_jet;

import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import com.leafia.contents.machines.reactors.rbmk.effects.ParticleRBMKJet;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DebugRBMKJetEmitter extends AddonBlockBase {
	public DebugRBMKJetEmitter(Material m,String s) {
		super(m,s);
	}

	@SideOnly(Side.CLIENT)
	void emit(World worldIn,BlockPos pos) {
		ParticleRBMKJet jet = new ParticleRBMKJet(worldIn,pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,30);
		Minecraft.getMinecraft().effectRenderer.addEffect(jet);
	}

	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (worldIn.isRemote)
			emit(worldIn,pos);
		return true;
	}
}
