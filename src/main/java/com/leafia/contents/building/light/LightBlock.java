package com.leafia.contents.building.light;

import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class LightBlock extends AddonBlockDummyable {
	public final boolean lit;
	public LightBlock(Material materialIn,String s,boolean lit) {
		super(materialIn,s);
		this.lit = lit;
		if (lit)
			this.setLightLevel(1);
	}

	@Override
	public int[] getDimensions() {
		return new int[]{0,0,0,0,1,1};
	}

	@Override
	public ItemStack getPickBlock(IBlockState state,RayTraceResult target,World world,BlockPos pos,EntityPlayer player) {
		return new ItemStack(AddonBlocks.lightUnlit);
	}

	@Override
	public Item getItemDropped(IBlockState state,Random rand,int fortune) {
		return Item.getItemFromBlock(AddonBlocks.lightUnlit);
	}

	static final AxisAlignedBB aabb = new AxisAlignedBB(0,0.75,0,1,1,1);
	@Override
	public @NotNull AxisAlignedBB getBoundingBox(@NotNull IBlockState state,@NotNull IBlockAccess source,@NotNull BlockPos pos) {
		return aabb;
	}

	@Override
	public int getOffset() {
		return 0;
	}

	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		if (meta >= 12)
			return new LightTE();
		return new TileEntityProxyCombo(false,true,false);
	}

	@Override
	public void fillSpace(World world,int x,int y,int z,ForgeDirection dir,int o) {
		super.fillSpace(world,x,y,z,dir,o);
	}

	/// prevent suffocation damage
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
}
