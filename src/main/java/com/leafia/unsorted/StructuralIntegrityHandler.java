package com.leafia.unsorted;

import com.hbm.blocks.BlockDummyable;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaUtil;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

// Fun
public class StructuralIntegrityHandler {
	public static boolean AUTOMATIC = false;

	private static final int MAX_DEPTH = 50;

	public static int calculations = 0;
	public static LongOpenHashSet blockedPoses = new LongOpenHashSet();

	public static Set<Integer> blacklistedDimensions = new HashSet<>();

    public static void collapse(World world,BlockPos pos) {
		if (world.getBlockState(pos).getBlock() instanceof BlockAir) return;
		LeafiaDebug.debugPos(world,pos,3,0xFFAA00,"COLLAPSED");
		IBlockState state = world.getBlockState(pos);
		if (LeafiaUtil.isSolidVisibleCube(state)) {
			EntityFallingBlock fallingBlock = new EntityFallingBlock(world,pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,state);
			fallingBlock.fallTime = 1;
			world.setBlockToAir(pos);
			world.spawnEntity(fallingBlock);
		} else
			world.destroyBlock(pos,true);
	}

	private static final class GM {
		final int glue, mass;
		GM(int g,int m) { glue = g; mass = m; }
	}
	private static final IdentityHashMap<Material,GM> GLUE_MASS_MAP = new IdentityHashMap<>();
	static {
		GM iron = new GM(50,5);
		GLUE_MASS_MAP.put(Material.ANVIL,iron);
		GLUE_MASS_MAP.put(Material.IRON,iron);
		GLUE_MASS_MAP.put(Material.ROCK,new GM(30,5));
		blacklistedDimensions.add(-1);
		blacklistedDimensions.add(1);
	}

	public static int getGlue(IBlockState state) {
		if (state.getBlock() instanceof BlockDummyable) return 0;
		GM gm = GLUE_MASS_MAP.get(state.getMaterial());
		return gm == null ? 10 : gm.glue;
	}
	public static int getMass(IBlockState state) {
		if (state.getBlock() instanceof BlockDummyable) return 10;
		GM gm = GLUE_MASS_MAP.get(state.getMaterial());
		int mass = gm == null ? 1 : gm.mass;
		if (!state.isFullCube()) mass /= 3;
		return Math.max(mass,1);
	}

	public static class CalStack {
		public int mass;
		public int glue;
		public int count;
		public boolean terminate;
		public boolean bottomReached;
		public final MutableBlockPos scratch = new MutableBlockPos();
		LongOpenHashSet ignore;
		LongOpenHashSet supporteds;
		final LongOpenHashSet newIgnore = new LongOpenHashSet();
		CalStack parent;

		public CalStack(int mass) { this.mass = mass; }

		void reset(int mass,int count,LongOpenHashSet ignore,LongOpenHashSet supporteds,CalStack parent) {
			this.mass = mass;
			this.glue = 0;
			this.count = count;
			this.terminate = false;
			this.bottomReached = false;
			this.ignore = ignore;
			this.supporteds = supporteds;
			this.parent = parent;
			this.newIgnore.clear();
		}
	}

	private static final CalStack[] FRAME_POOL = new CalStack[MAX_DEPTH + 2];
	private static final LongOpenHashSet ROOT_IGNORE = new LongOpenHashSet();
	private static final LongOpenHashSet ROOT_SUPPORTEDS = new LongOpenHashSet();

	private static CalStack frame(int depth) {
		CalStack f = FRAME_POOL[depth];
		if (f == null) FRAME_POOL[depth] = f = new CalStack(0);
		return f;
	}

	static boolean needsSupport(IBlockState state) {
		return state.getMaterial().isSolid();
	}

