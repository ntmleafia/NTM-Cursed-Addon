// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.Landmine;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModBlocks.class)
public class MixinModBlocks {
	@Redirect(method="<clinit>",at=@At(value="NEW",target="Lcom/hbm/blocks/bomb/Landmine;"),remap=false,require=1)
	private static Landmine onNewLandmine(Material materialIn,String s,double range,double height) {
		if (s.equals("mine_ap"))
			range /= 4;
		return new Landmine(materialIn,s,range,height);
	}
}
