package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.explosion.ExplosionSolinium;
import com.hbm.world.WorldUtil;
import com.hbm.world.biome.BiomeGenCraterBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ExplosionSolinium.class,remap = false)
public class MixinExplosionSolinium {
	@Shadow
	public int radius2;

	@Shadow
	public float explosionCoefficient2;

	@Shadow
	public float explosionCoefficient;

	@Shadow
	public int posX;

	@Shadow
	public int posY;

	@Shadow
	public int posZ;

	@Shadow
	public World worldObj;

	/**
	 * @author ntmleafia
	 * @reason add biome change
	 */
	@Overwrite
	private void breakColumn(int x,int z)
	{
		MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int dist = this.radius2 - (x * x + z * z);
		if (dist > 0)
		{
			dist = (int) Math.sqrt(dist);
			for (int y = (int)(dist / this.explosionCoefficient2); y > -dist / this.explosionCoefficient; y--)
			{
				pos.setPos(this.posX + x, this.posY+ y, this.posZ + z);
				ExplosionNukeGeneric.solinium(this.worldObj, pos);
			}
			pos.setPos(this.posX + x, this.posY, this.posZ + z);
			Biome curBiome = worldObj.getBiome(pos);
			if (curBiome instanceof BiomeGenCraterBase) {
				Biome biome = worldObj.getBiomeProvider().getBiome(pos);
				WorldUtil.setBiome(worldObj,this.posX+x,this.posZ+z,biome);
				Chunk dumb = worldObj.getChunk(pos);
				WorldUtil.syncBiomeChange(worldObj,dumb.x,dumb.z);
			}
		}
	}
}
