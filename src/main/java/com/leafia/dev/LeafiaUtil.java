package com.leafia.dev;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityProxyBase;
import com.leafia.contents.network.FFNBT;
import com.leafia.contents.network.ff_duct.FFDuctTE;
import com.leafia.contents.network.ff_duct.uninos.IFFConnector;
import com.leafia.contents.network.ff_duct.uninos.IFFHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;

public class LeafiaUtil {
	public static int colorFromTextFormat(TextFormatting formatting) {
		int index = formatting.getColorIndex();
		int offset = (index>>3)*0x55; // colors go 00AA 55FF, and 55+AA is FF so we can just do this
		return (offset<<16) + (offset<<8) + (offset)
				+ ((index>>2 &1)*0xAA<<16) // r
				+ ((index>>1 &1)*0xAA<<8) // g
				+ ((index &1)*0xAA); // b
	}
	public static boolean isSolidVisibleCube(IBlockState state) {
		return state.isFullCube() && state.getMaterial().isSolid() && !state.getMaterial().isReplaceable() && state.getRenderType().equals(EnumBlockRenderType.MODEL);
	}
	// For server. Stupid minecraft has SideOnly(Side.CLIENT) on the constructor so this is a bypass to that
	public static AxisAlignedBB createAABB(Vec3d min,Vec3d max) {
		return new AxisAlignedBB(min.x,min.y,min.z,max.x,max.y,max.z);
	}
	public static Vec3d getKnockback(Vec3d entityPos,Vec3d blastPos,double amplitude) {
		Vec3d deltaPos = entityPos.subtract(blastPos);
		double dist = deltaPos.length();
		//Vec3d unit = new Vec3d(deltaPos.x/dist,deltaPos.y/dist,deltaPos.z/dist);
		float force = (float)Math.pow(Math.pow(Math.max(amplitude-dist*0.5,0)/amplitude,2)*amplitude,0.9)/4f;
		return new Vec3d(deltaPos.x/dist*force,deltaPos.y/dist*force,deltaPos.z/dist*force);
	}
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
	public static RayTraceResult leafiaRayTraceBlocks(World world,Vec3d start,Vec3d end,boolean stopOnLiquid,boolean ignoreBlockWithoutBoundingBox,boolean returnLastUncollidableBlock) {
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
	public static AxisAlignedBB getAABBRadius(Vec3d center,double radius) {
		return new AxisAlignedBB(center.x-radius,center.y-radius,center.z-radius,center.x+radius,center.y+radius,center.z+radius);
	}

	public static Vec3d getBlockPosCenter(BlockPos pos) {
		return new Vec3d(pos).add(0.5,0.5,0.5);
	}

	public static boolean canConnectFF(IBlockAccess world,int x,int y,int z,ForgeDirection dir /* duct's connecting side */,FluidType type) {
		return canConnectFF(world,new BlockPos(x,y,z),type,dir.toEnumFacing().getOpposite());
	}

	public static boolean canConnectFF(IBlockAccess world,BlockPos pos,FluidType type,@Nullable EnumFacing facing){
		TileEntity tileentity = world.getTileEntity(pos);
		if(tileentity instanceof FFDuctTE && ((FFDuctTE)tileentity).getType() == type)
			return true;
		TileEntity mainTE = tileentity;
		if (tileentity instanceof TileEntityProxyBase proxy)
			mainTE = proxy.getTE();
		if(tileentity != null && !(tileentity instanceof FFDuctTE) && tileentity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing) && mainTE instanceof IFFHandler) {
			return true;
		}
		//Block block = world.getBlockState(pos).getBlock();
		//if(block instanceof IFluidVisualConnectable)
		//	return ((IFluidVisualConnectable)block).shouldConnect(type);
		return false;
	}

	public static int fillFF(FluidTank sending,FluidTank receiving,int amount) {
		if (sending.getFluid() == null) return 0;
		if (amount <= 0) return 0;
		FFNBT.transferTags(sending,receiving);
		if (receiving.getFluid() == null) {
			FluidStack stack = sending.getFluid().copy();
			stack.amount = amount;
			receiving.setFluid(stack);
			sending.drain(amount,true);
			return amount;
		} else {
			int toFill = Math.min(receiving.getCapacity()-receiving.getFluidAmount(),amount);
			receiving.getFluid().amount += toFill;
			sending.drain(amount,true);
			return toFill;
		}
	}

