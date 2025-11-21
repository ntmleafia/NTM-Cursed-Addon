package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.tileentity.TileEntityMachineBase;
import com.llib.exceptions.messages.TextWarningLeafia;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileEntityMachineBase.class)
public class MixinTileEntityMachineBase extends TileEntityLoadedBase {
	// LCE anti-crash patch, allows loading Extended/Reloaded worlds
	@Redirect(method = {"readFromNBT","func_145839_a"},at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;deserializeNBT(Lnet/minecraft/nbt/NBTTagCompound;)V"))
	public void onReadFromNBT(ItemStackHandler instance,NBTTagCompound nbt) {
		if (nbt.getInteger("Size") == instance.getSlots())
			instance.deserializeNBT(nbt);
		else {
			System.out.println("WARNING: Invalid machine storage size! Resetting storage for block at "+pos.getX()+","+pos.getY()+","+pos.getZ());
			for (EntityPlayer plr : world.playerEntities)
				plr.sendMessage(new TextWarningLeafia("Invalid machine storage size! Resetting storage.. ("+pos.getX()+","+pos.getY()+","+pos.getZ()+")"));
		}
	}
}
