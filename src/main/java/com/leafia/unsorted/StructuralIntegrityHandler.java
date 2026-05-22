package com.leafia.unsorted;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.*;
import com.hbm.blocks.machine.rbmk.RBMKBase;
import com.leafia.contents.machines.elevators.floors.EvFloor;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaUtil;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

// Fun
public class StructuralIntegrityHandler {
	public static final StructuralIntegrityHandler SERVER = new StructuralIntegrityHandler();
	public static final StructuralIntegrityHandler LOCAL = new StructuralIntegrityHandler();
	public static boolean AUTOMATIC = false;

	private static final int MAX_DEPTH = 50;

	public static int calculations = 0;
	public static LongOpenHashSet blockedPoses = new LongOpenHashSet();

	public static Set<Integer> blacklistedDimensions = new HashSet<>();

    public static void collapse(World world,BlockPos pos) {
		if (world.getBlockState(pos).getBlock() instanceof BlockAir) return;
		//LeafiaDebug.debugPos(world,pos,3,0xFFAA00,"COLLAPSED");
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
		GM iron = new GM(40,5);
		GLUE_MASS_MAP.put(Material.ANVIL,iron);
		GLUE_MASS_MAP.put(Material.IRON,iron);
		//GLUE_MASS_MAP.put(Material.ROCK,new GM(25,5*3));
		GLUE_MASS_MAP.put(Material.WOOD,new GM(6,1));
		blacklistedDimensions.add(-1);
		blacklistedDimensions.add(1);
	}

	public static int getGlue(IBlockState state,@Nullable World world,@Nullable BlockPos pos) {
		if (state.getBlock() instanceof BlockDummyable) {
			if (state.getValue(BlockDummyable.META) >= 12 )
				return 0;
			if (state.getBlock() instanceof MachineElectrolyser)
				return 22;
			if (state.getBlock() instanceof RBMKBase)
				return 20;
			if (state.getBlock() instanceof MachineFrackingTower)
				return 62;
			if (state.getBlock() instanceof MachineICF)
				return 22;
			if (state.getBlock() instanceof MachineCoker)
				return 32;
			if (state.getBlock() instanceof MachineOilWell)
				return 22;
			if (state.getBlock() instanceof MachineExcavator)
				return 42;
			if (state.getBlock() instanceof BlockSiloHatch)
				return 22;
			if (state.getBlock() instanceof DummyBlockSiloHatch)
				return 22;
			if (state.getBlock() instanceof MachineMiningLaser)
				return 22;
			return 0;
		}
		if (state.getBlock() == ModBlocks.dfc_core)
			return 0;
		if (state.getBlock() instanceof EvFloor)
			return 1;
		GM gm = GLUE_MASS_MAP.get(state.getMaterial());
		int glue = gm == null ? 4*3 : gm.glue;
		if (state.getMaterial() == Material.ROCK)
			return 25;
		if (state.getBlock() == ModBlocks.concrete_rebar)
			glue *= 8;
		if (state.getBlock() == ModBlocks.steel_scaffold)
			glue *= 4;
		return glue;
	}
	public static int getMass(IBlockState state,@Nullable World world,@Nullable BlockPos pos) {
		if (state.getBlock() instanceof BlockDummyable) {
			if (state.getBlock() instanceof RBMKBase)
				return 1;
			return 10;
		}
		if (state.getBlock() == ModBlocks.dfc_core)
			return 0;
		if (state.getBlock() instanceof EvFloor)
			return 0;
		GM gm = GLUE_MASS_MAP.get(state.getMaterial());
		if (state.getMaterial() == Material.ROCK)
			return 1+Math.min((int)Math.pow(state.getBlock().getExplosionResistance(null),0.45)/2,15);
		int mass = gm == null ? 1 : gm.mass;
		if (!state.isFullCube()) mass /= 3;
		return Math.max(mass,1);
	}