	static void calculate(CalStack stack,World world,BlockPos pos) {
		MutableBlockPos mutable = stack.scratch;
		int baseX = pos.getX(), baseY = pos.getY(), baseZ = pos.getZ();
		mutable.setPos(baseX,baseY,baseZ);
		int maxRelativeY = 0, minRelativeY = 0;
		// fill vertical column & pillar check
		LongOpenHashSet newIgnore = stack.newIgnore;
		LongOpenHashSet ignore = stack.ignore;
		LongOpenHashSet supporteds = stack.supporteds;

		stack.mass += getMass(world.getBlockState(pos));
		newIgnore.add(mutable.toLong());

		while (true) {
			mutable.setY(mutable.getY() + 1);
			if (!world.isValid(mutable)) break;
			long key = mutable.toLong();
			if (ignore.contains(key)) break;
			if (!needsSupport(world.getBlockState(mutable))) break;
			newIgnore.add(key);
			maxRelativeY++;
			//stack.mass += getMass(world.getBlockState(mutable));
		}
		mutable.setY(baseY);
		while (true) {
			mutable.setY(mutable.getY() - 1);
			if (!world.isValid(mutable)) {
				stack.bottomReached = true;
				ignore.addAll(newIgnore);
				stack.glue = Integer.MAX_VALUE;
				return;
			}
			long key = mutable.toLong();
			if (ignore.contains(key)) break;
			if (!needsSupport(world.getBlockState(mutable))) break;
			newIgnore.add(key);
			minRelativeY--;
			//stack.mass += getMass(world.getBlockState(mutable));
		}
		ignore.addAll(newIgnore);

		if (stack.count > MAX_DEPTH) return;

		int cachedCx = Integer.MIN_VALUE, cachedCz = Integer.MIN_VALUE;
		boolean cachedLoaded = false;

		outer:
		for (int yo = minRelativeY; yo <= maxRelativeY; yo++) {
            for (EnumFacing face : EnumFacing.HORIZONTALS) {
                int nx = baseX + face.getXOffset();
                int ny = baseY + yo;
                int nz = baseZ + face.getZOffset();
                mutable.setPos(nx, ny, nz);

                int cx = nx >> 4, cz = nz >> 4;
                if (cx != cachedCx || cz != cachedCz) {
                    cachedCx = cx;
                    cachedCz = cz;
                    cachedLoaded = world.isChunkGeneratedAt(cx, cz);
                }
                if (!cachedLoaded) {
                    //LeafiaDebug.debugLog(world,"Cancelled integrity calculation because the chunk is not loaded yet");
                    stack.terminate = true;
                    return;
                }

                IBlockState nState = world.getBlockState(mutable);
                if (!needsSupport(nState)) continue;
                int glueAdd = getGlue(nState);
                long neighborLong = mutable.toLong();

                if (!ignore.contains(neighborLong)) {
                    CalStack next = frame(stack.count + 1);
                    next.reset(stack.mass, stack.count + 1, ignore, supporteds, stack);
                    calculate(next, world, mutable);
                    if (next.terminate) {
                        stack.terminate = true;
                        return;
                    }
                    if (next.mass <= next.glue)
                        supporteds.addAll(next.newIgnore);
                }
                if (supporteds.contains(neighborLong)) {
                    stack.glue += glueAdd;
                    supporteds.addAll(newIgnore);
                    if (stack.glue >= stack.mass) break outer;
                }
            }
		}

		if (stack.mass > stack.glue) {
			if (stack.parent == null)
				collapse(world,pos.down(-minRelativeY));
			else
				stack.parent.mass = stack.mass;
		}
		/*if (!isSupported) {
			stack.collapseCandidates.add(pos.down(-minRelativeY));
			return;
		}*/
	}

	static boolean isChunkLoaded(World world,BlockPos pos) {
		return world.isChunkGeneratedAt(pos.getX()>>4,pos.getZ()>>4);
	}
	static boolean collapsed = false;
	public static void handleBlock(World world,BlockPos pos) {
		if (world.isRemote) return;
		if (blacklistedDimensions.contains(world.provider.getDimension())) return;
		if (calculations > 200) return;
		if (!isChunkLoaded(world,pos)) {
			//LeafiaDebug.debugLog(world,"Skipped integrity calculation because the chunk is not loaded yet");
			return;
		}
		IBlockState state = world.getBlockState(pos);
		if (!needsSupport(state)) return;
		if (blockedPoses.contains(pos.toLong())) return;
		blockedPoses.add(pos.toLong());
		calculations++;
		collapsed = false;
		ROOT_IGNORE.clear();
		ROOT_SUPPORTEDS.clear();
		CalStack stacc = new CalStack(0);
		stacc.ignore = ROOT_IGNORE;
		stacc.supporteds = ROOT_SUPPORTEDS;
		calculate(stacc,world,pos);
		/*if (!stacc.finished) {
			for (BlockPos p : stacc.collapseCandidates)
				collapse(world,p);
			//stack.finished = true;
			collapsed = true;
		}*/
		if (!collapsed)
			LeafiaDebug.debugPos(world,pos,3,0x88FF00,"SUPPORTED "+stacc.mass+"/"+stacc.glue);
	}
}
