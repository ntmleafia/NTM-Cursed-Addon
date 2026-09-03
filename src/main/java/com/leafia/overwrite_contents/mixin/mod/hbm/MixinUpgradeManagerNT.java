package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.tileentity.network.RTTYSystem;
import com.hbm.tileentity.network.RTTYSystem.RTTYChannel;
import com.leafia.contents.AddonItems;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Mixin(value = UpgradeManagerNT.class)
public class MixinUpgradeManagerNT {
	@Shadow(remap = false)
	public HashMap<UpgradeType,Integer> upgrades;
	@Unique World leafia$world;
	@Unique List<String> leafia$channels = new ArrayList<>();
	@Inject(method = "checkSlotsInternal",at = @At(value = "HEAD"),remap = false,require = 1)
	public void leafia$onCheckSlotsInternal(TileEntity te,ItemStack[] slots,int start,int end,CallbackInfo ci) {
		ItemStack[] upgradeSlots = Arrays.copyOfRange(slots, start, end + 1);
		leafia$world = te.getWorld();
		leafia$channels.clear();
		for (ItemStack stack : slots) {
			if (stack.getItem() == AddonItems.upgrade_control) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag == null) tag = new NBTTagCompound();
				String freq = tag.getString("freq");
				leafia$channels.add(tag.getString("freq"));
			}
		}
	}
	@Inject(method = "getLevel",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	public void leafia$onGetLevel(UpgradeType type,CallbackInfoReturnable<Integer> cir) {
		if (leafia$world == null) return;
		for (String freq : leafia$channels) {
			RTTYChannel chan = RTTYSystem.listen(leafia$world,freq);
			int level = upgrades.getOrDefault(type,0);
			if (chan != null) {
				String msg = ""+chan.signal;
				int lvl = 0;
				try {
					lvl = Integer.parseInt(msg);
				} catch (Exception ignored) {}
				level = Math.min(lvl,level);
			} else
				level = 0;
			cir.setReturnValue(level);
			cir.cancel();
		}
	}
}
