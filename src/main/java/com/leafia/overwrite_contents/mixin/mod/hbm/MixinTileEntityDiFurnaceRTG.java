package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityDiFurnaceRTG;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityDiFurnaceRTG.class)
public abstract class MixinTileEntityDiFurnaceRTG extends TileEntityMachineBase {
	@Shadow(remap = false)
	public short progress;

	@Shadow(remap = false)
	public int rtgPower;

	@Shadow(remap = false)
	public abstract boolean canProcess();

	public MixinTileEntityDiFurnaceRTG(int scount) {
		super(scount);
	}
	@Inject(method = "<init>",at = @At("TAIL"),require = 1,remap = false)
	public void onInit(CallbackInfo ci) {
		inventory = getNewInventory(10,64);
	}
	/**
	 * @author ntmleafia
	 * @reason migeration bullsh*t
	 */
	@Overwrite
	public void readFromNBT(NBTTagCompound compound) {
		this.progress = compound.getShort("progress");
		this.rtgPower = compound.getInteger("rtgPower");
		if (compound.hasKey("inventory")) {
			if (compound.getCompoundTag("inventory").getInteger("Size") < 10)
				compound.getCompoundTag("inventory").setInteger("Size",10);
		}
		super.readFromNBT(compound);
	}
	@Inject(method = "update",at = @At(value = "INVOKE", target = "Lcom/hbm/tileentity/machine/TileEntityDiFurnaceRTG;hasPower()Z",ordinal = 0,remap = false),require = 1,cancellable = true)
	public void onUpdate(CallbackInfo ci) {
		if (inventory.getStackInSlot(9).getItem() instanceof LeafiaRodItem) {
			ItemStack stack = inventory.getStackInSlot(9);
			LeafiaRodItem rod = (LeafiaRodItem)stack.getItem();
			NBTTagCompound nbt = stack.getTagCompound();
			double fuelHeat = 20;
			if (nbt != null) {
				fuelHeat = nbt.getDouble("heat");
				//if (fuelHeat >= 2000) {
				if (nbt.getBoolean("nuke")) {
					for (int i = 0; i < inventory.getSlots(); i++)
						inventory.setStackInSlot(i, ItemStack.EMPTY);
					world.setBlockToAir(pos);
					rod.nuke(world,pos);
					ci.cancel();
					return;
				}
				if (nbt.getInteger("spillage") > 20*5) {
					ItemStack prevStack = null;
					for (int i = 0; i < inventory.getSlots(); i++) {
						prevStack = LeafiaRodItem.comparePriority(inventory.getStackInSlot(i), prevStack);
						inventory.setStackInSlot(i, ItemStack.EMPTY);
					}
					world.setBlockToAir(pos);
					LeafiaRodItem detonate = (LeafiaRodItem)prevStack.getItem();
					detonate.resetDetonate();
					detonate.detonateRadius = 2;
					detonate.detonate(world, pos);
					ci.cancel();
					return;
				}
				//}
			}
			if (canProcess()) {
				rtgPower += (int)Math.floor(Math.pow(fuelHeat/250,0.54)*15);
				rod.HeatFunction(stack,true,rod.getFlux(stack)*2,0,0,0);
				rod.decay(stack,inventory,9);
			} else
				rod.HeatFunction(stack,true,0,0,0,0);
		}
	}
	@Redirect(method = "canProcess",at = @At(value = "INVOKE", target = "Lcom/hbm/tileentity/machine/TileEntityDiFurnaceRTG;hasPower()Z"),require = 1,remap = false)
	public boolean onCanProcess(TileEntityDiFurnaceRTG instance) {
		if (inventory.getStackInSlot(9).getItem() instanceof LeafiaRodItem)
			return true;
		return instance.hasPower();
	}
}
