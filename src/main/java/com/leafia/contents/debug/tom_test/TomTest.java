package com.leafia.contents.debug.tom_test;

import com.hbm.entity.effect.EntityCloudTom;
import com.hbm.entity.logic.EntityTomBlast;
import com.leafia.dev.blocks.blockbase.AddonBlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TomTest extends AddonBlockBase {
	public TomTest(Material m,String s) {
		super(m,s);
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (!worldIn.isRemote) {
			/*EntityTomBlast tom = new EntityTomBlast(worldIn);
			tom.posX = pos.getX();
			tom.posY = pos.getY();
			tom.posZ = pos.getZ();
			tom.destructionRange = 30;
			worldIn.spawnEntity(tom);

			EntityCloudTom cloud = new EntityCloudTom(worldIn, 30);
			cloud.setLocationAndAngles(pos.getX(),pos.getY(),pos.getZ(),0,0);
			worldIn.spawnEntity(cloud);
*/
			worldIn.setBlockToAir(pos);
		}
		return true;
	}
}
