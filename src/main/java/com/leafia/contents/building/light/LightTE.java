package com.leafia.contents.building.light;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.blocks.BlockDummyable;
import com.hbm.lib.ForgeDirection;
import com.leafia.contents.AddonBlocks;
import com.leafia.dev.blocks.blockbase.AddonBlockDummyable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class LightTE extends TileEntity implements ITickable, IEnergyReceiverMK2 {
	long power = 0;
	static final long consumption = 1;
	static final long interval = 5;
	static final long maxPower = consumption*2;
	boolean isLoaded = true;
	int timer = 0;

	public void updateLightEmitters(BlockPos pos,boolean emit) {
		for (int i = 0; i < LightEmitter.lightRange; i++) {
			pos = pos.down();
			if (world.getBlockState(pos).getMaterial() == Material.AIR) {
				Block block = world.getBlockState(pos).getBlock();
				if (emit)
					world.setBlockState(pos,AddonBlocks.lightEmitter.getDefaultState(),2);
				else if (block == AddonBlocks.lightEmitter)
					world.setBlockToAir(pos);
			} else
				break;
		}
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof LightBlock) {
				BlockPos pos1;
				BlockPos pos2;
				int meta = state.getValue(AddonBlockDummyable.META);
				trySubscribe(world,pos.up(),ForgeDirection.UP);
				if (meta <= 13) {
					trySubscribe(world,pos.up().east(),ForgeDirection.UP);
					trySubscribe(world,pos.up().west(),ForgeDirection.UP);
					pos1 = pos.east();
					pos2 = pos.west();
				} else {
					trySubscribe(world,pos.up().north(),ForgeDirection.UP);
					trySubscribe(world,pos.up().south(),ForgeDirection.UP);
					pos1 = pos.north();
					pos2 = pos.south();
				}
				boolean isLit = block == AddonBlocks.lightLit;
				boolean shouldBeLit = power > 0;
				timer++;
				if (timer > interval) {
					timer = 0;
					power = Math.max(power-consumption,0);
				}
				if (shouldBeLit != isLit) {
					LightBlock chosenBlock = (LightBlock)(shouldBeLit ? AddonBlocks.lightLit : AddonBlocks.lightUnlit);
					world.setBlockState(pos,chosenBlock.getDefaultState().withProperty(AddonBlockDummyable.META,meta),2);
					chosenBlock.fillSpace(world,pos.getX(),pos.getY(),pos.getZ(),ForgeDirection.getOrientation(meta-BlockDummyable.offset),chosenBlock.getOffset());
					this.validate();
					world.setTileEntity(pos,this);
					updateLightEmitters(pos,isLit);
					updateLightEmitters(pos1,isLit);
					updateLightEmitters(pos2,isLit);
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		power = compound.getLong("power");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setLong("power",power);
		return super.writeToNBT(compound);
	}

	@Override
	public long getPower() {
		return power;
	}
	@Override
	public void setPower(long l) {
		power = l;
	}
	@Override
	public long getMaxPower() {
		return maxPower;
	}
	@Override
	public boolean isLoaded() {
		return isLoaded;
	}
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		isLoaded = false;
	}
}
