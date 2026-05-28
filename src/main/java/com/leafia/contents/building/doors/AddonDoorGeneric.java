package com.leafia.contents.building.doors;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockDoorGeneric;
import com.hbm.tileentity.DoorDecl;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AddonDoorGeneric extends BlockDoorGeneric {
	public AddonDoorGeneric(Material materialIn,DoorDecl type,boolean isRadResistant,String s) {
		super(materialIn,type,isRadResistant,s);
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return meta >= 12 ? new AddonDoorTE() : null;
	}
}
