// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators.weight;

import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.elevators.EvPulleyTE;
import com.leafia.contents.machines.elevators.EvShaftNeo;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.math.FiaMatrix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
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
		setPosition(pos.x,pos.y,pos.z);
	}
	public void kms() {
		if (!isEntityAlive()) return;
		if (world.isRemote) return;
		entityDropItem(new ItemStack(AddonItems.weight_spawn),0);
		setDead();
	}
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		double renderMaxHeight = posY+3.0625;
		if (pulley != null) {
			double pulleyHeight = pulley.getPos().getY();
			if (renderMaxHeight < pulleyHeight)
				renderMaxHeight = pulleyHeight;
		}
		return new AxisAlignedBB(new Vec3d(posX-0.75,posY,posZ-0.75),new Vec3d(posX+0.75,renderMaxHeight,posZ+0.75));
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (!(world.getBlockState(new BlockPos(posX,posY,posZ)).getBlock() instanceof EvShaftNeo)) {
			kms();
			return;
		}
		Vec3d pos = new Vec3d(posX,posY,posZ);
		FiaMatrix mat = new FiaMatrix(pos).rotateY(-rotationYaw);
		BlockPos bp = new BlockPos(mat.translate(-1.5,0,0).position);
		if (pulley != null && pulley.isInvalid()) pulley = null;
		if (pulley == null) {
			findPulley(bp);
			if (pulley == null)
				motionY -= 9.8/400;
		}
		move(MoverType.SELF,motionX,motionY,motionZ);
		if (pulley == null) {
			if (this.onGround)
				kms();
		} else {
			EnumFacing dir = EnumFacing.byIndex(pulley.getBlockMetadata()-10).getOpposite();
			FiaMatrix mat2 = new FiaMatrix(new Vec3d(pulley.getPos().getX()+0.5,posY,pulley.getPos().getZ()+0.5)).rotateY(-dir.getHorizontalAngle());
			setPos(mat2.translate(1.40625,0,0).position);
			rotationYaw = dir.getHorizontalAngle();
			if (pulley.setupDistWeight < 0)
				pulley.setupDistWeight = pulley.getPos().getY()-posY;
			pulley.counterweight = this;
		}
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
