package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.bomb.TileEntityLandmine;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityLandmine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityLandmine.class)
public class MixinTileEntityLandmine extends TileEntity implements IMixinTileEntityLandmine {
	@Redirect(method = "update",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/EntityPlayer;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V"),require = 1)
	public void leafia$onUpdate(World instance,EntityPlayer player,double x,double y,double z,SoundEvent soundEvent,SoundCategory soundCategory,float v,float p) {
		instance.playSound(player,x,y,z,soundEvent,soundCategory,0.5f,p);
	}
	@Unique protected boolean leafia$playerPlaced = false;
	@Override
	public void leafia$setPlayerPlaced() {
		leafia$playerPlaced = true;
	}
	@Override
	public boolean leafia$getPlayerPlaced() {
		return leafia$playerPlaced;
	}
	@Inject(method = "readFromNBT",at = @At(value = "HEAD"),require = 1)
	public void leafia$onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
		if (compound.getBoolean("byPlayer"))
			leafia$setPlayerPlaced();
	}
	@Inject(method = "writeToNBT",at = @At(value = "HEAD"),require = 1)
	public void leafia$onWriteToNBT(NBTTagCompound compound,CallbackInfoReturnable<NBTTagCompound> cir) {
		compound.setBoolean("byPlayer",leafia$playerPlaced);
	}
}
