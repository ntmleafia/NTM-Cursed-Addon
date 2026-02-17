package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.tileentity.machine.TileEntityReactorResearch;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityReactorResearch.class)
public abstract class MixinTileEntityReactorResearch extends TileEntityMachineBase {
	@Shadow(remap = false)
	public double level;

	@Shadow(remap = false)
	public int heat;

	@Shadow(remap = false)
	public int totalFlux;

	@Shadow(remap = false)
	protected abstract void explode();

	public MixinTileEntityReactorResearch(int scount) {
		super(scount);
	}
	@Unique double getHeatInSlot(int slot,LeafiaRodItem rod) {
		return rod.getFlux(inventory.getStackInSlot(slot));
	}
	@Unique double handleLeafiaFuel(int slot,double cool) {
		ItemStack stack = inventory.getStackInSlot(slot);
		LeafiaRodItem rod = (LeafiaRodItem)stack.getItem();
		double detectedHeat = 0;
		for (int offset = 1; slot-offset*5 >= 0; offset += 1) {
			detectedHeat += getHeatInSlot(slot-offset*5,rod)/Math.pow(2,offset-1);
		}
		for (int offset = 1; slot+offset*5 < 12; offset += 1) {
			detectedHeat += getHeatInSlot(slot+offset*5,rod)/Math.pow(2,offset-1);
		}
		switch(Math.floorMod(slot,5)) {
			case 0:
				detectedHeat += getHeatInSlot(slot+1,rod);
				if (slot < 10) {
					detectedHeat += getHeatInSlot(slot + 2, rod) / 2;
					detectedHeat += getHeatInSlot(slot + 3, rod) / 2;
				}
				if (slot > 4) {
					detectedHeat += getHeatInSlot(slot - 2, rod) / 2;
					detectedHeat += getHeatInSlot(slot - 3, rod) / 2;
				}
				break;
			case 1:
				detectedHeat += getHeatInSlot(slot-1,rod);
				if (slot < 10) {
					detectedHeat += getHeatInSlot(slot + 2, rod) / 2;
					detectedHeat += getHeatInSlot(slot + 3, rod) / 2;
				}
				if (slot > 4) {
					detectedHeat += getHeatInSlot(slot - 2, rod) / 2;
					detectedHeat += getHeatInSlot(slot - 3, rod) / 2;
				}
				break;
			case 2:
				detectedHeat += getHeatInSlot(slot+1,rod) + getHeatInSlot(slot+2,rod)/2;

				detectedHeat += getHeatInSlot(slot-2,rod)/2;
				detectedHeat += getHeatInSlot(slot+3,rod)/2;
				break;
			case 3:
				detectedHeat += getHeatInSlot(slot-1,rod) + getHeatInSlot(slot+1,rod);

				detectedHeat += getHeatInSlot(slot-2,rod)/2;
				detectedHeat += getHeatInSlot(slot+2,rod)/2;
				detectedHeat += getHeatInSlot(slot-3,rod)/2;
				detectedHeat += getHeatInSlot(slot+3,rod)/2;
				break;
			case 4:
				detectedHeat += getHeatInSlot(slot-1,rod) + getHeatInSlot(slot-2,rod)/2;

				detectedHeat += getHeatInSlot(slot-3,rod)/2;
				detectedHeat += getHeatInSlot(slot+2,rod)/2;
				break;
		}
		rod.HeatFunction(stack,true,detectedHeat*level,cool,20,400);
		rod.decay(stack,inventory,slot);
		NBTTagCompound data = stack.getTagCompound();
		if (data != null) {
			heat = heat + (int)(data.getDouble("cooled")*50);
			totalFlux += (int)(data.getDouble("incoming")/80);
			return data.getDouble("cooled");
		}
		return 0; // failsafe
	}
	@Inject(method = "update",at = @At(value = "INVOKE", target = "Lcom/hbm/tileentity/machine/TileEntityReactorResearch;reaction()V",remap = false,shift = Shift.AFTER),require = 1,cancellable = true)
	public void onUpdate(CallbackInfo ci) {
		for (int i = 0; i < 12; i++) {
			if (inventory.getStackInSlot(i).getItem() instanceof LeafiaRodItem rod) {
				handleLeafiaFuel(i,1);
				NBTTagCompound nbt = inventory.getStackInSlot(i).getTagCompound();
				if (nbt != null) {
					if (nbt.getBoolean("nuke")) {
						for (int j = 0; j < inventory.getSlots(); j++)
							inventory.setStackInSlot(j, ItemStack.EMPTY);
						world.setBlockToAir(pos);
						rod.nuke(world,pos.add(0,1,0));
						ci.cancel();
						return;
					}
					if (nbt.getInteger("spillage") > 20*5) {
						ItemStack prevStack = null;
						for (int j = 0; j < inventory.getSlots(); j++) {
							prevStack = LeafiaRodItem.comparePriority(inventory.getStackInSlot(j),prevStack);
							inventory.setStackInSlot(j,ItemStack.EMPTY);
						}
						world.setBlockToAir(pos);
						LeafiaRodItem detonate = (LeafiaRodItem) prevStack.getItem();
						detonate.resetDetonate();
						detonate.detonateRadius = 2;
						detonate.detonate(world,pos);
						explode();
						ci.cancel();
						return;
					}
				}
			}
		}
	}
}