	public static boolean setTypeExcluding(FluidTankNTM tank,int in,int out,@NotNull IItemHandler slots,FluidType... excludeTypes) {

		if (!slots.getStackInSlot(in).isEmpty() && slots.getStackInSlot(in).getItem() instanceof IItemFluidIdentifier id) {

			if (in == out) {
				FluidType newType = id.getType(null, 0, 0, 0, slots.getStackInSlot(in));
				for (FluidType excludeType : excludeTypes) {
					if (excludeType.equals(newType))
						return false;
				}

				if (tank.getTankType() != newType) {
					tank.setTankType(newType);
					tank.setFill(0);
					return true;
				}

			} else if (slots.getStackInSlot(out).isEmpty()) {
				FluidType newType = id.getType(null, 0, 0, 0, slots.getStackInSlot(in));
				for (FluidType excludeType : excludeTypes) {
					if (excludeType.equals(newType))
						return false;
				}

				if (tank.getTankType() != newType) {
					tank.setTankType(newType);
					slots.insertItem(out, slots.getStackInSlot(in).copy(), false);
					slots.getStackInSlot(in).shrink(1);
					tank.setFill(0);
					return true;
				}
			}
		}

		return false;
	}
	public static boolean setTypeOnly(FluidTankNTM tank,int in,int out,@NotNull IItemHandler slots,FluidType... allowedTypes) {

		if (!slots.getStackInSlot(in).isEmpty() && slots.getStackInSlot(in).getItem() instanceof IItemFluidIdentifier id) {

			if (in == out) {
				FluidType newType = id.getType(null, 0, 0, 0, slots.getStackInSlot(in));
				boolean allow = false;
				for (FluidType usableType : allowedTypes) {
					if (usableType.equals(newType)) {
						allow = true;
						break;
					}
				}

				if (tank.getTankType() != newType && allow) {
					tank.setTankType(newType);
					tank.setFill(0);
					return true;
				}

			} else if (slots.getStackInSlot(out).isEmpty()) {
				FluidType newType = id.getType(null, 0, 0, 0, slots.getStackInSlot(in));
				boolean allow = false;
				for (FluidType usableType : allowedTypes) {
					if (usableType.equals(newType)) {
						allow = true;
						break;
					}
				}

				if (tank.getTankType() != newType && allow) {
					tank.setTankType(newType);
					slots.insertItem(out, slots.getStackInSlot(in).copy(), false);
					slots.getStackInSlot(in).shrink(1);
					tank.setFill(0);
					return true;
				}
			}
		}

		return false;
	}
	public static boolean setTypeBy(FluidTankNTM tank,int in,int out,@NotNull IItemHandler slots,Function<FluidType,Boolean> processor,FluidType... excludeTypes) {

		if (!slots.getStackInSlot(in).isEmpty() && slots.getStackInSlot(in).getItem() instanceof IItemFluidIdentifier id) {

			if (in == out) {
				FluidType newType = id.getType(null, 0, 0, 0, slots.getStackInSlot(in));
				if (!processor.apply(newType)) return false;
				for (FluidType excludeType : excludeTypes) {
					if (excludeType.equals(newType))
						return false;
				}

				if (tank.getTankType() != newType) {
					tank.setTankType(newType);
					tank.setFill(0);
					return true;
				}

			} else if (slots.getStackInSlot(out).isEmpty()) {
				FluidType newType = id.getType(null, 0, 0, 0, slots.getStackInSlot(in));
				if (!processor.apply(newType)) return false;
				for (FluidType excludeType : excludeTypes) {
					if (excludeType.equals(newType))
						return false;
				}

				if (tank.getTankType() != newType) {
					tank.setTankType(newType);
					slots.insertItem(out, slots.getStackInSlot(in).copy(), false);
					slots.getStackInSlot(in).shrink(1);
					tank.setFill(0);
					return true;
				}
			}
		}

		return false;
	}
	/// I think there's a better way to do this but since I'm retarded here we are
	public static String getFormatDecimal(double value,int minDecimals,int maxDecimals) {
		value -= Math.floor(value);
		String s = Double.toString(value);
		if (s.endsWith(".0")) s = s.substring(0,s.length()-2);
		boolean startCounting = false;
		int decimals = 0;
		for (char c : s.toCharArray()) {
			if (c == '.')
				startCounting = true;
			else if (startCounting)
				decimals++;
		}
		decimals = MathHelper.clamp(decimals,minDecimals,maxDecimals);
		return "1."+decimals+"f";
	}
}