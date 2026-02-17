package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.bomb.TileEntityLaunchPadBase;
import com.leafia.contents.bomb.missile.customnuke.entity.CustomNukeMissileEntity;
import com.leafia.contents.bomb.missile.customnuke.CustomNukeMissileItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityLaunchPadBase.class)
public abstract class MixinTileEntityLaunchPadBase extends TileEntityMachineBase {
	@Shadow(remap = false)
	public abstract double getLaunchOffset();
	public MixinTileEntityLaunchPadBase(int scount) {
		super(scount);
	}
	@Inject(method = "instantiateMissile",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	public void onInstantiateMissile(int targetX,int targetZ,CallbackInfoReturnable<Entity> cir) {
		ItemStack stack = inventory.getStackInSlot(0);
		if (stack.isEmpty()) return;
		if (stack.getItem() instanceof CustomNukeMissileItem) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt != null && nbt.hasKey("Inventory")) {
				nbt = nbt.getCompoundTag("Inventory");
				if (nbt.getFloat("tnt") > 0) {
					CustomNukeMissileEntity missile = new CustomNukeMissileEntity(
							world,pos.getX()+0.5F,pos.getY()+(float)getLaunchOffset(),pos.getZ()+0.5F,targetX,targetZ,
							nbt.getFloat("tnt"),
							nbt.getFloat("nuke"),
							nbt.getFloat("hydro"),
							nbt.getFloat("bale"),
							nbt.getFloat("dirty"),
							nbt.getFloat("schrab"),
							nbt.getFloat("sol"),
							nbt.getFloat("euph")
					);
					cir.setReturnValue(missile);
					cir.cancel();
					return;
				}
			}
			cir.setReturnValue(null);
			cir.cancel();
		}
	}
}
