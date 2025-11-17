package com.leafia.contents.machines.powercores.dfc;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.CoreComponent;
import com.leafia.AddonBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.powercores.dfc.components.creativeemitter.TileEntityCoreCreativeEmitter;
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

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (this == AddonBlocks.dfc_cemitter) return new TileEntityCoreCreativeEmitter();
        else return super.createNewTileEntity(worldIn, meta);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && !player.isSneaking() && this == AddonBlocks.dfc_cemitter) {
            FMLNetworkHandler.openGui(player, AddonBase.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        } else return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }
}
