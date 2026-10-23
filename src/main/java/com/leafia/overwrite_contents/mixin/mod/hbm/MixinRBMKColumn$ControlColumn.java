// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.machine.rbmk.RBMKColumn;
import com.hbm.tileentity.machine.rbmk.RBMKColumn.ControlColumn;
import com.leafia.overwrite_contents.interfaces.IMixinRBMKColumn$ControlColumn;
import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ControlColumn.class)
public class MixinRBMKColumn$ControlColumn extends RBMKColumn implements IMixinRBMKColumn$ControlColumn {
	@Unique boolean leafia$exclamation = false;
	protected MixinRBMKColumn$ControlColumn(ColumnType type) {
		super(type);
	}
	@Inject(method = "serialize",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onSerialize(ByteBuf buf,CallbackInfo ci) {
		buf.writeBoolean(leafia$exclamation);
	}
	@Inject(method = "deserialize",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onDeserialize(ByteBuf buf,CallbackInfo ci) {
		leafia$exclamation = buf.readBoolean();
	}
	@SideOnly(Side.CLIENT)
	@Inject(method = "getFancyStats",at = @At(value = "RETURN"),require = 1,remap = false)
	void leafia$onGetFancyStats(CallbackInfoReturnable<List<String>> cir,@Local(type = List.class) List<String> stats) {
		if (leafia$exclamation)
			stats.add(TextFormatting.GOLD+"Rod is shut off");
	}
	@Override
	public boolean leafia$getExclamation() {
		return leafia$exclamation;
	}
	@Override
	public void leafia$setExclamation(boolean exclamation) {
		leafia$exclamation = exclamation;
	}
}
