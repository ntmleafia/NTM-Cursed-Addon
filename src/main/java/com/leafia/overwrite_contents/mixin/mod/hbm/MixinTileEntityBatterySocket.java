package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.machine.storage.TileEntityBatteryBase;
import com.hbm.tileentity.machine.storage.TileEntityBatterySocket;
import com.leafia.contents.AddonItems;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityBatterySocket;
import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated // suck my butt
@Mixin(value = TileEntityBatterySocket.class)
public abstract class MixinTileEntityBatterySocket /*extends TileEntityBatteryBase implements IMixinTileEntityBatterySocket*/ {
	/*@Unique public int renderPackLeafia = -1;
	@Override
	public int renderPackLeafia() {
		return renderPackLeafia;
	}
	public MixinTileEntityBatterySocket(int slotCount) {
		super(slotCount);
	}
	@Inject(method = "serialize",at = @At(value = "TAIL"),require = 1,remap = false)
	public void onSerialize(ByteBuf buf,CallbackInfo ci) {
		int renderPackLeafia = -1;
		if (!inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getItem() == AddonItems.addon_battery_pack) {
			renderPackLeafia = inventory.getStackInSlot(0).getItemDamage();
		}
		buf.writeByte(renderPackLeafia);
	}
	@Inject(method = "deserialize",at = @At(value = "TAIL"),require = 1,remap = false)
	public void onDeserialize(ByteBuf buf,CallbackInfo ci) {
		renderPackLeafia = buf.readByte();
	}*/
}
