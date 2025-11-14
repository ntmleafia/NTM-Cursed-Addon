package com.leafia.contents.network.spk_cable;

import com.leafia.contents.network.BlockCable;
import com.leafia.contents.network.spk_cable.uninos.ISPKConnector;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SPKCableBlock extends BlockCable {
    public SPKCableBlock(Material materialIn,String s) {
        super(materialIn,s);
    }
    @Override
    public TileEntity createNewTileEntity(World worldIn,int meta) {
        return new SPKCableTE();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess world,BlockPos pos) {
        if (world.getTileEntity(pos) instanceof SPKCableTE) {
            boolean pX = world.getTileEntity(pos.add(1, 0, 0)) instanceof ISPKConnector;
            boolean nX = world.getTileEntity(pos.add(-1, 0, 0)) instanceof ISPKConnector;
            boolean pY = world.getTileEntity(pos.add(0, 1, 0)) instanceof ISPKConnector;
            boolean nY = world.getTileEntity(pos.add(0, -1, 0)) instanceof ISPKConnector;
            boolean pZ = world.getTileEntity(pos.add(0, 0, 1)) instanceof ISPKConnector;
            boolean nZ = world.getTileEntity(pos.add(0, 0, -1)) instanceof ISPKConnector;
            return getBB(pX,pY,pZ,nX,nY,nZ);
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
        super.addInformation(stack,worldIn,tooltip,flagIn);
        tooltip.add("You know what? Fuck the item model");
        tooltip.add("We tried, minecraft just refuses to render it.");
    }
}
