// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.world.WorldProviderNTM;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import com.leafia.eventbuses.LeafiaClientListener.Digamma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = WorldProviderNTM.class)
public class MixinWorldProviderNTM extends WorldProviderSurface {
	@Override
	@SideOnly(Side.CLIENT)
	public @Nullable MusicTicker.MusicType getMusicType() {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (DigammaCrater.isDigammaBiome(world.getBiome(new BlockPos(player.posX,player.posY,player.posZ))))
			return Digamma.silence;
		return super.getMusicType();
	}
}
