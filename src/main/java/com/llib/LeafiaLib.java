package com.llib;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Function;

public class LeafiaLib {

    public static class LeafiaRayTraceConfig {
        public final World world;
        public final Vec3d start;
        public final Vec3d end;
        public final Vec3d unitDir;
        public final EnumFacing pivotAxisFace;
        public final Vec3d secondaryVector;
        LeafiaRayTraceConfig(World world,Vec3d start,Vec3d end,Vec3d unitDir,EnumFacing pivotAxisFace,Vec3d secondaryVector) {
            this.world = world;
            this.start = start;
            this.end = end;
            this.unitDir = unitDir;
            this.pivotAxisFace = pivotAxisFace;
            this.secondaryVector = secondaryVector;
        }
    }
    public static class LeafiaRayTraceProgress {
        public final int distanceAlongPivot;
        public final Vec3d posIntended;
        public final BlockPos posSnapped;
        public final Block block;
        public final IBlockState state;
        public final Material material;
        public boolean stop = false;
        LeafiaRayTraceProgress(int distanceAlongPivot,Vec3d posIntended,BlockPos posSnapped,Block block,IBlockState state,Material material) {
            this.distanceAlongPivot = distanceAlongPivot;
            this.posIntended = posIntended;
            this.posSnapped = posSnapped;
            this.block = block;
            this.state = state;
            this.material = material;
        }
    }
    public static class LeafiaRayTraceControl<T> {
        final T value;
        final boolean stop;
        final boolean overwrite;
        private LeafiaRayTraceControl(T value,boolean stop,boolean overwrite) {
            this.value = value;
            this.stop = stop;
            this.overwrite = overwrite;
        }
    }
    // piece of sh*t that i couldnt make it so you could call these by like "this.RETURN()" on fucking interface that'd be cool smfh
    public static class LeafiaRayTraceController<T> {
        public LeafiaRayTraceControl<T> RETURN(T value) { return new LeafiaRayTraceControl<>(value,true,true); }
        public LeafiaRayTraceControl<T> BREAK() { return new LeafiaRayTraceControl<>(null,true,false); }
        public LeafiaRayTraceControl<T> CONTINUE(T value) { return new LeafiaRayTraceControl<>(value,false,true); }
        public LeafiaRayTraceControl<T> CONTINUE() { return new LeafiaRayTraceControl<>(null,false,false); }
        private LeafiaRayTraceController() {};
    }
    @FunctionalInterface
    public interface LeafiaRayTraceFunction<T> {
        LeafiaRayTraceControl<T> process(LeafiaRayTraceController<T> process,LeafiaRayTraceConfig config,LeafiaRayTraceProgress current);
    }
    public static <T> T leafiaRayTraceBlocksCustom(World world,Vec3d start,Vec3d end,LeafiaRayTraceFunction<T> callback) {
        // since minecraft base raytrace is basically shit i made my own one
        Vec3d direction = end.subtract(start);
        Vec3d unit = direction.normalize();
        double[] inclinations = new double[2];
        double maxAxisValue;

        Vec3d dirAbs = new Vec3d(Math.abs(direction.x),Math.abs(direction.y),Math.abs(direction.z));
        Vec3d mVector;
        Vec3d aVector;
        Vec3d bVector;
        //EnumFacing.Axis maxAxis;
        EnumFacing facing;
        if ((dirAbs.x >= dirAbs.y) && (dirAbs.x >= dirAbs.z)) {
            facing = (direction.x > 0) ? EnumFacing.EAST : EnumFacing.WEST;
            //maxAxis = EnumFacing.Axis.X;
            mVector = new Vec3d(1,0,0);
            aVector = new Vec3d(0,1,0);
            bVector = new Vec3d(0,0,1);
            inclinations[0] = direction.y/dirAbs.x;
            inclinations[1] = direction.z/dirAbs.x;
            maxAxisValue = direction.x;
        } else if ((dirAbs.y >= dirAbs.x) && (dirAbs.y >= dirAbs.z)) {
            facing = (direction.y > 0) ? EnumFacing.UP : EnumFacing.DOWN;
            //maxAxis = EnumFacing.Axis.Y;
            mVector = new Vec3d(0,1,0);
            aVector = new Vec3d(1,0,0);
            bVector = new Vec3d(0,0,1);
            inclinations[0] = direction.x/dirAbs.y;
            inclinations[1] = direction.z/dirAbs.y;
            maxAxisValue = direction.y;
        } else if ((dirAbs.z >= dirAbs.x) && (dirAbs.z >= dirAbs.y)) {
            facing = (direction.z > 0) ? EnumFacing.SOUTH : EnumFacing.NORTH;
            //maxAxis = EnumFacing.Axis.Z;
            mVector = new Vec3d(0,0,1);
            aVector = new Vec3d(1,0,0);
            bVector = new Vec3d(0,1,0);
            inclinations[0] = direction.x/dirAbs.z;
            inclinations[1] = direction.y/dirAbs.z;
            maxAxisValue = direction.z;
        } else
            throw new RuntimeException("Cannot find maximum axis :/");
        LeafiaRayTraceConfig config = new LeafiaRayTraceConfig(
                world,start,end,unit,facing,
                aVector.scale(inclinations[0]).add(bVector.scale(inclinations[1]))
        );
        LeafiaRayTraceController<T> session = new LeafiaRayTraceController<>();
        T returnValue = null;
        for (int m = 0; m <= Math.abs(maxAxisValue); m++) {
            Vec3d posVec = start
                    .add(mVector.scale(m*Math.signum(maxAxisValue)))
                    .add(aVector.scale(m*inclinations[0]))
                    .add(bVector.scale(m*inclinations[1]));
            for (int a = (int)Math.floor(Math.abs(inclinations[0]*m)); a <= (int)Math.ceil(Math.abs(inclinations[0]*m)); a++) {
                for (int b = (int)Math.floor(Math.abs(inclinations[1]*m)); b <= (int)Math.ceil(Math.abs(inclinations[1]*m)); b++) {
                    BlockPos pos = new BlockPos(
                            start
                                    .add(mVector.scale(m*Math.signum(maxAxisValue)))
                                    .add(aVector.scale(a*Math.signum(inclinations[0])))
                                    .add(bVector.scale(b*Math.signum(inclinations[1])))
                    );
                    if (world.isValid(pos)) {
                        IBlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        LeafiaRayTraceProgress progress = new LeafiaRayTraceProgress(
                                m,posVec,pos,block,state,state.getMaterial()
                        );
                        LeafiaRayTraceControl<T> signal = callback.process(session,config,progress);
                        if (signal.overwrite) returnValue = signal.value;
                        if (signal.stop) return returnValue;
                    }
                }
            }
        }
        return returnValue;
    }
    @Nullable
    public static RayTraceResult leafiaRayTraceBlocks(World world, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        return leafiaRayTraceBlocksCustom(world,start,end,(process,config,current)->{
            Block block = current.block;
            IBlockState state = current.state;
            BlockPos pos = current.posSnapped;
            Vec3d posVec = current.posIntended;
            Vec3d unit = config.unitDir;
            if (!ignoreBlockWithoutBoundingBox || state.getMaterial() == Material.PORTAL || state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB)
            {
                if (block.canCollideCheck(state, stopOnLiquid))
                {
                    RayTraceResult result = state.collisionRayTrace(world, pos, posVec.subtract(unit.scale(2)), posVec.add(unit.scale(2)));

                    if (result != null)
                    {
                        return process.RETURN(result);
                    }
                }
                else if (returnLastUncollidableBlock)
                {
                    return process.CONTINUE(new RayTraceResult(RayTraceResult.Type.MISS, posVec, config.pivotAxisFace, pos));
                }
            }
            return process.CONTINUE();
        });
    }
	/**
	 * <s>Replaces all occurrences of <tt>a</tt> and <tt>b</tt> within <tt>s</tt> with the other.</s>
	 * <p>Too painful to make.
	 * @param s The string to replace texts within
	 * @param a Pattern A
	 * @param b Pattern B
	 * @return Resulting string
	 */
	public static String stringSwap(String s,String a,String b) {
		/*
		StringBuilder builder = new StringBuilder(s.length());
		StringBuilder patternA = new StringBuilder();
		StringBuilder patternB = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (a.charAt(patternA.length()) == c) {
				patternA.append(c);
				if (patternA.toString().equals(a)) {
					builder.append(patternA);
					patternA = new StringBuilder();
				}
			} else if (b.charAt(patternB.length()) == c) {
				patternB.append(c);
				if (patternB.toString().equals(b)) {
					builder.append(patternB);
					patternB = new StringBuilder();
				}
			} else {

			}
			if (patternA.length() >= a.length()) {
				builder.append(patternA);
				patternA = new StringBuilder();
			} else if (patternB.length() >= b.length()) {
				builder.append(patternB);
				patternB = new StringBuilder();
			}
		}
		return builder.toString();*/
		throw new UnsupportedOperationException("Too painful to make :/");
	}
	/**
	 * Replaces all occurrences of <tt>a</tt> and <tt>b</tt> within <tt>s</tt> with the other.
	 * @param s The string to replace texts within
	 * @param a Char A
	 * @param b Char B
	 * @return Resulting string
	 */
	public static String stringSwap(String s,char a,char b) {
		StringBuilder builder = new StringBuilder(s.length());
		for (char c : s.toCharArray()) {
			if (c == a)
				builder.append(b);
			else if (c == b)
				builder.append(a);
			else
				builder.append(c);
		}
		return builder.toString();
	}
	public static class NumScale {
		static public final int KILO = 1_000;
		static public final int MEGA = 1_000_000;
		static public final int GIGA = 1_000_000_000;
		static public final long TERRA = 1_000_000_000_000L;
		static public final long PETA = 1_000_000_000_000_000L;
		static public final long EXA = 1_000_000_000_000_000_000L;
		//,ZETTA,YOTTA,RONNA,QUETTA;
	}
	static int brailleStart = 0x2800;
	static int[] brailleMapping = new int[]{1<<6,1<<2,1<<1,1,1<<7,1<<5,1<<4,1<<3};
	public static String[] drawGraph(int width,int height,int lfSpacing,double minX,double maxX,double minY,double maxY,Function<Double,Double> callback) {
		double increment = (maxX-minX)/width/2;
		String[] output = new String[height];
		int dotHeight = height*4 + lfSpacing*(height-1);
		for (int i = 0; i < height; i++) output[i] = "";
		Integer prevY = null;
		for (int xb = 0; xb < width*2; xb+=2) {
			int[] bits = new int[height];
			int column = 0;
			for (int xf = xb; xf <= xb+1; xf++) {
				double xcal = xf*increment;
				double ycal = callback.apply(xcal);
				int yf = (int)((ycal-minY)/(maxY-minY)*dotHeight+0.5);
				int yTgt = yf;
				if (prevY != null) yTgt = prevY;
				if (Math.abs(yf-yTgt) >= 1) {
					int difference = yf-yTgt;
					yTgt += (int)Math.signum(difference);
				}
				int drawY0 = Math.min(yf,yTgt);
				int drawY1 = Math.max(yf,yTgt);
				for (int row = 0; row < height; row++) {
					int y0 = row*(4+lfSpacing+1);
					int y1 = y0+3;
					if (drawY0 < y0 && drawY1 < y0) continue;
					if (drawY0 > y1 && drawY1 > y1) continue;
					for (int yc = Math.max(drawY0,y0); yc <= Math.min(drawY1,y1); yc++) {
						int offset = yc-y0;
						bits[row] = bits[row] | brailleMapping[offset+column*4];
					}
				}
				column++;
				prevY = yf;
			}
			for (int i = 0; i < height; i++) {
				output[height-1-i] = output[height-1-i] + (char)(brailleStart+bits[i]);
			}
		}
		return output;
	}
}
