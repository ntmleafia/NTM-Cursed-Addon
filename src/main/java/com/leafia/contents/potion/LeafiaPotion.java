package com.leafia.contents.potion;

import com.leafia.contents.gear.advisor.AdvisorItem;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorWarningPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.LeafiaParticlePacket.Sweat;
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LeafiaPotion extends Potion {
	public static LeafiaPotion skindamage;
	public static LeafiaPotion frigid;

	public LeafiaPotion(boolean isBad,int color,String name,int x,int y){
		super(isBad, color);
		this.setPotionName(name);
		this.setRegistryName("leafia", name);
		this.setIconIndex(x, y);
	}

	/**
	 * Add Damaged Skin status effect to an entity. Starting from Damaged Skin I,
	 * multiple calls will increase its amplifier until it hits the specified maximum.
	 * @param entity The entity to be affected
	 * @param maxLevel A number ranging from 1 to 3
	 */
	public static void hurtSkin(EntityLivingBase entity,int maxLevel) {
		maxLevel -= 1;
		int level = 0;
		PotionEffect effect = entity.getActivePotionEffect(skindamage);
		if (effect != null) {
			if (effect.getAmplifier() > maxLevel) return;
			level = Math.min(effect.getAmplifier() + 1,maxLevel);
		}
		entity.addPotionEffect(new PotionEffect(skindamage,450*20,level,false,false));
		if (entity instanceof EntityPlayer player) {
			if (level > 0)
				LeafiaCustomPacket.__start(new AdvisorWarningPacket(level)).__sendToClient(player);
		}
	}

	/**
	 * Retrieves the amplifier of Damaged Skin status effect added by 1,
	 * or 0 if entity does not have the effect.
	 * @param entity Target entity
	 * @return A number ranging from 0 to 3
	 */
	public static int getSkinDamage(EntityLivingBase entity) {
		PotionEffect effect = entity.getActivePotionEffect(skindamage);
		if (effect != null)
			return effect.getAmplifier()+1;
		return 0;
	}

	public static void init() {
		skindamage = registerPotion(true, 0xe11313, "skindamage", 4, 1);
		frigid = registerPotion(true, /*0x65d3ff*/0xFFFFFF, "frigid", 0, 2);
	}

	public static LeafiaPotion registerPotion(boolean isBad, int color, String name, int x, int y) {

		LeafiaPotion effect = new LeafiaPotion(isBad, color, name, x, y);
		ForgeRegistries.POTIONS.register(effect);
		
		return effect;
	}

	@Override
	public String getName() {
		if (this == frigid)
			return MobEffects.SLOWNESS.getName();
		return "potion."+super.getName();
	}

	PotionEffect lastEffect = null;
	@Override
	@SideOnly(Side.CLIENT)
	public int getStatusIconIndex() {
		ResourceLocation loc = new ResourceLocation( "leafia","textures/gui/potions.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(loc);
		if (this == skindamage) {
			if (lastEffect != null) {
				if (lastEffect.getPotion() == this)
					return 8+5+Math.min(lastEffect.getAmplifier(),2);
			}
		}
		return super.getStatusIconIndex();
	}
	@Override
	public boolean shouldRender(PotionEffect effect) {
		lastEffect = effect;
		return super.shouldRender(effect);
	}
	@Override
	public boolean shouldRenderHUD(PotionEffect effect) {
		return this.shouldRender(effect);
	}
	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entity,AbstractAttributeMap attr,int amplifier) {
		super.removeAttributesModifiersFromEntity(entity,attr,amplifier);
		if (this == skindamage && !entity.world.isRemote && amplifier > 0)
			LeafiaPassiveServer.queueFunction(()->{
				if (entity.isEntityAlive())
					entity.addPotionEffect(new PotionEffect(skindamage,300*20,amplifier-1,false,false));
			});
	}

	public void performEffect(EntityLivingBase entity,int level) {
		if (this == skindamage && !entity.world.isRemote) {
			if (level > 2) level = 2; // foolproof
			if (entity.getRNG().nextInt(50-level*20) == 0) {
				Sweat particle = new Sweat(entity,Blocks.REDSTONE_BLOCK.getDefaultState(),1);
				particle.emit(new Vec3d(entity.posX,entity.posY,entity.posZ),Vec3d.ZERO,entity.dimension);
			}
		}
	}

	public boolean isReady(int par1, int par2) {

		if (this == skindamage)
			return true;

		return false;
	}
}
