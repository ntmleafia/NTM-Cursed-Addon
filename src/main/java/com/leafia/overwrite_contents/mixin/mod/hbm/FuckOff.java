package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.bomb.BlockVolcano.TileEntityVolcanoCore;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityVolcanoCore.class)
public class FuckOff extends TileEntity {
	@Inject(method = "update",at = @At(value = "HEAD"),require = 1,cancellable = true)
	public void leafia$onUpdate(CallbackInfo ci) {
		//System.out.println("Volcano location: "+getPos().getX()+", "+getPos().getZ());
		if (world.isRemote) return;
		// volcanoes on servers are uberkis
		if (world.getMinecraftServer() == null) return;
		if (world.getMinecraftServer().isDedicatedServer())
			ci.cancel();
	}
}
