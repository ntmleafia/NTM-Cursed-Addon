package com.leafia.contents.machines.powercores.dfc;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.CoreComponent;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.powercores.dfc.components.creativeemitter.CEmitterTE;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class AddonCoreComponent extends CoreComponent {
	public AddonCoreComponent(Material materialIn,String s) {
		super(materialIn,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
}
