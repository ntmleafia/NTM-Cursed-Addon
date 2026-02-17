package com.leafia.contents.machines.elevators.weight;

import com.leafia.contents.machines.elevators.EvPulleyTE;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.math.FiaMatrix;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EvWeightEntity extends Entity {
	public EvWeightEntity(World worldIn) {
		super(worldIn);
	}
	public static final DataParameter<Integer> PULLEY_X = EntityDataManager.createKey(EvWeightEntity.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> PULLEY_Y = EntityDataManager.createKey(EvWeightEntity.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> PULLEY_Z = EntityDataManager.createKey(EvWeightEntity.class,DataSerializers.VARINT);
	public EvPulleyTE pulley = null;
	public void findPulley(BlockPos basePos) {
		for (int i = (int)posY; i < 255; i++) {
			TileEntity te = world.getTileEntity(new BlockPos(basePos.getX(),i,basePos.getZ()));
			if (te instanceof EvPulleyTE) {
				pulley = (EvPulleyTE)te;
				BlockPos pos = pulley.getPos();
				dataManager.set(PULLEY_X,pos.getX());
				dataManager.set(PULLEY_Y,pos.getY());
				dataManager.set(PULLEY_Z,pos.getZ());
			}
		}
	}
	public void setPos(Vec3d pos) {
		posX = pos.x;
		posY = pos.y;
		posZ = pos.z;
	}
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		Vec3d pos = new Vec3d(posX,posY,posZ);
		FiaMatrix mat = new FiaMatrix(pos).rotateY(-rotationYaw);
		BlockPos bp = new BlockPos(mat.translate(-1.5,0,0).position);
		if (pulley == null)
			findPulley(bp);
		if (pulley != null) {
			EnumFacing dir = EnumFacing.byIndex(pulley.getBlockMetadata()-10).getOpposite();
			FiaMatrix mat2 = new FiaMatrix(new Vec3d(pulley.getPos().getX()+0.5,posY,pulley.getPos().getZ()+0.5)).rotateY(-dir.getHorizontalAngle());
			setPos(mat2.translate(1.40625,0,0).position);
			rotationYaw = -dir.getHorizontalAngle();
		}
		LeafiaDebug.debugPos(world,bp,1/20f,0xFFFF00,"Hello!");
	}
	@Override
	protected void entityInit() {
		this.dataManager.register(PULLEY_X,1);
		this.dataManager.register(PULLEY_Y,1);
		this.dataManager.register(PULLEY_Z,1);
	}
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {

	}
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {

	}
}
