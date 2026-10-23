// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.flywheel;

import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.recipes.ShredderRecipes;
import com.hbm.items.ModItems;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentTE;
import com.leafia.dev.math.FiaMatrix;
import com.leafia.init.LeafiaDamageSource;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MTFlywheelTE extends ModularTurbineComponentTE implements ITickable {
	public void grindEntity(Entity entity,double damage) {
		if (entity instanceof EntityPlayer player) {
			if (player.isCreative() || player.isSpectator())
				return;
		}
		if (getBlockType() instanceof ModularTurbineBlockBase base) {
			EnumFacing facing = EnumFacing.byIndex(getBlockMetadata()-10).getOpposite();
			EnumFacing side = facing.rotateY();
			BlockPos shaft = pos.up(base.shaftHeight());
			Vec3d s = new Vec3d(shaft.getX()+0.5,shaft.getY()+0.5,shaft.getZ()+0.5);
			FiaMatrix mat = new FiaMatrix(s,s.add(new Vec3d(facing.getDirectionVec())));
			FiaMatrix offset = mat.toObjectSpace(new FiaMatrix(new Vec3d(entity.posX,entity.posY,entity.posZ)));
			double mul = -1;
			if (facing.equals(EnumFacing.SOUTH) || facing.equals(EnumFacing.WEST))
				mul *= -1;
			mat = mat.rotateZ(core.getVisualDeltaAngle()*mul);
			FiaMatrix newPos = mat.travel(offset);

			BlockPos bp = new BlockPos(newPos.position);
			BlockPos headPos = new BlockPos(newPos.position.add(0,entity.getEyeHeight(),0));
			boolean grind = false;
			if (!(world.getBlockState(bp).getBlock() instanceof ModularTurbineBlockBase)) {
				if (world.getBlockState(bp).getMaterial().isSolid())
					grind = true;
			} else if (entity instanceof EntityLivingBase && world.getBlockState(headPos).causesSuffocation())
				grind = true;

			if (!grind)
				entity.setPosition(newPos.getX(),newPos.getY(),newPos.getZ());

			if (core.rps > 8)
				grind = true;
			if (!grind) return;

			if (!world.isRemote && damage > 0) {
				if (entity instanceof EntityLivingBase living) {
					living.attackEntityFrom(LeafiaDamageSource.flywheel,(float)damage);
					if (!living.isEntityAlive()) {
						// stolen code below
						NBTTagCompound vdat = new NBTTagCompound();
						vdat.setString("type","giblets");
						vdat.setInteger("ent",living.getEntityId());
						vdat.setInteger("cDiv",5);
						PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(vdat,living.posX,living.posY+living.height*0.5,living.posZ),new NetworkRegistry.TargetPoint(living.dimension,living.posX,living.posY+living.height*0.5,living.posZ,150));
						world.playSound(null,living.posX,living.posY,living.posZ,SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD,SoundCategory.BLOCKS,2.0F,0.95F+world.rand.nextFloat()*0.2F);
						living.setDead();
					}
				} else if (entity instanceof EntityItem item && damage >= 5) {
					ItemStack stack = item.getItem();
					RecipesCommon.ComparableStack comp = (new RecipesCommon.ComparableStack(stack)).makeSingular();
					ItemStack sta = ShredderRecipes.shredderRecipes.get(comp);
					if (sta == null) {
						comp.meta = 32767;
						sta = ShredderRecipes.shredderRecipes.get(comp);
					}
					if (sta != null && sta.getItem() != ModItems.dust) {
						int count = sta.getCount()*stack.getCount();
						int max = sta.getMaxStackSize();
						int iteration = (int)Math.ceil(count/(double)max);
						for (int i = 0; i < iteration; i++) {
							ItemStack stk = new ItemStack(sta.getItem(),1,sta.getMetadata());
							stk.setCount(i == iteration-1 ? Math.floorMod(count-1,max)+1 : max);
							InventoryHelper.spawnItemStack(world,item.posX,item.posY,item.posZ,stk);
						}
						item.setDead();
					}
				}
			}
		}
	}
	@SideOnly(Side.CLIENT)
	public void local$grindEntity(Entity entity,double damage) {
		if (entity.equals(Minecraft.getMinecraft().player))
			grindEntity(entity,damage);
	}
	@Override
	public void update() {
		if (getBlockType() instanceof ModularTurbineBlockBase base) {
			int hurtDiameter = base.size()/2*2+1;
			if (core != null && core.rps >= 0.005) {
				double damage = Math.max(0,Math.pow(core.weight,0.5)/2*(core.rps-0/*.5*/));
				EnumFacing facing = EnumFacing.byIndex(getBlockMetadata()-10).getOpposite();
				EnumFacing side = facing.rotateY();
				int dx = Math.abs(side.getXOffset());
				int dz = Math.abs(side.getZOffset());
				int dfx = Math.abs(facing.getXOffset());
				int dfz = Math.abs(facing.getZOffset());
				BlockPos shaft = pos.up(base.shaftHeight());
				double hurtRadius = hurtDiameter/2d+1;
				AxisAlignedBB aabb = new AxisAlignedBB(
						shaft.getX()+0.5-hurtRadius*dx-dfx/2d,
						shaft.getY()+0.5-hurtRadius,
						shaft.getZ()+0.5-hurtRadius*dz-dfz/2d,
						shaft.getX()+0.5+hurtRadius*dx+dfx/2d,
						shaft.getY()+0.5+hurtRadius,
						shaft.getZ()+0.5+hurtRadius*dz+dfz/2d
				);
				for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null,aabb)) {
					Vec3d p = new Vec3d(entity.posX,entity.posY,entity.posZ);
					Vec3d s = new Vec3d(shaft.getX()+0.5,shaft.getY()+0.5,shaft.getZ()+0.5);
					if (facing.getAxis() == Axis.X)
						p = new Vec3d(s.x,p.y,p.z);
					else if (facing.getAxis() == Axis.Z)
						p = new Vec3d(p.x,p.y,s.z);
					double threshold = hurtRadius-1+0.25+entity.width/2;
					if (s.distanceTo(p) < threshold || s.distanceTo(p.add(0,entity.height,0)) < threshold) {
						if (world.isRemote)
							local$grindEntity(entity,damage);
						else
							grindEntity(entity,damage);
					}
				}
			}
		}
	}
}
