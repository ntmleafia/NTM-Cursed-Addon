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
		public boolean finished = false;
		public boolean bottomReached = false;
		public MutableBlockPos pos = new MutableBlockPos();
		Set<Long> ignore = new HashSet<>();
		Set<Long> pillars = new HashSet<>();
		Map<Long,Integer> glueMap = new HashMap<>();
		List<BlockPos> collapseCandidates = new ArrayList<>();
		public CalStack(int mass) {
			this.mass = mass;
		}
		public CalStack copy() {
			CalStack stack = new CalStack(mass);
			stack.finished = finished;
			stack.pos.setPos(pos);
			stack.bottomReached = bottomReached;
			stack.ignore = ignore; // point the same instance
			stack.glue = glue;
			stack.count = count+1;
			stack.glueMap = glueMap;
			stack.collapseCandidates = collapseCandidates;
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
		Set<Long> newIgnore = new HashSet<>();
		// fill vertical column & pillar check
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
				stack.mass += getMass(world.getBlockState(mutable));
			}
			mutable.setY(pos.getY());
			while (true) {
				mutable.setY(mutable.getY()-1);
				if (!world.isValid(mutable)) {
					stack.bottomReached = true;
					stack.ignore.addAll(newIgnore);
					stack.pillars.addAll(newIgnore);
					return;
				}
				if (stack.ignore.contains(mutable.toLong())) break;
				if (!needsSupport(world.getBlockState(mutable))) break;
				newIgnore.add(mutable.toLong());
				minRelativeY--;
				stack.mass += getMass(world.getBlockState(mutable));
			}
			mutable.setY(pos.getY());
			stack.ignore.addAll(newIgnore);
		}
		if (stack.count > 50) return;
		{
			boolean isSupported = false;
			for (int yo = minRelativeY; yo <= maxRelativeY; yo++) {
				for (EnumFacing face : EnumFacing.HORIZONTALS) {
					mutable.setPos(pos.getX()+face.getXOffset(),pos.getY()+yo,pos.getZ()+face.getZOffset());
					if (needsSupport(world.getBlockState(mutable))) {
						int glueAdd = getGlue(world.getBlockState(mutable));
						if (!stack.ignore.contains(mutable.toLong())) {
							CalStack next = stack.copy();
							calculate(next,world,mutable);
							isSupported = true;
							/*if (next.finished) {
								stack.finished = true;
								return;
							}*/
							if (next.bottomReached) {
								stack.glue += glueAdd;
								if (stack.mass <= stack.glue) {
									stack.finished = true;
									//return;
									break;
								}
							}
						} else if (stack.pillars.contains(mutable.toLong())) {
							stack.glue += glueAdd;
							isSupported = true;
							if (stack.mass <= stack.glue) {
								stack.finished = true;
								//return;
								break;
							}
						} else if (stack.glueMap.containsKey(mutable.toLong())) {
							stack.glue = Math.max(stack.glue,stack.glueMap.get(mutable.toLong()));
							isSupported = true;
							if (stack.mass <= stack.glue) {
								stack.finished = true;
								//return;
								break;
							}
						}
					}
				}
				for (Long l : newIgnore) {
					stack.glueMap.put(l,Math.max(stack.glueMap.getOrDefault(l,0),stack.glue));
				}
			}
			if (!isSupported) {
				stack.collapseCandidates.add(pos.down(-minRelativeY));
				return;
			}
		}
	}
	static boolean collapsed = false;
	public static void handleBlock(World world,BlockPos pos) {
		if (world.isRemote) return;
		IBlockState state = world.getBlockState(pos);
		if (!needsSupport(state)) return;
		collapsed = false;
		CalStack stacc = new CalStack(0);
		calculate(stacc,world,pos);
		if (!stacc.finished) {
			for (BlockPos p : stacc.collapseCandidates)
				collapse(world,p);
			//stack.finished = true;
			collapsed = true;
		}
		if (!collapsed)
			LeafiaDebug.debugPos(world,pos,3,0x88FF00,"SUPPORTED");
	}
}
