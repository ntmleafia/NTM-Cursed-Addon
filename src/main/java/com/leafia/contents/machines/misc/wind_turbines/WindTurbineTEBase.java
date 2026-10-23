// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.wind_turbines;

import com.hbm.api.energymk2.IEnergyProviderMK2;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.leafia.dev.container_utility.LeafiaPacketReceiver;
import com.leafia.passive.Wind;
import com.llib.LeafiaLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

public abstract class WindTurbineTEBase extends TileEntityLoadedBase implements ITickable, LeafiaPacketReceiver, IEnergyProviderMK2 {
	public float bladeAnglePrev = 0;
	public float bladeAngle = 0;
	public abstract int lubeConsumptionPeriod();
	public FluidTankNTM lube = new FluidTankNTM(Fluids.LUBRICANT,3600*20/lubeConsumptionPeriod());
	public boolean obstructed = false;
	public void addBladeAngle(float ang) {
		bladeAnglePrev = bladeAngle;
		bladeAngle += ang;
		if (bladeAngle >= 360) {
			bladeAngle -= 360;
			bladeAnglePrev -= 360;
		}
	}
	/// returns true if it has clear line of sight
	public boolean raytrace(double height,double angle,int range) {
		double dx = Math.cos(angle/180*Math.PI);
		double dz = -Math.sin(angle/180*Math.PI);
		Vec3d start = new Vec3d(pos.getX()+0.5,pos.getY()+height,pos.getZ()+0.5).add(dx*(0.707+0.01),0,dz*(0.707+0.01));
		Vec3d end = start.add(dx*range,0.1*range,dz*range);
		RayTraceResult result = LeafiaLib.leafiaRayTraceBlocks(world,start,end,true,true,false);
		return result == null || result.typeOfHit == Type.MISS;
	}
	/// returns true if it's obstructed
	public boolean checkForObstruction(double height,int range) {
		double angle = Wind.angle+180;
		if (!raytrace(height,angle+10,range)) {
			if (!raytrace(height,angle-10,range))
				return true;
		}
		return false;
	}
	boolean firstTick = true;
	public void updateObstructed(double height,int range) {
		if (world.rand.nextInt(100) == 0 || firstTick) {
			obstructed = checkForObstruction(height,range);
			firstTick = false;
		}
	}
	int lubeTimer = 0;
	public void consumeLube() {
		if (generated > 10) {
			lubeTimer++;
			if (lubeTimer > lubeConsumptionPeriod()) {
				lubeTimer = 0;
				lube.setFill(Math.max(0,lube.getFill()-1));
			}
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("obstructed",obstructed);
		compound.setInteger("lube",lube.getFill());
		return super.writeToNBT(compound);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		obstructed = compound.getBoolean("obstructed");
		lube.setFill(compound.getInteger("lube"));
	}
	@Override
	public void onPlayerValidate(EntityPlayer plr) { }
	@Override
	public void onReceivePacketLocal(byte key,Object value) {
		if (key == 31)
			obstructed = (boolean)value;
		if (key == 30)
			generated = (long)value;
		else if (key == 29)
			lube.setFill((int)value);
	}
	@Override
	public double affectionRange() {
		return 512;
	}
	@Override
	public void onReceivePacketServer(byte key,Object value,EntityPlayer plr) { }
	@Override
	public double getMaxRenderDistanceSquared() {
		return 512*512-16;
	}
	public long generated = 0;
	@Override
	public void setPower(long l) {
		generated = l;
	}
	@Override
	public long getPower() {
		return generated;
	}
	@Override
	public long getMaxPower() {
		return 100000;
	}
	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir.toEnumFacing().equals(EnumFacing.DOWN);
	}
}