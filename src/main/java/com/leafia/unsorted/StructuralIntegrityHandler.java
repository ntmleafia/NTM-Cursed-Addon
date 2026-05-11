package com.leafia.unsorted;

import com.custom_hbm.util.LCETuple.Pair;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.LeafiaUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

import java.util.*;

// Fun
public class StructuralIntegrityHandler {
	public static boolean AUTOMATIC = false;
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
	public static final Map<Material,Pair<Integer,Integer>> glueMassMap = new HashMap<>();
	static {
		glueMassMap.put(Material.ANVIL,new Pair<>(50,5));
		glueMassMap.put(Material.IRON,new Pair<>(50,5));
		glueMassMap.put(Material.ROCK,new Pair<>(30,5));
	}
	public static int getGlue(IBlockState state) {
		Material material = state.getMaterial();
		int glue = 10;
		if (glueMassMap.containsKey(material))
			glue = glueMassMap.get(material).getA();
		return Math.max(glue,0);
	}
	public static int getMass(IBlockState state) {
		Material material = state.getMaterial();
		int mass = 1;
		if (glueMassMap.containsKey(material))
			mass = glueMassMap.get(material).getB();
		if (!state.isFullCube())
			mass /= 3;
		return Math.max(mass,1);
	}
	public static class CalStack {
		public int mass;
		public int glue = 0;
		public int count = 0;
		public boolean terminate = false;
		public boolean bottomReached = false;
		public MutableBlockPos pos = new MutableBlockPos();
		Set<Long> ignore = new HashSet<>();
		Set<Long> supporteds = new HashSet<>();
		List<Pair<BlockPos,CalStack>> collapseCandidates = new ArrayList<>();
		BlockPos startPos = null;
		CalStack parent = null;
		Set<Long> newIgnore = new HashSet<>();
		boolean noVerify = false;
		public CalStack(int mass) {
			this.mass = mass;
		}
		public CalStack copy() {
			CalStack stack = new CalStack(mass);
			stack.terminate = terminate;
			stack.pos.setPos(pos);
			stack.bottomReached = bottomReached;
			stack.ignore = ignore; // point the same instance
			stack.supporteds = supporteds; // point the same instance
			stack.glue = glue;
			stack.count = count+1;
			stack.collapseCandidates = collapseCandidates;
			stack.startPos = startPos;
			stack.noVerify = noVerify;
			stack.parent = this;
			return stack;
		}
	}
	static boolean needsSupport(IBlockState state) {
		return state.getMaterial().isSolid();
	}
	static void calculate(CalStack stack,World world,BlockPos pos) {
		MutableBlockPos mutable = new MutableBlockPos(pos);
		int maxRelativeY = 0;
		int minRelativeY = 0;
		// fill vertical column & pillar check
		Set<Long> newIgnore = stack.newIgnore;
		{
			stack.mass += getMass(world.getBlockState(pos));
			newIgnore.add(pos.toLong());
			while (true) {
				mutable.setY(mutable.getY()+1);
				if (!world.isValid(mutable)) break;
				if (stack.ignore.contains(mutable.toLong())) break;
				if (!needsSupport(world.getBlockState(mutable))) break;
				newIgnore.add(mutable.toLong());
				maxRelativeY++;
				//stack.mass += getMass(world.getBlockState(mutable));
			}
			mutable.setY(pos.getY());
			while (true) {
				mutable.setY(mutable.getY()-1);
				if (!world.isValid(mutable)) {
					stack.bottomReached = true;
					stack.ignore.addAll(newIgnore);
					stack.glue = Integer.MAX_VALUE;
					return;
				}
				if (stack.ignore.contains(mutable.toLong())) break;
				if (!needsSupport(world.getBlockState(mutable))) break;
				newIgnore.add(mutable.toLong());
				minRelativeY--;
				//stack.mass += getMass(world.getBlockState(mutable));
			}
			mutable.setY(pos.getY());
			stack.ignore.addAll(newIgnore);
		}
		if (stack.count > 50) return;
		{
			int maxMass = stack.mass;
			for (int yo = minRelativeY; yo <= maxRelativeY; yo++) {
				for (EnumFacing face : EnumFacing.HORIZONTALS) {
					mutable.setPos(pos.getX()+face.getXOffset(),pos.getY()+yo,pos.getZ()+face.getZOffset());
					if (!isChunkLoaded(world,mutable)) {
						//LeafiaDebug.debugLog(world,"Cancelled integrity calculation because the chunk is not loaded yet");
						stack.terminate = true;
						return;
					}
					if (needsSupport(world.getBlockState(mutable))) {
						int glueAdd = getGlue(world.getBlockState(mutable));
						if (!stack.ignore.contains(mutable.toLong())) {
							CalStack next = stack.copy();
							next.glue = 0;
							calculate(next,world,mutable);
							if (next.terminate) {
								stack.terminate = true;
								return;
							}
							if (next.mass <= next.glue) {
								stack.supporteds.addAll(next.newIgnore);
								maxMass = Math.max(stack.mass,next.mass);
							}
						}
						if (stack.supporteds.contains(mutable.toLong())) {
							stack.glue += glueAdd;
							stack.supporteds.addAll(newIgnore);
						}
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
	}
	static boolean isChunkLoaded(World world,BlockPos pos) {
		return world.isChunkGeneratedAt(pos.getX()>>4,pos.getZ()>>4);
	}
	static boolean collapsed = false;
	public static void handleBlock(World world,BlockPos pos) {
		if (world.isRemote) return;
		if (!isChunkLoaded(world,pos)) {
			//LeafiaDebug.debugLog(world,"Skipped integrity calculation because the chunk is not loaded yet");
			return;
		}
		IBlockState state = world.getBlockState(pos);
		if (!needsSupport(state)) return;
		collapsed = false;
		CalStack stacc = new CalStack(0);
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
