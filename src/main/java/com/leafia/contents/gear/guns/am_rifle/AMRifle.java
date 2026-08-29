package com.leafia.contents.gear.guns.am_rifle;

import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import com.hbm.render.anim.sedna.AnimationEnums;
import com.hbm.render.anim.sedna.AnimationEnums.GunAnimation;
import com.hbm.render.anim.sedna.BusAnimationKeyframeSedna.IType;
import com.hbm.render.anim.sedna.BusAnimationSedna;
import com.hbm.render.anim.sedna.BusAnimationSequenceSedna;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.passive.EntityAttachedSounds;
import com.leafia.passive.EntityAttachedSounds.AttachedSoundPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

import static com.hbm.items.weapon.sedna.factory.Lego.spawnBullet;
import static com.hbm.items.weapon.sedna.factory.XFactoryEnergy.energy_las;
import static com.leafia.contents.gear.guns.GunInit.am_beam;

public class AMRifle extends ItemGunBaseNT {
	public AMRifle(WeaponQuality quality,String s,GunConfig... cfg) {
		super(quality,s,cfg);
	}
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation,BusAnimationSedna> LAMBDA_AMRIFLE = (stack,type) -> {
		int amount = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, MainRegistry.proxy.me().inventory);
		return switch (type) {
			case EQUIP -> new BusAnimationSedna()
					.addBus("EQUIP", new BusAnimationSequenceSedna().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
			case CYCLE -> new BusAnimationSedna()
					.addBus("RECOIL", new BusAnimationSequenceSedna().addPos(0,0,0,550).addPos(0, 0, -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_FULL))
					.addBus("CYCLE", new BusAnimationSequenceSedna().addPos(0,0,0,550).addPos(0, 0, 0, 150).addPos(0, 0, 22.5, 350))
					.addBus("COUNT", new BusAnimationSequenceSedna().addPos(0,0,0,550).addPos(amount, 0, 0, 0))
					.addBus("CHARGE", new BusAnimationSequenceSedna().addPos(0,0,-0.5,550,IType.SIN_UP).addPos(0,0,0,30,IType.SIN_DOWN));
			default -> null;
		};

	};
	public static void fire(ItemStack stack, LambdaContext ctx) {
		EntityLivingBase entity = ctx.entity;
		EntityPlayer player = ctx.getPlayer();
		int index = ctx.configIndex;
		boolean aim = ItemGunBaseNT.getIsAiming(stack);
		Receiver primary = ctx.config.getReceivers(stack)[0];
		IMagazine mag = primary.getMagazine(stack);
		BulletConfig config = am_beam;

		Vec3d offset = ItemGunBaseNT.getIsAiming(stack) ? primary.getProjectileOffsetScoped(stack) : primary.getProjectileOffset(stack);
		double forwardOffset = offset.x;
		double heightOffset = offset.y;
		double sideOffset = offset.z;
		World world = entity.world;
		getNBT(stack).setByte("leafia_state",(byte)2);
		getNBT(stack).setInteger("leafia_timer",0);

		spawnBullet(world, () ->{
			AMRifleBeam mk4 = new AMRifleBeam(entity,null,config,200,sideOffset,heightOffset,forwardOffset);
			world.spawnEntity(mk4);
		});
	}
	public static int fanDuration = 50;
	public static int fanDiv = 5;
	public static double fanRPS = 360;
	@Override
	public void onUpdate(@NotNull ItemStack stack,@NotNull World world,@NotNull Entity entity,int slot,boolean isHeld) {
		super.onUpdate(stack,world,entity,slot,isHeld);
		if (!world.isRemote) {
			NBTTagCompound tag = getNBT(stack);
			if (!tag.hasKey("leafia_soundId"))
				tag.setLong("leafia_soundId",world.rand.nextLong());
			long soundId = tag.getLong("leafia_soundId");
			if (tag.getByte("leafia_state") == 2) {
				if (tag.getInteger("leafia_fan") < fanDuration)
					tag.setInteger("leafia_fan",tag.getInteger("leafia_fan")+1);
				int timestamp = tag.getInteger("leafia_timer");
				if (timestamp > 200) {
					tag.removeTag("leafia_timer");
					tag.setByte("leafia_state",(byte)0);
				} else
					tag.setInteger("leafia_timer",timestamp+1);
			} else {
				if (tag.getInteger("leafia_fan") > 0) {
					tag.setInteger("leafia_fan",tag.getInteger("leafia_fan")-1);
					if (tag.getInteger("leafia_fan") == fanDuration/fanDiv)
						LeafiaCustomPacket.__start(new AttachedSoundPacket(entity,soundId)).__sendToAllInDimension(entity.dimension);
				}
			}
			if (tag.getInteger("leafia_fan") > 0) {
				double rot = tag.getDouble("leafia_rot")+fanRPS/20*(tag.getInteger("leafia_fan")/(double)fanDuration);
				double subt = 0;
				if (rot >= 360)
					subt = 360;
				tag.setDouble("leafia_prev_rot",tag.getDouble("leafia_rot")-subt);
				tag.setDouble("leafia_rot",rot-subt);
				int offset = fanDuration/fanDiv;
				float pitch = (tag.getInteger("leafia_fan")-offset)/(float)(fanDuration-offset)*1.5f+0.5f;
				if (tag.getInteger("leafia_fan") > offset) {
					LeafiaCustomPacket.__start(
							new AttachedSoundPacket(
									entity,soundId,
									LeafiaSoundEvents.fan,
									0.35f*pitch,pitch
							)
					).__sendToAllAround(entity.dimension,new Vec3d(entity.posX,entity.posY,entity.posZ),128);
				}
			}
		}
	}
	public static NBTTagCompound getNBT(ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		return stack.getTagCompound();
	}
	public static void primary(ItemStack stack,LambdaContext ctx) {
		EntityLivingBase entity = ctx.entity;
		EntityPlayer player = ctx.getPlayer();
		int index = ctx.configIndex;
		GunState state = ItemGunBaseNT.getState(stack,index);
		if (state == GunState.IDLE && getNBT(stack).getByte("leafia_state") == 0) {
			ItemGunBaseNT.playAnimation(player,stack,GunAnimation.CYCLE,ctx.configIndex);
			ItemGunBaseNT.setState(stack,index,GunState.COOLDOWN);
			ItemGunBaseNT.setTimer(stack,index,11);
			getNBT(stack).setByte("leafia_state",(byte)1);
		}
	}
	public static void orchestra(ItemStack stack,LambdaContext ctx) {
		EntityLivingBase entity = ctx.entity;
		if(entity.world.isRemote) return;
		AnimationEnums.GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		if (type == GunAnimation.EQUIP) {
			if (getNBT(stack).getByte("leafia_state") == 1)
				getNBT(stack).setByte("leafia_state",(byte)0); // anti-softlock
		}
		if (type == GunAnimation.CYCLE) {
			if (timer == 0) {
				entity.world.playSound(
						null,entity.getPosition(),
						LeafiaSoundEvents.am_rifle_charge,SoundCategory.PLAYERS,
						1,1
				);
			}
			if (timer == 11) {
				entity.world.playSound(
						null,entity.getPosition(),
						LeafiaSoundEvents.am_rifle_fire,SoundCategory.PLAYERS,
						1,1
				);
				Receiver rec = ctx.config.getReceivers(stack)[0];
				rec.getOnFire(stack).accept(stack, ctx);
			}
		}
	}
}
