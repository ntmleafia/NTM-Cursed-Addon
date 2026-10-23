// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.tool.ItemGeigerCounter;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.util.ContaminationUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ItemGeigerCounter.class)
public class MixinItemGeigerCounter {
	/**
	 * @author ntmleafia
	 * @reason the geigers are too loud at low rad levels
	 */
	@Overwrite(remap = false)
	public static void playGeiger(World world,EntityPlayer player){
		if (world.isRemote) return;
		double x = ContaminationUtil.getPlayerRads(player);

		if(world.getTotalWorldTime() % 5 == 0) {

			if(x > 1e-5) {
				List<Integer> list = new ArrayList<>();
				float pitch = 1;
				if(x < 1) list.add(0);
				if(x < 5) list.add(0);
				if(x < 10) list.add(1);
				if(x >= 10 && x < 40) list.add(2);
				if(x >= 20 && x < 60) list.add(3);
				if(x >= 40 && x < 90) list.add(4);
				if(x >= 70 && x < 130) list.add(5);
				if(x >= 100 && x <= 350) list.add(6);
				if(x >= 300 && x <= 800) list.add(7);
				if(x >= 700 && x <= 1600) list.add(8);
				if(x >= 1300) list.add(9);
				if (x > 50)
					pitch = Math.min(pitch+(float)((x-200)/35000),1.1f);
				int r = list.get(world.rand.nextInt(list.size()));

				if(r > 0) world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.geigerSounds[r - 1], SoundCategory.PLAYERS, 1.0F, pitch);
			} else if(world.rand.nextInt(100) == 0) {
				world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.geigerSounds[(world.rand.nextInt(1))], SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}
	}
}
