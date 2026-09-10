package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.FalloutConfigJSON.FalloutEntry;
import com.hbm.config.FalloutConfigJSON.LookupResult;
import com.hbm.entity.effect.EntityFalloutRain;
import com.hbm.entity.logic.EntityExplosionChunkloading;
import com.hbm.lib.Library;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.worldgen.AddonBiomes;
import com.leafia.dev.LeafiaUtil;
import com.leafia.init.FalloutConfigInit;
import com.leafia.overwrite_contents.interfaces.IMixinEntityFalloutRain;
import com.leafia.savedata.FalloutSavedData;
import com.leafia.savedata.FalloutSavedData.FalloutData;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

	/*@Unique
	int leafia$rainTimer = 0;
	@Inject(method = "onUpdate",at = @At(value = "HEAD"),require = 1)
	void leafia$onOnUpdate(CallbackInfo ci) {
		if (finished == 1) {
			leafia$rainTimer++;
			if (leafia$rainTimer >= 24000)
				setDead();
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() { // why does ts no work
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Redirect(method = "startWorkersIfNeeded",at = @At(value = "INVOKE", target = "Lcom/hbm/entity/effect/EntityFalloutRain;setDead()V",remap = true),require = 1,remap = false)
	void leafia$onStartWorkersIfNeeded(EntityFalloutRain instance) {
		// do nothing lmao
	}

	@Redirect(method = "secondPassAndFinish",at = @At(value = "INVOKE", target = "Lcom/hbm/entity/effect/EntityFalloutRain;setDead()V",remap = true),require = 1,remap = false)
	void leafia$onSecondPassAndFinish(EntityFalloutRain instance) {
		// do nothing lmao
	}*/ // forget it

	@Inject(method = "setDead",at = @At(value = "HEAD"),require = 1)
	void leafia$onSetDead(CallbackInfo ci) {
		if (finished == 1 && !world.isRemote && !digammaFallout && !endothermicFallout) {
			double radius = getScale();
			FalloutSavedData saved = FalloutSavedData.forWorld(world);
			saved.syncTimer = 0;
			saved.falloutMap.add(new FalloutData(new Vec3d(posX,0,posZ),radius));
			saved.markDirty();
		}
	}

	@Shadow(remap = false)
	protected static Biome getBiomeChange(double distPercent,int scale,Biome original) {
		return null;
	}

	@Shadow(remap = false)
	private volatile int finished;

	@Shadow(remap = false)
	public abstract int getScale();

	public MixinEntityFalloutRain(World world) {
		super(world);
	}

	@Unique
	private boolean digammaFallout;
	@Unique
	private boolean endothermicFallout;
	@Unique
	private int leafia$floodHeight;

	@Override
	public void leafia$setDigammaFallout() {
		digammaFallout = true;
	}
	@Override
	public void leafia$setEndothermic() {
		endothermicFallout = true;
	}
	@Override
	public void leafia$setFloodHeight(int height) {
		leafia$floodHeight = height;
	}

	@Inject(method = "readEntityFromNBT",at = @At(value = "HEAD"),require = 1)
	public void leafia$onReadEntityFromNBT(NBTTagCompound nbt,CallbackInfo ci) {
		digammaFallout = nbt.getBoolean("digammaFallout");
		endothermicFallout = nbt.getBoolean("endothermic");
		leafia$floodHeight = nbt.getInteger("floodHeight");
	}

	@Inject(method = "writeEntityToNBT",at = @At(value = "HEAD"),require = 1)
	public void leafia$onWriteEntityToNBT(NBTTagCompound nbt,CallbackInfo ci) {
		nbt.setBoolean("digammaFallout",digammaFallout);
		nbt.setBoolean("endothermic",endothermicFallout);
		nbt.setInteger("floodHeight",leafia$floodHeight);
	}

	@Inject(method = "stompColumnToUpdates",at = @At(value = "HEAD"),cancellable = true,require = 1,remap = false)
	private void leafia$onStompColumnToUpdates(CallbackInfo ci,
	                                           @Local(argsOnly = true) ExtendedBlockStorage[] ebs,
	                                           @Local(argsOnly = true,ordinal = 0) int x,
	                                           @Local(argsOnly = true,ordinal = 1) int z,
	                                           @Local(argsOnly = true) double distPercent,
	                                           @Local(argsOnly = true,ordinal = 0) Long2ObjectOpenHashMap<IBlockState> updates,
	                                           @Local(argsOnly = true,ordinal = 1) Long2ObjectOpenHashMap<IBlockState> spawnFalling,
	                                           @Local(argsOnly = true) ThreadLocalRandom rand) {
		if (digammaFallout) {
			ci.cancel();
			leafia$stompColumnToUpdatesDigamma(ebs,x,z,distPercent,updates,spawnFalling,rand);
		} else if (endothermicFallout) {
			ci.cancel();
			leafia$stompColumnToUpdatesEndothermic(ebs,x,z,distPercent,updates,spawnFalling,rand);
		}
	}

	@Unique
	private void leafia$stompColumnToUpdatesEndothermic(ExtendedBlockStorage[] ebs,int x,int z,double distPercent,
	                                                Long2ObjectOpenHashMap<IBlockState> updates,
	                                                Long2ObjectOpenHashMap<IBlockState> spawnFalling,
	                                                ThreadLocalRandom rand) {

		int solidDepth = 0;
		List<FalloutEntry> entries = FalloutConfigInit.endoEntries;
		boolean useOreDict = leafia$hasOreDictMatchers(entries);
		int lx = x & 15;
		int lz = z & 15;
		MutableBlockPos pos = TL_POS.get();
		float stonebrickRes = Blocks.STONEBRICK.getExplosionResistance(null);

		for (int y = 255; y >= 0; y--) {
			if (solidDepth >= MAX_SOLID_DEPTH) return;

			int subY = y >>> 4;
			ExtendedBlockStorage storage = ebs[subY];
			IBlockState state = storage == Chunk.NULL_BLOCK_STORAGE || storage.isEmpty() ? Blocks.AIR.getDefaultState() : storage.get(lx, y & 15, lz);
			Block block = state.getBlock();
			if (block.isAir(state, world, pos.setPos(x, y, z)) || block == ModBlocks.fallout) continue;

			IBlockState stateUp = null;
			int upY = y + 1;
			if (solidDepth == 0 && upY < 256) {
				int upSub = upY >>> 4;
				ExtendedBlockStorage su = ebs[upSub];
				stateUp = su == Chunk.NULL_BLOCK_STORAGE || su.isEmpty() ? Blocks.AIR.getDefaultState() : su.get(lx, upY & 15, lz);
			}

			for (int i = upY; i < leafia$floodHeight; i++) {
				Material mat = world.getBlockState(pos.setPos(x,i,z)).getMaterial();
				if (!mat.isSolid()) {
					updates.put(Library.blockPosToLong(x, i, z), AddonBlocks.fluid_lox.getDefaultState());
				}
			}

			boolean transformed = false;
			LookupResult lookup = new LookupResult(state);
			int[] oreIds = null;
			for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
				FalloutEntry entry = entries.get(i);
				boolean entryUsesOreDict = useOreDict && entry.usesOreDict();
				if (entryUsesOreDict && oreIds == null) {
					oreIds = leafia$lookupOreIds(state);
				}
				IBlockState result = entry.eval(y,state,lookup,entryUsesOreDict ? oreIds : null,distPercent,rand);
				if (result != null) {
					updates.put(Library.blockPosToLong(x, y, z), result);
					if (entry.isSolid()) solidDepth++;
					transformed = true;
					break;
				}
			}

			if (!transformed && distPercent < 65 && y > 0) {
				int yBelow = y - 1;
				ExtendedBlockStorage sb = ebs[yBelow >>> 4];
				IBlockState below = sb == Chunk.NULL_BLOCK_STORAGE || sb.isEmpty() ? Blocks.AIR.getDefaultState() : sb.get(lx, yBelow & 15, lz);
				if (below.getBlock().isAir(below, world, pos.setPos(x, yBelow, z))) {
					float hardnessHere = state.getBlockHardness(world, pos.setPos(x, y, z));
					if (hardnessHere >= 0.0F && hardnessHere <= stonebrickRes) {
						for (int i = 0; i <= solidDepth; i++) {
							int yy = y + i;
							if (yy >= 256) break;
							int sub = yy >>> 4;
							ExtendedBlockStorage ss = ebs[sub];
							IBlockState sAt = ss == Chunk.NULL_BLOCK_STORAGE || ss.isEmpty() ? Blocks.AIR.getDefaultState() : ss.get(lx, yy & 15, lz);
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

	@Unique
	private void leafia$stompColumnToUpdatesDigamma(ExtendedBlockStorage[] ebs,int x,int z,double distPercent,
	                                                Long2ObjectOpenHashMap<IBlockState> updates,
	                                                Long2ObjectOpenHashMap<IBlockState> spawnFalling,
	                                                ThreadLocalRandom rand) {

		distPercent*=5;
		if (distPercent > 100) return;

		int solidDepth = 0;
		List<FalloutEntry> entries = FalloutConfigInit.digammaEntries;
		boolean useOreDict = leafia$hasOreDictMatchers(entries);
		int lx = x & 15;
		int lz = z & 15;
		MutableBlockPos pos = TL_POS.get();
		float stonebrickRes = Blocks.STONEBRICK.getExplosionResistance(null);

		for (int y = 255; y >= 0; y--) {
			if (solidDepth >= MAX_SOLID_DEPTH) return;

			int subY = y >>> 4;
			ExtendedBlockStorage storage = ebs[subY];
			IBlockState state = storage == Chunk.NULL_BLOCK_STORAGE || storage.isEmpty() ? Blocks.AIR.getDefaultState() : storage.get(lx, y & 15, lz);
			Block block = state.getBlock();
			if (block.isAir(state, world, pos.setPos(x, y, z)) || block == ModBlocks.fallout) continue;

			IBlockState stateUp = null;
			int upY = y + 1;
			if (solidDepth == 0 && upY < 256) {
				int upSub = upY >>> 4;
				ExtendedBlockStorage su = ebs[upSub];
				stateUp = su == Chunk.NULL_BLOCK_STORAGE || su.isEmpty() ? Blocks.AIR.getDefaultState() : su.get(lx, upY & 15, lz);
			}

			if (distPercent < 65 && (rand.nextInt(3) == 0 || block.isFlammable(world, pos.setPos(x, y, z), EnumFacing.UP))) {
				if (upY < 256) {
					int upSub = upY >>> 4;
					if (stateUp == null) {
						ExtendedBlockStorage su = ebs[upSub];
						stateUp = su == Chunk.NULL_BLOCK_STORAGE || su.isEmpty() ? Blocks.AIR.getDefaultState() : su.get(lx, upY & 15, lz);
					}
					if (stateUp.getBlock().isAir(stateUp, world, pos.setPos(x, upY, z)) && rand.nextInt(5) == 0) {
						updates.put(Library.blockPosToLong(x, upY, z), ModBlocks.fire_digamma.getDefaultState());
					}
				}
			}

			boolean transformed = false;
			LookupResult lookup = new LookupResult(state);
			int[] oreIds = null;
			for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
				FalloutEntry entry = entries.get(i);
				boolean entryUsesOreDict = useOreDict && entry.usesOreDict();
				if (entryUsesOreDict && oreIds == null) {
					oreIds = leafia$lookupOreIds(state);
				}
				IBlockState result = entry.eval(y,state,lookup,entryUsesOreDict ? oreIds : null,distPercent,rand);
				if (result != null) {
					updates.put(Library.blockPosToLong(x, y, z), result);
					if (entry.isSolid()) solidDepth++;
					transformed = true;
					break;
				}
			}

			if (!transformed && LeafiaUtil.isSolidVisibleCube(state) && rand.nextInt(80) + 20 >= Math.pow(distPercent / 100.0, 4.0) * 100.0) {
				if (y > 0) {
					updates.put(Library.blockPosToLong(x, y, z), ModBlocks.ash_digamma.getDefaultState());
					solidDepth++;
				}
				transformed = true;
			}

			if (!transformed && distPercent < 65 && y > 0) {
				int yBelow = y - 1;
				ExtendedBlockStorage sb = ebs[yBelow >>> 4];
				IBlockState below = sb == Chunk.NULL_BLOCK_STORAGE || sb.isEmpty() ? Blocks.AIR.getDefaultState() : sb.get(lx, yBelow & 15, lz);
				if (below.getBlock().isAir(below, world, pos.setPos(x, yBelow, z))) {
					float hardnessHere = state.getBlockHardness(world, pos.setPos(x, y, z));
					if (hardnessHere >= 0.0F && hardnessHere <= stonebrickRes) {
						for (int i = 0; i <= solidDepth; i++) {
							int yy = y + i;
							if (yy >= 256) break;
							int sub = yy >>> 4;
							ExtendedBlockStorage ss = ebs[sub];
							IBlockState sAt = ss == Chunk.NULL_BLOCK_STORAGE || ss.isEmpty() ? Blocks.AIR.getDefaultState() : ss.get(lx, yy & 15, lz);
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

	@Unique
	private static boolean leafia$hasOreDictMatchers(List<FalloutEntry> entries) {
		for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
			if (entries.get(i).usesOreDict()) return true;
		}
		return false;
	}

	@Unique
	private static int[] leafia$lookupOreIds(IBlockState state) {
		Block block = state.getBlock();
		Item item = Item.getItemFromBlock(block);
		if (item == Items.AIR) return LookupResult.NO_ORE_IDS;

		ItemStack stack = new ItemStack(item,1,block.damageDropped(state));
		int[] oreIds = OreDictionary.getOreIDs(stack);
		return oreIds.length == 0 ? LookupResult.NO_ORE_IDS : oreIds;
	}

	@Redirect(method = "processChunkOffThread",at = @At(value = "INVOKE", target = "Lcom/hbm/entity/effect/EntityFalloutRain;getBiomeChange(DILnet/minecraft/world/biome/Biome;)Lnet/minecraft/world/biome/Biome;"),require = 1,remap = false)
	private Biome leafia$onProcessChunkOffThread(double distPercent,int scale,Biome original) {
		if (digammaFallout)
			return AddonBiomes.digamma;
		if (endothermicFallout)
			return null;
		return getBiomeChange(distPercent,scale,original);
	}
}
