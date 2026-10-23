// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.worldgen;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.main.MainRegistry;
import com.leafia.contents.worldgen.NTMStructBuffer.StructData;
import com.leafia.contents.worldgen.NTMStructBuffer.StructLoader;
import com.leafia.contents.worldgen.lib.SellacityRoadChunk;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddonWorldGen implements IWorldGenerator {
	Random rand = new Random();
	// stolen from NTMWorldGenerator
	private void setRandomSeed(World world, int chunkX, int chunkZ) {
		rand.setSeed(world.getSeed() + world.provider.getDimension());
		final long i = rand.nextLong() / 2L * 2L + 1L;
		final long j = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed((long) chunkX * i + (long) chunkZ * j ^ world.getSeed());
	}
	int pmod(int x,int y) {
		return (y+(x%y))%y;
	}
	public static int roadWidth = 7;
	Map<Pair<Integer,Integer>,SellacityRoadChunk> chunks = new HashMap<>();

	public SellacityRoadChunk getNoiseMap(World world,int nx,int ny) {
		int mx = Math.floorDiv(nx,SellacityRoadChunk.size);
		int my = Math.floorDiv(ny,SellacityRoadChunk.size);
		Pair<Integer,Integer> chunkPos = new Pair<>(mx,my);
		setRandomSeed(world,Math.floorDiv(nx,SellacityRoadChunk.size),Math.floorDiv(ny,SellacityRoadChunk.size));
		if (!chunks.containsKey(chunkPos))
			chunks.put(chunkPos,new SellacityRoadChunk(rand));
		return chunks.get(chunkPos);
	}
	public int getNoiseValue(World world,int nx,int ny) {
		int nox = Math.floorMod(nx,SellacityRoadChunk.size);
		int noy = Math.floorMod(ny,SellacityRoadChunk.size);
		SellacityRoadChunk chunk = getNoiseMap(world,nx,ny);
		return chunk.data[chunk.index(nox,noy)];
	}

	public void genByRoadCoords(int rx,int rz,int cx,int cz,World world) {
		//world.setBlockState(world.getHeight(new BlockPos(rx,0,rz)),ModBlocks.fissure_bomb.getDefaultState());

		// check if theres road here
		if (getNoiseValue(world,rx/7,rz/7) == 1) {
			boolean n = getNoiseValue(world,rx/7,rz/7-1) == 1;
			boolean s = getNoiseValue(world,rx/7,rz/7+1) == 1;
			boolean w = getNoiseValue(world,rx/7-1,rz/7) == 1;
			boolean e = getNoiseValue(world,rx/7+1,rz/7) == 1;
			StructData road = null;
			EnumFacing face = EnumFacing.NORTH;
			// retarded coding 10000
			if (n && s && w && e)
				road = StructLoader.structs.get("roads/road_cross");
			else {
				if (s && w && e) {
					road = StructLoader.structs.get("roads/road_t");
				} else if (n && w && e) {
					road = StructLoader.structs.get("roads/road_t");
					face = EnumFacing.SOUTH;
				} else if (n && w && s) {
					road = StructLoader.structs.get("roads/road_t");
					face = EnumFacing.EAST;
				} else if (n && e && s) {
					road = StructLoader.structs.get("roads/road_t");
					face = EnumFacing.WEST;
				} else {
					if (s && e) {
						road = StructLoader.structs.get("roads/road_curve");
					} else if (n && w) {
						road = StructLoader.structs.get("roads/road_curve");
						face = EnumFacing.SOUTH;
					} else if (w && s) {
						road = StructLoader.structs.get("roads/road_curve");
						face = EnumFacing.EAST;
					} else if (n && e) {
						road = StructLoader.structs.get("roads/road_curve");
						face = EnumFacing.WEST;
					} else {
						road = StructLoader.structs.get("roads/road_straight");
						if (w || e)
							face = EnumFacing.EAST;
					}
				}
			}
			NTMStructBuffer.fromMetadata(road).rotateToFace(face).build(
					world,
					world.getHeight(new BlockPos(rx+roadWidth/2,65,rz+roadWidth/2))
			);
		}
	}
	MutableBlockPos mb = new MutableBlockPos();
	@Override
	public void generate(Random random,int chunkX,int chunkZ,World world,IChunkGenerator chunkGenerator,IChunkProvider chunkProvider) {
		try {
			int minX = chunkX*16;
			int minZ = chunkZ*16;
			if (world.getBiomeProvider().getBiome(mb.setPos(minX+8,56,minZ+8)) == AddonBiomes.ruins) {
			//world.setBlockState(world.getHeight(new BlockPos(minX+5,15,minZ+5)),ModBlocks.fissure_bomb.getDefaultState(), 2 | 16);
			//if (minX > -200 && minX < 200 && minZ > -200 && minZ < 200) {
				int lx = (16+pmod(minX,roadWidth)-roadWidth)/roadWidth;
				int lz = (16+pmod(minZ,roadWidth)-roadWidth)/roadWidth;
				int sx = minX-pmod(minX,roadWidth);
				int sz = minZ-pmod(minZ,roadWidth);
				for (int xo = 0; xo <= lx; xo++) {
					for (int zo = 0; zo <= lz; zo++)
						genByRoadCoords(sx+xo*roadWidth,sz+zo*roadWidth,chunkX,chunkZ,world);
				}
			}
		} catch (Throwable t) {
			MainRegistry.logger.error("LCA Worldgen Error", t);
		}
	}
}
