// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.machine.TileEntityCrate;
import com.hbm.tileentity.machine.storage.TileEntityCrateBase;
import com.leafia.contents.nonmachines.storage.items.CrateLabelContainer;
import com.leafia.contents.nonmachines.storage.items.CrateLabelGUI;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityCrate.class)
public abstract class MixinTileEntityCrate extends TileEntityCrateBase {
	public MixinTileEntityCrate(int scount) {
		super(scount);
	}
	@Inject(method = "provideContainer",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	public void leafia$onProvideContainer(int ID,EntityPlayer player,World world,int x,int y,int z,CallbackInfoReturnable<Container> cir) {
		if (ID >= 1121 && ID < 1121+4) {
			cir.setReturnValue(new CrateLabelContainer(player.inventory,this));
			cir.cancel();
		}
	}
	@SideOnly(Side.CLIENT)
	@Inject(method = "provideGUI",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	public void leafia$onProvideGUI(int ID,EntityPlayer player,World world,int x,int y,int z,CallbackInfoReturnable<GuiScreen> cir) {
		if (ID >= 1121 && ID < 1121+4) {
			cir.setReturnValue(new CrateLabelGUI(player.inventory,this,world.getBlockState(pos),EnumFacing.byHorizontalIndex(ID-1121)));
			cir.cancel();
		}
	}
}