	public static class SimulationData {
		public double maxRatio = 0;
		public double maxMass = 0;
		public double maxGlue = 0;
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
		SimulationData simulation = null;
		ObjectOpenHashSet<BlockPos> checkPoses = new ObjectOpenHashSet<>();

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
			this.simulation = parent.simulation;
			this.checkPoses = parent.checkPoses;
		}
	}

	private final CalStack[] FRAME_POOL = new CalStack[MAX_DEPTH + 2];
	private final LongOpenHashSet ROOT_IGNORE = new LongOpenHashSet();
	private final LongOpenHashSet ROOT_SUPPORTEDS = new LongOpenHashSet();

	private CalStack frame(int depth) {
		CalStack f = FRAME_POOL[depth];
		if (f == null) FRAME_POOL[depth] = f = new CalStack(0);
		return f;
	}

	static boolean needsSupport(IBlockState state) {
		return state.getMaterial().isSolid();
	}

	void calculate(CalStack stack,World world,BlockPos pos) {
		MutableBlockPos mutable = stack.scratch;
		int baseX = pos.getX(), baseY = pos.getY(), baseZ = pos.getZ();
		mutable.setPos(baseX,baseY,baseZ);
		int maxRelativeY = 0, minRelativeY = 0;
		// fill vertical column & pillar check
		LongOpenHashSet newIgnore = stack.newIgnore;
		LongOpenHashSet ignore = stack.ignore;
		LongOpenHashSet supporteds = stack.supporteds;

		stack.mass += getMass(world.getBlockState(pos),world,pos);
		newIgnore.add(mutable.toLong());

		int extraMass = 0;
		int extraMassDiv = 1;

		while (true) {
			mutable.setY(mutable.getY() + 1);
			if (!world.isValid(mutable)) break;
			long key = mutable.toLong();
			if (ignore.contains(key)) break;
			if (!needsSupport(world.getBlockState(mutable))) break;
			newIgnore.add(key);
			maxRelativeY++;
			extraMass += Math.max(getMass(world.getBlockState(mutable),world,mutable)-getGlue(world.getBlockState(mutable),world,mutable),0);
			extraMassDiv++;
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
			extraMass += Math.max(getMass(world.getBlockState(mutable),world,mutable)-getGlue(world.getBlockState(mutable),world,mutable),0);
			extraMassDiv++;
		}
		ignore.addAll(newIgnore);
		stack.mass += extraMass;

		if (stack.count > MAX_DEPTH) return;

		int cachedCx = Integer.MIN_VALUE, cachedCz = Integer.MIN_VALUE;
		boolean cachedLoaded = false;

		int mass = stack.mass;
		int glue = stack.glue;

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
				int glueAdd = getGlue(nState,world,mutable);
				long neighborLong = mutable.toLong();

				if (!ignore.contains(neighborLong)) {
					CalStack next = frame(stack.count + 1);
					stack.mass = mass;
					stack.glue = glue;
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
					stack.checkPoses.add(pos.up(minRelativeY));
					if (stack.glue >= stack.mass) break outer;
				}
			}
		}

		if (stack.parent == null) {
			if (stack.mass > stack.glue) {
				if (stack.simulation == null)
					collapse(world,pos.down(-minRelativeY));
				else
					stack.simulation.maxRatio = 1;
			}
		} else {
			stack.parent.mass = Math.max(stack.mass,stack.parent.mass);
			//if (supporteds.contains(pos.toLong()))
			//	stack.parent.glue = Math.max(stack.parent.glue,(int)(stack.parent.glue+(stack.glue-stack.parent.glue)*0.75));
			// ^^ ok so this is asshole and this is how:
			// the farthest block keeps accumulating glue, meaning it never collapses
			// after building, the block on the root tends to collapse because it doesnt calculate glue for the other
			// way around
			//LeafiaDebug.debugPos(world,pos,5,0xFFD000,"ADD MASS ("+stack.mass+"/"+stack.glue+")");
		}
		if (stack.simulation != null) {
			//if (stack.parent == null)
			//	stack.simulation.maxRatio = Math.min(stack.mass/(double)stack.glue,1);
			if (stack.parent == null && stack.mass > stack.glue)
				stack.simulation.maxRatio = 1;
			//double ratio = Math.min(stack.mass/(double)stack.glue,1);
			//stack.simulation.maxRatio = (stack.simulation.maxRatio+ratio)/2;
			stack.simulation.maxMass = Math.max(stack.simulation.maxMass,stack.mass);
			stack.simulation.maxGlue = Math.max(stack.simulation.maxGlue,stack.glue);
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
		SERVER.handleBlock(world,pos,false,false,null);
	}
	public static SimulationData handleBlockSimulate(World world,BlockPos pos,IBlockState forcedBlockState) {
		return LOCAL.handleBlock(world,pos,true,false,forcedBlockState);
	}
	public SimulationData handleBlock(World world,BlockPos pos,boolean simulate,boolean verifyMode,IBlockState forcedBlockState) {
		if (world.isRemote && !simulate) return null;
		if (blacklistedDimensions.contains(world.provider.getDimension())) return null;
		if (!verifyMode) {
			if (calculations > 200 && !simulate) return null;
		}
		if (!isChunkLoaded(world,pos)) {
			//LeafiaDebug.debugLog(world,"Skipped integrity calculation because the chunk is not loaded yet");
			return null;
		}
		IBlockState state = forcedBlockState != null ? forcedBlockState : world.getBlockState(pos);
		if (!needsSupport(state)) return null;
		if (blockedPoses.contains(pos.toLong())) return null;
		if (!simulate)
			blockedPoses.add(pos.toLong());
		if (!simulate && !verifyMode)
			calculations++;
		collapsed = false;
		ROOT_IGNORE.clear();
		ROOT_SUPPORTEDS.clear();
		CalStack stacc = new CalStack(0);
		stacc.ignore = ROOT_IGNORE;
		stacc.supporteds = ROOT_SUPPORTEDS;
		if (simulate) {
			SimulationData sim = new SimulationData();
			stacc.simulation = sim;
		}
		calculate(stacc,world,pos);
		/*if (!stacc.finished) {
			for (BlockPos p : stacc.collapseCandidates)
				collapse(world,p);
			//stack.finished = true;
			collapsed = true;
		}*/
		//if (!collapsed)
		//LeafiaDebug.debugPos(world,pos,3,0x88FF00,"CALCULATION "+stacc.mass+"/"+stacc.glue);
		if (stacc.simulation != null) {
			if (stacc.simulation.maxRatio <= 0) {
				if (stacc.simulation.maxGlue == 0)
					stacc.simulation.maxRatio = stacc.simulation.maxMass <= 0 ? 0 : 1;
				else
					stacc.simulation.maxRatio = Math.min(stacc.simulation.maxMass/stacc.simulation.maxGlue,0.99);
			}
		} else {
			//if (!verifyMode)
			//	for (BlockPos p : stacc.checkPoses)
			//		handleBlock(world,p,simulate,true,null);
		}
		return stacc.simulation;
	}
}
