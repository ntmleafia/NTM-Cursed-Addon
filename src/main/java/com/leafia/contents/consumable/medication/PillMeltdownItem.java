package com.leafia.contents.consumable.medication;

import com.hbm.capability.HbmLivingCapability;
import com.hbm.items.weapon.sedna.factory.ConfettiUtil;
import com.hbm.potion.HbmPotion;
import com.hbm.util.I18nUtil;
import com.leafia.contents.consumable.AddonFoodBaked;
import com.leafia.contents.miscellanous.slop.SlopTE;
import com.leafia.init.LeafiaDamageSource;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PillMeltdownItem extends AddonFoodBaked {
	public PillMeltdownItem(String s) {
		super(s,0,false);
		setAlwaysEdible();
	}
	@Override
	public void onFoodEaten(ItemStack stack,World world,EntityPlayer player) {
		if (!world.isRemote) {
			NBTTagCompound tag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			if (!tag.hasKey("pillMeltdownCooldown")) {
				tag.setInteger("pillMeltdownCooldown",59*60*20);
				player.addPotionEffect(new PotionEffect(HbmPotion.mutation,180*20));
				if (player.hasCapability(HbmLivingCapability.EntityHbmPropsProvider.ENT_HBM_PROPS_CAP,null))
					player.getCapability(HbmLivingCapability.EntityHbmPropsProvider.ENT_HBM_PROPS_CAP,null).setRads(0);
			} else {
				player.attackEntityFrom(LeafiaDamageSource.pillMeltdown,player.getMaxHealth());
				if (player.getHealth() > 0) {
					SlopTE.tryKill(player);
					player.onDeath(LeafiaDamageSource.pillMeltdown);
				}
				ConfettiUtil.gib(player);
			}
		}
	}
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		tooltip.addAll(Arrays.asList(I18nUtil.resolveKey("item.pill_meltdown.desc").split("\\$")));
	}
	@Override
	public int getMaxItemUseDuration(@NotNull ItemStack stack) {
		return 10;
	}
}
