package com.leafia.contents.gear.combat.shields;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.entity.projectile.*;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.I18nUtil;
import com.leafia.AddonBase;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.Shields;
import com.leafia.contents.gear.combat.guns.am_rifle.AMRifleBeam;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket.SyncVelocityPacket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddonShieldItem extends ItemShield {
	private final String materialOreDict;

	public AddonShieldItem(String matOd,ToolMaterial material,String s){
		this(matOd, material.getMaxUses(), s);
	}

	public AddonShieldItem(String matOd,int dmg,String s){
		super();
		materialOreDict = matOd;
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.setMaxDamage(dmg);
		this.setCreativeTab(CreativeTabs.COMBAT);
		AddonItems.ALL_ITEMS.add(this);
	}

	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		tooltip.addAll(Arrays.asList(I18nUtil.resolveKey(getTranslationKey()+".desc").split("\\$")));
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}

	public static Map<EntityPlayer,Pair<Integer,Long>> chargePrepare = new HashMap<>();
	public static Map<EntityPlayer,Pair<Integer,Long>> chargeCooldown = new HashMap<>();
	@Override
	public void onUpdate(ItemStack stack,World worldIn,Entity entityIn,int itemSlot,boolean isSelected) {
		if (worldIn.isRemote) return;
		if (entityIn instanceof EntityPlayer plr) {
			if (plr.getHeldItemMainhand() == stack || plr.getHeldItemOffhand() == stack) {
				if (this == Shields.mysticite_shield || this == Shields.fissite_shield) {
					if (plr.isActiveItemStackBlocking() && entityIn.isSneaking()) {
						int cooldown = AddonBase.getExpirable(chargeCooldown,plr,0);
						if (cooldown != 0) {
							// fah
						} else {
							int value = AddonBase.getExpirable(chargePrepare,plr,0)+1;
							if (value < 20)
								AddonBase.addExpirable(chargePrepare,plr,value);
							else {
								chargePrepare.remove(plr);
								AddonBase.addExpirable(chargeCooldown,plr,60);
								plr.getCooldownTracker().setCooldown(this,60);
								LeafiaCustomPacket.__start(new SyncVelocityPacket(getVectorForRotation(0,plr.rotationYaw).scale(1.75).add(0,0.4,0))).__sendToClient(plr);
							}
						}
					} else
						chargePrepare.remove(plr);
				}
			}
		}
	}
	Vec3d getVectorForRotation(float pitch,float yaw)
	{
		float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
	}

	public double calBulletDamage(float dmg) {
		return Math.sqrt(dmg);
	}

	public boolean canBlockBullets() {
		return this == Shields.mysticite_shield || this == Shields.elec_shield;
	}

	public boolean handleImpact(ItemStack stack,Entity attacker,float amount) {
		if (canBlockBullets()) {
			if (attacker instanceof AMRifleBeam) return false;
			if (attacker instanceof EntityBulletBase || attacker instanceof EntityBulletBaseMK4 || attacker instanceof EntityBulletBaseNT || attacker instanceof EntityBulletBaseMK4CL || attacker instanceof EntityBulletBeamBase) {
				stack.setItemDamage(Math.min(stack.getMaxDamage(),stack.getItemDamage()+(int)calBulletDamage(amount)));
				return true;
			}
		}
		if (this == Shields.fissite_shield) {
			if (attacker instanceof EntityLivingBase living)
				ContaminationUtil.contaminate(living,HazardType.RADIATION,ContaminationType.CREATIVE,70);
		}
		if (this == Shields.pu238_shield) {
			attacker.setFire(5);
			if (attacker instanceof EntityLivingBase living)
				ContaminationUtil.contaminate(living,HazardType.RADIATION,ContaminationType.CREATIVE,10);
		}
		return false;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair,ItemStack repair) {
		for (int id : OreDictionary.getOreIDs(repair)) {
			if (OreDictionary.getOreName(id).equals(materialOreDict))
				return true;
		}
		return super.getIsRepairable(toRepair,repair);
	}

	@Override
	public @NotNull String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getTranslationKey(stack) + ".name").trim();
	}

	@Override
	public boolean isShield(ItemStack stack,@Nullable EntityLivingBase entity) {
		return !stack.isEmpty() && stack.getItem() instanceof AddonShieldItem;
	}
}
