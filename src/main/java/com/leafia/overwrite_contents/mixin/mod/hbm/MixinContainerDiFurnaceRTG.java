package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.container.ContainerDiFurnaceRTG;
import com.hbm.tileentity.machine.TileEntityDiFurnaceRTG;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerDiFurnaceRTG.class)
public abstract class MixinContainerDiFurnaceRTG extends Container {
	@Inject(method = "<init>",at = @At("TAIL"),require = 1,remap = false)
	public void onInit(InventoryPlayer playerInv,TileEntityDiFurnaceRTG teIn,CallbackInfo ci) {
		// Leafia rod
		this.addSlotToContainer(new SlotItemHandler(teIn.inventory, 9, 4, 36));
	}
}