package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityWasteDrum;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.dev.LeafiaDebug;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityWasteDrum.class)
public abstract class MixinTileEntityWasteDrum extends TileEntityMachineBase {
	public MixinTileEntityWasteDrum(int scount) {
		super(scount);
	}
	@Inject(method = "update",at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;getStackInSlot(I)Lnet/minecraft/item/ItemStack;",ordinal = 0,remap = false),require = 1)
	public void onUpdate(CallbackInfo ci,@Local(name = "i") int i,@Local(name = "r") int r) {
		ItemStack stack = inventory.getStackInSlot(i);
		if(!stack.isEmpty()) {
			if (stack.getItem() instanceof LeafiaRodItem) {
				LeafiaRodItem rod = (LeafiaRodItem)stack.getItem();
				rod.HeatFunction(stack,true,0,r/(float)(60*60*5),20,100);
				NBTTagCompound tag = stack.getTagCompound();
				if (tag != null) {
					double d = r/60d/60/20/30;
					double decay = tag.getDouble("decay");
					tag.setDouble("decay",Math.max(decay-d,0));
				}
			}
		}
	}
}
