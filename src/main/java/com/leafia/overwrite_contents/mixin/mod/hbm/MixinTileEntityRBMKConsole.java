package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.machine.rbmk.TileEntityRBMKConsole;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityRBMKControlAuto;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityRBMKConsole.class)
public class MixinTileEntityRBMKConsole extends TileEntity {
	@Shadow(remap = false) private int targetX;
	@Shadow(remap = false) private int targetY;
	@Shadow(remap = false) private int targetZ;

	@Inject(method = "receiveControl",at = @At(value = "HEAD"),remap = false)
	void leafia$onReceiveControl(EntityPlayerMP player,NBTTagCompound data,CallbackInfo ci) {
		if (data.hasKey("fuckingstopalldamnautocontrolrods")) {
			for (int i = -7; i <= 7; i++) {
				for (int j = -7; j <= 7; j++) {
					TileEntity te = world.getTileEntity(new BlockPos(targetX + i, targetY, targetZ + j));
					if (te instanceof IMixinTileEntityRBMKControlAuto mixin) {
						mixin.leafia$setScrammed(true);
						te.markDirty();
					}
				}
			}
		}
	}
}
