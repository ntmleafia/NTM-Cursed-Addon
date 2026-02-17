package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockMeta;
import com.hbm.explosion.ExplosionBalefire;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ExplosionBalefire.class,remap = false)
public class MixinExplosionBalefire {
	@Shadow
	public int radius;

	@Shadow
	public int posX;

	@Shadow
	public int posZ;

	@Shadow
	public World worldObj;

	@Shadow
	public int posY;

	/**
	 * @author ntmleafia
	 * @reason use LCE algorithm
	 */
	@Overwrite
	private void breakColumn(int x,int z)
	{
		double dist = Math.sqrt(x * x + z * z);
		int amplitude = (int) (radius - dist);

		if (amplitude > 0) {
			int pX = posX + x;
			int pZ = posZ + z;

			int y  = posY+5;//worldObj.getHeight(pX, pZ);
			int ystart = y;
			for (int y0 = y; y0 >= 0; y0--) {
				BlockPos p = new BlockPos(pX,y0,pZ);
				if (worldObj.getBlockState(p).getBlock().isReplaceable(worldObj,p)) worldObj.setBlockToAir(p);
				if (worldObj.isAirBlock(p)) y = y0;
				else break;
			}
			int maxdepth = (int) (10 + radius * 0.25);
			int voidDepth = (int) ((maxdepth * amplitude / radius) + (Math.sin(amplitude * 0.15 + 2) * 2));//

			int yend = (int)(ystart+maxdepth*2.5*Math.pow(Math.cos(dist/(double)radius*Math.PI/2),0.375))-worldObj.rand.nextInt(5);
			for (int yc = ystart; yc < yend-10; yc++)
				worldObj.setBlockState(new BlockPos(pX,yc,pZ),Blocks.AIR.getDefaultState(),2);
			int depth = Math.max(y - voidDepth, 0);

			while(y > depth) {

				Block b = worldObj.getBlockState(new BlockPos(pX, y, pZ)).getBlock();

				if(b == ModBlocks.block_schrabidium_cluster) {

					if(worldObj.rand.nextInt(10) == 0) {
						worldObj.setBlockState(new BlockPos(pX, y + 1, pZ), ModBlocks.balefire.getDefaultState());
						worldObj.setBlockState(new BlockPos(pX, y, pZ), ModBlocks.block_euphemium_cluster.getStateFromMeta(b.getMetaFromState(worldObj.getBlockState(new BlockPos(pX, y, pZ)))), 3);
					}
					return;
				} else if(b == ModBlocks.cmb_brick_reinforced){
					if(worldObj.rand.nextInt(10) == 0) {
						worldObj.setBlockState(new BlockPos(pX, y + 1, pZ), ModBlocks.balefire.getDefaultState());
					}
					return;
				}

				worldObj.setBlockToAir(new BlockPos(pX, y, pZ));

				y--;
			}

			if(worldObj.rand.nextInt(10) == 0) {
				worldObj.setBlockState(new BlockPos(pX, depth + 1, pZ), ModBlocks.balefire.getDefaultState());

				Block b = worldObj.getBlockState(new BlockPos(pX, y, pZ)).getBlock();

				if(b == ModBlocks.block_schrabidium_cluster)
					worldObj.setBlockState(new BlockPos(pX, y, pZ), ModBlocks.block_euphemium_cluster.getStateFromMeta(b.getMetaFromState(worldObj.getBlockState(new BlockPos(pX, y, pZ)))), 3);
			}
			int startDepth = (int)(6 * amplitude / radius);
			for(int i = 0; i <= startDepth; i++) {
				if(worldObj.getBlockState(new BlockPos(pX, depth-i, pZ)).getMaterial() == Material.ROCK) //mlbv: was == Blocks.stone; loosened for compatibility.
					worldObj.setBlockState(new BlockPos(pX, depth-i, pZ), AddonBlocks.baleonitite.getDefaultState().withProperty(BlockMeta.META,Math.min(5,startDepth-i))); break;
			}
		}
	}
}
