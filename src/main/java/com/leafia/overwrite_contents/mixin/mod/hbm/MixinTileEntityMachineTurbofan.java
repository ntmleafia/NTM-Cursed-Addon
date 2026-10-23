// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.machine.TileEntityMachinePolluting;
import com.hbm.tileentity.machine.TileEntityMachineTurbofan;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileEntityMachineTurbofan.class)
public abstract class MixinTileEntityMachineTurbofan extends TileEntityMachinePolluting {
	public MixinTileEntityMachineTurbofan(int scount,int buffer) {
		super(scount,buffer);
	}
	// who's the retard that decided to make this deserialize twice??
	@Redirect(method = "readFromNBT",at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;deserializeNBT(Lnet/minecraft/nbt/NBTTagCompound;)V",remap = false),require = 1)
	void leafia$onReadFromNBT(ItemStackHandler instance,NBTTagCompound compound) {
		// do nothing
	}
}
