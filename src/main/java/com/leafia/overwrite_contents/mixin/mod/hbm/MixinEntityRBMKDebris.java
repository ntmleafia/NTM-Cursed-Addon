// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.entity.projectile.EntityRBMKDebris;
import com.leafia.contents.machines.reactors.rbmk.debris.RBMKDebrisSmoke;
import com.leafia.dev.LeafiaUtil;
import com.leafia.overwrite_contents.interfaces.IMixinDebrisBase;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRBMKDebris.class)
public abstract class MixinEntityRBMKDebris extends Entity implements IMixinDebrisBase {
	public MixinEntityRBMKDebris(World worldIn) {
		super(worldIn);
	}
	@Redirect(method = "onUpdate",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;"),require = 1)
	public RayTraceResult leafia$onRayTraceBlocks(World instance,Vec3d pos,Vec3d next,boolean st,boolean ig,boolean re) {
		if (this.leafia$destroysBlocks())
			return world.rayTraceBlocks(pos,next,st,ig,re);
		return null;
	}
	@Unique
	private static void leafia$fallBlock(World world,BlockPos pos,Vec3d velocity,int count) {
		if (world.isRemote) return;
		if (!world.getBlockState(pos.down()).getMaterial().isSolid()) {
			boolean fallNextBlock = false;
			IBlockState state = world.getBlockState(pos);
			if (LeafiaUtil.isSolidVisibleCube(state) && count < 5) {
				EntityFallingBlock fallingBlock = new EntityFallingBlock(world,pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,state);
				fallingBlock.fallTime = 1;
				if (velocity != null) {
					fallingBlock.motionX = velocity.x;
					fallingBlock.motionY = velocity.y;
					fallingBlock.motionZ = velocity.z;
				}
				world.setBlockToAir(pos);
				LeafiaPassiveServer.queueFunction(()->world.spawnEntity(fallingBlock));
				fallNextBlock = true;
			} else {
				if (state.getMaterial() != Material.AIR && !(state.getBlock() instanceof BlockAir)) {
					fallNextBlock = true;
					world.setBlockToAir(pos);
				}
			}
			BlockPos up = pos.up(); // just in case shit uses MutableBlockPos.. (i dont think it does though)
			if (fallNextBlock)
				LeafiaPassiveServer.queueFunction(()->leafia$fallBlock(world,up,null,count+1));
		}
	}
	// prevent debris breaking smoke block
	@Redirect(method = "onUpdate",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(Lnet/minecraft/util/math/BlockPos;)Z"),require = 1)
	public boolean leafia$onOnUpdate(World instance,BlockPos pos) {
		if (instance.getBlockState(pos).getBlock() instanceof RBMKDebrisSmoke)
			return false;
		BlockPos up = pos.up(); // just in case shit uses MutableBlockPos.. (i dont think it does though)
		Vec3d vel = new Vec3d(motionX,motionY,motionZ);
		if (world.rand.nextInt(5) == 0)
			leafia$fallBlock(instance,up,vel,0);
		else
			LeafiaPassiveServer.queueFunction(()->leafia$fallBlock(instance,up,vel,0));
		return instance.setBlockToAir(pos);
	}
	@Override
	public boolean leafia$destroysBlocks() {
		return true;
	}
}
