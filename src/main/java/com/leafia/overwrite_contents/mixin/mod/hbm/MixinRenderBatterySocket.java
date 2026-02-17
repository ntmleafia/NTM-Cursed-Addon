package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.machine.ItemBatteryPack;
import com.hbm.main.ResourceManager;
import com.hbm.render.tileentity.RenderBatterySocket;
import com.hbm.tileentity.machine.storage.TileEntityBatterySocket;
import com.hbm.util.EnumUtil;
//import com.leafia.contents.control.battery.AddonBatteryPackItem.AddonEnumBatteryPack;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityBatterySocket;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated // suck my butt
@Mixin(value = RenderBatterySocket.class)
public abstract class MixinRenderBatterySocket extends TileEntitySpecialRenderer<TileEntityBatterySocket> {
	/*@Inject(method = "render(Lcom/hbm/tileentity/machine/storage/TileEntityBatterySocket;DDDFIF)V",at = @At(value = "FIELD", target = "Lcom/hbm/tileentity/machine/storage/TileEntityBatterySocket;renderPack:I"),require = 1,remap = false)
	public void onRender(TileEntityBatterySocket tile,double x,double y,double z,float partialTicks,int destroyStage,float alpha,CallbackInfo ci) {
		IMixinTileEntityBatterySocket mixin = (IMixinTileEntityBatterySocket)tile;
		if (mixin.renderPackLeafia() >= 0) {
			AddonEnumBatteryPack pack = EnumUtil.grabEnumSafely(AddonEnumBatteryPack.class, mixin.renderPackLeafia());
			if (pack != null) {
				bindTexture(pack.texture);
				ResourceManager.battery_socket.renderPart(pack.isCapacitor() ? "Capacitor" : "Battery");
			}
		}
	}*/
}
