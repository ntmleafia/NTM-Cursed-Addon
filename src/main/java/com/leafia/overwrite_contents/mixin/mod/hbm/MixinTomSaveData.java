package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.saveddata.TomSaveData;
import com.leafia.overwrite_contents.interfaces.IMixinTomSaveData;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TomSaveData.class)
public class MixinTomSaveData implements IMixinTomSaveData {
	@Unique float leafia$seismic;
	@Override
	public void leafia$setSeismic(float v) {
		leafia$seismic = v;
	}
	@Override
	public float leafia$getSeismic() {
		return leafia$seismic;
	}
	@Inject(method = "writeToNBT",at = @At("HEAD"),require = 1)
	public void leafia$onWriteToNBT(NBTTagCompound nbt,CallbackInfoReturnable<NBTTagCompound> cir) {
		nbt.setFloat("seismic",leafia$seismic);
	}
	@Inject(method = "readFromNBT",at = @At("HEAD"),require = 1)
	public void leafia$onReadFromNBT(NBTTagCompound compound,CallbackInfo ci) {
		leafia$seismic = compound.getFloat("seismic");
	}
}
