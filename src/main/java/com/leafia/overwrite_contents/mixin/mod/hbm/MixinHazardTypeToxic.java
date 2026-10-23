// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.config.RadiationConfig;
import com.hbm.handler.ArmorUtil;
import com.hbm.hazard.helper.HazardHelper;
import com.hbm.hazard.type.HazardTypeToxic;
import com.hbm.hazard.type.IHazardType;
import com.hbm.util.ArmorRegistry;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorWarningPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.hbm.hazard.helper.HazardHelper.applyPotionEffect;

@Mixin(value = HazardTypeToxic.class)
public abstract class MixinHazardTypeToxic implements IHazardType {
	/**
	 * @author ntmleafia
	 * @reason rework
	 */
	@Overwrite(remap = false)
	public void onUpdate(final EntityLivingBase target,final double level,final ItemStack stack) {

		if (RadiationConfig.disableToxic) return;

		final boolean reacher = HazardHelper.isHoldingReacher(target);
		//boolean hasToxFilter = false;
		boolean hasHazmat = false;

		if (target instanceof EntityPlayer player) {
			/*hasToxFilter = ArmorRegistry.hasProtection(player, EntityEquipmentSlot.HEAD, ArmorRegistry.HazardClass.NERVE_AGENT);

			if (hasToxFilter) {
				ArmorUtil.damageGasMaskFilter(player, hazardRate);
			}*/

			hasHazmat = ArmorUtil.checkForHazmat(player);
		}

		final boolean isUnprotected = !(/*hasToxFilter || */hasHazmat || reacher);

		if (isUnprotected) {
			applyPotionEffect(target, MobEffects.WEAKNESS, 110, (int) (level - 1));

			if (level > 2) {
				applyPotionEffect(target, MobEffects.SLOWNESS, 110, (int) Math.min(4, level - 4));
			}

			if (level > 4) {
				applyPotionEffect(target, MobEffects.HUNGER, 110, (int) level);
			}

			if (target instanceof EntityPlayer player)
				LeafiaCustomPacket.__start(new AdvisorWarningPacket(3)).__sendToClient(player);
		}

		if (!hasHazmat /*|| !hasToxFilter*/ || !reacher) {

			if (level > 6) {
				if (target instanceof EntityPlayer player)
					LeafiaCustomPacket.__start(new AdvisorWarningPacket(4)).__sendToClient(player);
			}

			if (level > 6 && target.world.rand.nextInt((int) (2000 / level)) == 0) {
				applyPotionEffect(target, MobEffects.POISON, 110, (int) (level - 4));
			}

			if (level > 8) {
				applyPotionEffect(target, MobEffects.MINING_FATIGUE, 110, (int) (level - 8));
			}

			if (level > 16) {
				applyPotionEffect(target, MobEffects.INSTANT_DAMAGE, 110, (int) (level - 16));
			}
		}
	}
}
