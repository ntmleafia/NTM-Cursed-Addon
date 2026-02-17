package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.FalloutConfigJSON;
import com.hbm.config.FalloutConfigJSON.FalloutEntry;
import com.hbm.entity.effect.EntityFalloutRain;
import com.hbm.entity.logic.EntityExplosionChunkloading;
import com.hbm.lib.Library;
import com.leafia.dev.LeafiaUtil;
import com.leafia.init.FalloutConfigInit;
import com.leafia.overwrite_contents.interfaces.IMixinEntityFalloutRain;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(value = EntityFalloutRain.class)
public abstract class MixinEntityFalloutRain extends EntityExplosionChunkloading implements IMixinEntityFalloutRain {
	@Shadow(remap = false)
	@Final
	private static int MAX_SOLID_DEPTH;
	@Shadow(remap = false)
	@Final
	private static ThreadLocal<MutableBlockPos> TL_POS;

	@Shadow(remap = false)
	protected static Biome getBiomeChange(double distPercent,int scale,Biome original) {
		return null;
	}

	public MixinEntityFalloutRain(World world) {
		super(world);
	}
	@Unique boolean digammaFallout = false;
	@Override
	public void setDigammaFallout() {
		digammaFallout = true;
	}
	@Inject(method = "readEntityFromNBT",at = @At(value = "HEAD"),require = 1)
	public void onReadEntityFromNBT(NBTTagCompound nbt,CallbackInfo ci) {
		digammaFallout = nbt.getBoolean("digammaFallout");
	}
	@Inject(method = "writeEntityToNBT",at = @At(value = "HEAD"),require = 1)
	public void onWriteEntityFromNBT(NBTTagCompound nbt,CallbackInfo ci) {
		nbt.setBoolean("digammaFallout",digammaFallout);
	}
	/**
	 * @author ntmleafia
	 * @reason fuck off im going lazy
	 */
	@Overwrite(remap = false)
	void stompColumnToUpdates(ExtendedBlockStorage[] ebs,int x,int z,double distPercent,Long2ObjectOpenHashMap<IBlockState> updates,
	                                  Long2ObjectOpenHashMap<IBlockState> spawnFalling,ThreadLocalRandom rand) {

		int solidDepth = 0;
		final int lx = x & 15;
		final int lz = z & 15;
		final MutableBlockPos pos = TL_POS.get();
		final float stonebrickRes = Blocks.STONEBRICK.getExplosionResistance(null);

		for (int y = 255; y >= 0; y--) {
			if (solidDepth >= MAX_SOLID_DEPTH) return;

			final int subY = y >>> 4;
			ExtendedBlockStorage s = ebs[subY];
			final IBlockState state = s == Chunk.NULL_BLOCK_STORAGE || s.isEmpty() ? Blocks.AIR.getDefaultState() : s.get(lx, y & 15, lz);
			final Block block = state.getBlock();
			if (block.isAir(state, world, pos.setPos(x, y, z)) || block == ModBlocks.fallout) continue;

			IBlockState stateUp = null;
			final int upY = y + 1;
			if (solidDepth == 0 && upY < 256) {
				final int upSub = upY >>> 4;
				ExtendedBlockStorage su = ebs[upSub];
				stateUp = su == Chunk.NULL_BLOCK_STORAGE || su.isEmpty() ? Blocks.AIR.getDefaultState() : su.get(lx, upY & 15, lz);
				pos.setPos(x, upY, z);
				boolean airOrReplaceable = stateUp.getBlock().isAir(stateUp, world, pos) || stateUp.getBlock().isReplaceable(world,
						pos) && !stateUp.getMaterial().isLiquid();
				if (airOrReplaceable) {
					double d = distPercent / 100.0;
					double chance = 0.1 - Math.pow(d - 0.7, 2.0);
					if (chance >= rand.nextDouble() && !digammaFallout) {
						updates.put(Library.blockPosToLong(x, upY, z), ModBlocks.fallout.getDefaultState());
					}
				}
			}

			if (distPercent < 65 && ((digammaFallout && world.rand.nextInt(3) == 0) || block.isFlammable(world, pos.setPos(x, y, z), EnumFacing.UP))) {
				if (upY < 256) {
					final int upSub = upY >>> 4;
					if (stateUp == null) {
						ExtendedBlockStorage su = ebs[upSub];
						stateUp = su == Chunk.NULL_BLOCK_STORAGE || su.isEmpty() ? Blocks.AIR.getDefaultState() : su.get(lx, upY & 15, lz);
					}
					if (stateUp.getBlock().isAir(stateUp, world, pos.setPos(x, upY, z))) {
						if ((rand.nextInt(5)) == 0) {
							updates.put(Library.blockPosToLong(x, upY, z), digammaFallout ? ModBlocks.fire_digamma.getDefaultState() : Blocks.FIRE.getDefaultState());
						}
					}
				}
			}

			boolean transformed = false;
			List<FalloutEntry> entries = FalloutConfigJSON.entries;
			if (digammaFallout)
				entries = FalloutConfigInit.digammaEntries;
			//noinspection ForLoopReplaceableByForEach
			for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
				FalloutEntry entry = entries.get(i);
				IBlockState result = entry.eval(y, state, distPercent, rand);
				if (result != null) {
					updates.put(Library.blockPosToLong(x, y, z), result);
					if (entry.isSolid()) solidDepth++;
					transformed = true;
					break;
				}
			}
			if (!transformed && digammaFallout && LeafiaUtil.isSolidVisibleCube(world.getBlockState(new BlockPos(x,y,z))) && world.rand.nextInt(80)+20 >= Math.pow(distPercent/100,4)*100) {
				IBlockState result = ModBlocks.ash_digamma.getDefaultState();
				updates.put(Library.blockPosToLong(x,y,z),result);
				solidDepth++;
				transformed = true;
			}

			if (!transformed && distPercent < 65 && y > 0) {
				final int yBelow = y - 1;
				ExtendedBlockStorage sb = ebs[yBelow >>> 4];
				final IBlockState below = (sb == Chunk.NULL_BLOCK_STORAGE || sb.isEmpty()) ? Blocks.AIR.getDefaultState() : sb.get(lx, yBelow & 15,
						lz);
				if (below.getBlock().isAir(below, world, pos.setPos(x, yBelow, z))) {
					float hardnessHere = state.getBlockHardness(world, pos.setPos(x, y, z));
					if (hardnessHere >= 0.0F && hardnessHere <= stonebrickRes) {
						for (int i = 0; i <= solidDepth; i++) {
							int yy = y + i;
							if (yy >= 256) break;
							final int sub = yy >>> 4;
							ExtendedBlockStorage ss = ebs[sub];
							final IBlockState sAt = ss == Chunk.NULL_BLOCK_STORAGE || ss.isEmpty() ? Blocks.AIR.getDefaultState() : ss.get(lx,
									yy & 15, lz);
							if (sAt.getBlock().isAir(sAt, world, pos.setPos(x, yy, z))) continue;
							float h = sAt.getBlockHardness(world, pos);
							if (h >= 0.0F && h <= stonebrickRes) {
								long key = Library.blockPosToLong(x, yy, z);
								spawnFalling.putIfAbsent(key, sAt);
							}
						}
					}
				}
			}

			if (!transformed && state.isNormalCube()) solidDepth++;
		}
	}
	@Redirect(method = "processChunkOffThread",at = @At(value = "INVOKE", target = "Lcom/hbm/entity/effect/EntityFalloutRain;getBiomeChange(DILnet/minecraft/world/biome/Biome;)Lnet/minecraft/world/biome/Biome;"),require = 1,remap = false)
	private Biome onProcessChunkOffThread(double distPercent,int scale,Biome original) {
		if (digammaFallout)
			return null;
		return getBiomeChange(distPercent,scale,original);
	}
}
