// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.worldgen.biomes;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.mob.EntityGlowingOne;
import com.leafia.contents.AddonBlocks.LegacyBlocks;
import com.leafia.contents.worldgen.AddonBiome;
import com.leafia.contents.worldgen.AddonBiomes;
import com.leafia.contents.worldgen.biomes.effects.HasAcidicRain;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import javax.annotation.Nullable;
import java.util.Random;

public class Ruins extends AddonBiome implements HasAcidicRain {
	@Nullable
	@Override
	public GenLayer[] overrideGenLayers(long seed,GenLayer[] layers,int shaperIndex,int decoratorIndex,int shaperScale) {
		return AddonBiomes.barrens.overrideGenLayers(seed,layers,shaperIndex,decoratorIndex,shaperScale);
	}
	/**
	 * Given x, z coordinates, we count down all the y positions starting at 255 and working our way down. When we hit a
	 * non-air block, we replace it with this.topBlock (default grass, descendants may set otherwise), and then a
	 * relatively shallow layer of blocks of type this.fillerBlock (default dirt). A random set of blocks below y == 5
	 * (but always including y == 0) is replaced with bedrock.
	 *
	 * If we don't hit non-air until somewhat below sea level, we top with gravel and fill down with stone.
	 *
	 * If this.fillerBlock is red sand, we replace some of that with red sandstone.
	 */
	public final void generateBiomeTerrainRuins(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
	{
		int i = worldIn.getSeaLevel();
		IBlockState iblockstate = this.topBlock;
		IBlockState iblockstate1 = this.fillerBlock;
		int j = -1;
		int k = (int)(noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		int cz = x & 15;
		int cx = z & 15;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		int targetHeight = 62;

		// oh my god this sucks balls
		int smoothingRadius = 5;
		double ratio = 1;
		for (int x1 = x-5; x1 <= x+5; x1++) {
			for (int z1 = z-5; z1 <= z+5; z1++) {
				Biome b = worldIn.getBiomeProvider().getBiome(blockpos$mutableblockpos.setPos(x1,targetHeight,z1));
				if (b != AddonBiomes.ruins)
					ratio = Math.max(0,ratio-1d/(smoothingRadius*smoothingRadius));
				if (ratio <= 0) break;
			}
			if (ratio <= 0) break;
		}

		int height = 75;
		for (int y = 75; y >= 56; y--) {
			if (chunkPrimerIn.getBlockState(cx,y,cz).getMaterial() != Material.AIR) {
				height = y;
				break;
			}
		}

		height = targetHeight+(int)((height-targetHeight)*(1-ratio*0.75));

		for (int y = 255; y >= 0; --y)
		{
			if (y <= rand.nextInt(5))
			{
				chunkPrimerIn.setBlockState(cx, y, cz, BEDROCK);
			}
			else
			{
				if (y >= 56) {
					if (y > height)
						chunkPrimerIn.setBlockState(cx,y,cz,Blocks.AIR.getDefaultState());
					else
						chunkPrimerIn.setBlockState(cx,y,cz,Blocks.STONE.getDefaultState());
				}
				IBlockState iblockstate2 = chunkPrimerIn.getBlockState(cx, y, cz);

				if (iblockstate2.getMaterial() == Material.AIR)
				{
					j = -1;
				}
				else if (iblockstate2.getBlock() == Blocks.STONE)
				{
					if (j == -1)
					{
						if (k <= 0)
						{
							iblockstate = AIR;
							iblockstate1 = STONE;
						}
						else if (y >= i - 4 && y <= i + 1)
						{
							iblockstate = this.topBlock;
							iblockstate1 = this.fillerBlock;
						}

						if (y < i && (iblockstate == null || iblockstate.getMaterial() == Material.AIR))
						{
							if (this.getTemperature(blockpos$mutableblockpos.setPos(x, y, z)) < 0.15F)
							{
								iblockstate = ICE;
							}
							else
							{
								iblockstate = WATER;
							}
						}

						j = k;

						if (y >= i - 1)
						{
							chunkPrimerIn.setBlockState(cx, y, cz, iblockstate);
						}
						else if (y < i - 7 - k)
						{
							iblockstate = AIR;
							iblockstate1 = STONE;
							chunkPrimerIn.setBlockState(cx, y, cz, GRAVEL);
						}
						else
						{
							chunkPrimerIn.setBlockState(cx, y, cz, iblockstate1);
						}
					}
					else if (j > 0)
					{
						--j;
						chunkPrimerIn.setBlockState(cx, y, cz, iblockstate1);

						if (j == 0 && iblockstate1.getBlock() == Blocks.SAND && k > 1)
						{
							j = rand.nextInt(4) + Math.max(0, y - 63);
							iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? RED_SANDSTONE : SANDSTONE;
						}
					}
				}
			}
		}
	}
	@Override
	public void genTerrainBlocks(World worldIn,Random rand,ChunkPrimer chunkPrimerIn,int x,int z,double noiseVal) {
		this.generateBiomeTerrainRuins(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
	}
	public Ruins(String resource) {
		super(resource,
				new BiomeProperties("Ruins")
						.setBaseHeight(0.1f)
						.setHeightVariation(0f)
						.setRainfall(0.85f)
						.setTemperature(1.52f)
						.setWaterColor(0x2f2c17)//0x737163)
		);
		this.spawnableCreatureList.clear();
		this.spawnableMonsterList.clear();

		this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 65, 3, 6));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityGlowingOne.class, 35, 3, 6));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityZombieVillager.class, 88, 8, 14));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 100, 1, 2));
		//this.spawnableMonsterList.add(new SpawnListEntry(EntityNuclearCreeper.class, 40, 1, 1)); hmmmm no not yet that makes him no longer so special
		this.spawnableCaveCreatureList.clear();
		this.spawnableWaterCreatureList.clear();

		this.decorator.treesPerChunk = -999;

		this.topBlock = ModBlocks.waste_earth.getStateFromMeta(5);
		this.fillerBlock = LegacyBlocks.waste_dirt.getStateFromMeta(5);

		this.postInit = ()->{
			//BiomeManager.addBiome(BiomeType.DESERT,new BiomeEntry(this,15));
			BiomeDictionary.addTypes(this,Type.DEAD,Type.DRY,Type.WASTELAND);
		};
	}
	@Override
	public int getSkyColorByTemp(float currentTemperature) {
		return 0x242318;
	}
	@Override
	public int getFogColor() {
		return getSkyColorByTemp(0);
	}
	@Override
	public float getFogDensity(float original) { return Math.max(original,0.75f); }
	/*@Override
	public float getFogStart(float original) { return original*-0.01f; }
	@Override
	public float getFogEnd(float original) { return original*0.35f; }*/
	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		return 0x0a0909;
	}
	@Override
	public int getFoliageColorAtPos(BlockPos pos) {
		return 0x0a0909;
	}
}
