// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.fluid.CoriumFinite;
import com.hbm.blocks.machine.rbmk.RBMKBase;
import com.hbm.blocks.machine.rbmk.RBMKDebris;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.RBMKDials.RBMKKeys;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.reactors.rbmk.RBMKConstants;
import com.leafia.contents.machines.reactors.rbmk.debris.RBMKDebrisSmokeTE;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.optimization.LeafiaParticlePacket.JumpingRBMKParticle;
import com.leafia.dev.optimization.LeafiaParticlePacket.RBMKJetParticle;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityRBMKBase;
import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityRBMKBase.class)
public abstract class MixinTileEntityRBMKBase extends TileEntityLoadedBase implements IMixinTileEntityRBMKBase {
	@Unique public double leafia$jumpHeight;
	@Shadow(remap = false) public double heat;
	@Unique public boolean leafia$falling;
	@Shadow(remap = false) public abstract double maxHeat();
	@Unique public float leafia$downwardSpeed;
	@Shadow(remap = false) public abstract void meltdown();
	@Unique public int leafia$damage = 0;
	@Unique public double leafia$lastHeat = 20;

	@Override
	public int leafia$getDamage() {
		return leafia$damage;
	}

	@Inject(method = "serialize",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onSerialize(ByteBuf buf,CallbackInfo ci) {
		buf.writeDouble(leafia$jumpHeight);
	}
	@Inject(method = "deserialize",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onDeserialize(ByteBuf buf,CallbackInfo ci) {
		leafia$jumpHeight = buf.readDouble();
	}

	@Inject(method = "writeToNBT",at = @At(value = "HEAD"),require = 1)
	void leafia$onWriteToNBT(NBTTagCompound nbt,CallbackInfoReturnable<NBTTagCompound> cir) {
		nbt.setInteger("leafia_damage",leafia$damage); // damage to my heart ;(
	}

	@Inject(method = "readFromNBT",at = @At(value = "HEAD"),require = 1)
	void leafia$onReadFromNBT(NBTTagCompound nbt,CallbackInfo ci) {
		leafia$damage = nbt.getInteger("leafia_damage");
	}

	@Inject(method = "update",at = @At(value = "HEAD"),require = 1)
	void leafia$onUpdate(CallbackInfo ci) {
		if (!world.isRemote) {
			if (heat > maxHeat() && !world.getGameRules().getBoolean(RBMKKeys.KEY_DISABLE_MELTDOWNS.keyString)) {
				if (heat >= leafia$lastHeat || heat >= 2000)
					leafia$damage += IMixinTileEntityRBMKBase.dmgIncrement+(int)(Math.max(0,heat-3000)/2000);
			} else
				leafia$damage = Math.max(leafia$damage-1,0);
			if (leafia$damage > IMixinTileEntityRBMKBase.maxDamage)
				meltdown();
			leafia$lastHeat = heat;
			leafia$jump();
		}
	}
	@Override
	public double leafia$jumpHeight() {
		return leafia$jumpHeight;
	}

	@Unique boolean checkSurrounding(BlockPos p) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			IBlockState state = world.getBlockState(p.offset(facing));
			if (!LeafiaUtil.isSolidVisibleCube(state) && !(state.getBlock() instanceof RBMKBase))
				return false;
		}
		return true;
	}

	/// @author Alcater & Leafia
	@Unique
	private void leafia$jump(){
		if(leafia$damage <= 0 && !leafia$falling && leafia$jumpHeight <= 0)
			return;

		// do not jump if it has exposed sides that would reveal Z-fighting of jumping rods
		BlockPos topPos = new BlockPos(pos);
		Block block = world.getBlockState(pos).getBlock();
		while (true) {
			if (world.getBlockState(topPos.up()).getBlock() == block)
				topPos = topPos.up();
			else
				break;
		}
		if (!checkSurrounding(pos) || !checkSurrounding(pos.up()) || !checkSurrounding(topPos))
			return;

		if(!leafia$falling){ // linear rise
			if(leafia$damage > 0){
				int rand = world.rand.nextInt(Math.max(1,(IMixinTileEntityRBMKBase.maxDamage-leafia$damage)/3+5));
				if(this.leafia$jumpHeight > 0 || /*world.rand.nextInt((int)(25D*maxHeat()/(this.heat-MachineConfig.rbmkJumpTemp+200D))+1)*/rand == 0){
					if (this.leafia$jumpHeight == 0) {
						RBMKJetParticle particle = new RBMKJetParticle(20+world.rand.nextInt(10));
						particle.emit(
								new Vec3d(
										pos.getX()+0.5,
										pos.getY()+RBMKDials.getColumnHeight(world)-1,
										pos.getZ()+0.5
								),
								new Vec3d(0,1,0),
								world.provider.getDimension(),
								1000
						);
						new JumpingRBMKParticle(pos).emitServer(world);
					}
					int dmg = (int)(Math.pow(leafia$damage/(double)IMixinTileEntityRBMKBase.maxDamage,0.5)*100);
					double change = dmg*0.0005D;
					double heightLimit = dmg*0.005D;

					this.leafia$jumpHeight = this.leafia$jumpHeight + change;

					if(this.leafia$jumpHeight > heightLimit){
						this.leafia$jumpHeight = heightLimit;
						this.leafia$falling = true;
					}
				}
			} else {
				this.leafia$falling = true;
			}
		} else{ // gravity fall
			if(this.leafia$jumpHeight > 0){
				this.leafia$downwardSpeed = this.leafia$downwardSpeed+ RBMKConstants.gravity * 0.05F;
				this.leafia$jumpHeight = Math.max(this.leafia$jumpHeight-this.leafia$downwardSpeed,0);
			} else {
				this.leafia$jumpHeight = 0;
				this.leafia$downwardSpeed = 0;
				this.leafia$falling = false;
				world.playSound(null, pos.getX(),  pos.getY() + 4,  pos.getZ(), HBMSoundHandler.rbmkLid, SoundCategory.BLOCKS, 2.0F, 1.0F);
			}
		}
	}
	@Inject(method = "meltdown",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/EntityPlayer;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V",remap = true),require = 1,remap = false)
	void leafia$onMeltdown(CallbackInfo ci,@Local(name = "avgX") int avgX,@Local(name = "avgZ") int avgZ,@Local(name = "minX") int minX,@Local(name = "maxX") int maxX,@Local(name = "minZ") int minZ,@Local(name = "maxZ") int maxZ) {
		float scale = ((maxX-minX)+(maxZ-minZ))/2f/2f;
		for (int y = pos.getY()+RBMKDials.getColumnHeight(world)+1; y >= 0; y--) {
			BlockPos bp = new BlockPos(avgX,y,avgZ);
			if (world.getBlockState(bp).getBlock() instanceof RBMKDebris || world.getBlockState(bp).getBlock() instanceof CoriumFinite) {
				world.setBlockState(bp,AddonBlocks.pribris_smoke.getDefaultState());
				if (world.getTileEntity(bp) instanceof RBMKDebrisSmokeTE te)
					te.setScale(scale);
				break;
			}
		}
	}
}
