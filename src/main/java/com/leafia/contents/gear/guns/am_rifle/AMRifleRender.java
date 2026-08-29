package com.leafia.contents.gear.guns.am_rifle;

import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.items.weapon.sedna.mods.XWeaponModManager;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import com.hbm.render.item.weapon.sedna.ItemRenderWeaponBase;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class AMRifleRender extends ItemRenderWeaponBase {
	public static final WaveFrontObjectVAO mdl = getVAO(getIntegrated("weapons/am_rifle/am_rifle.obj"));
	public static final ResourceLocation tex = getIntegrated("weapons/am_rifle/am_rifle.png");
	public static final ResourceLocation lasrifle = ResourceManager.lasrifle_tex;
	public static final ResourceLocation mods = ResourceManager.lasrifle_mods_tex;

	@Override
	protected float getTurnMagnitude(ItemStack stack) {
		return ItemGunBaseNT.getIsAiming(stack) ? 2.5F : -0.25F;
	}

	@Override
	public float getViewFOV(ItemStack stack, float fov) {
		float aimingProgress = ItemGunBaseNT.prevAimingProgress +
				(ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
		return fov * (1 - aimingProgress * (hasScope(stack) ? 7/8F : 0.2F));
	}

	@Override
	public void setupFirstPerson(ItemStack stack) {
		LeafiaGls.translate(0, 0, 0.875);

		float offset = 0.8F;
		if(hasScope(stack)) {
			standardAimingTransform(stack,
					-1.5F * offset, -1.5F * offset, 2.5F * offset,
					0, -8.375 / 8D, 0.75);
		} else {
			standardAimingTransform(stack,
					-1.5F * offset, -1.5F * offset, 2.5F * offset,
					0, -7.25 / 8D, 1);
		}
	}

	@Override
	public void renderFirstPerson(ItemStack stack) {

		if (hasScope(stack) && ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
		ItemGunBaseNT gun = (ItemGunBaseNT) stack.getItem();
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		double scale = 0.3125D;
		LeafiaGls.scale(scale,scale,scale);

		double[] equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
		double[] recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
		double[] charge = HbmAnimationsSedna.getRelevantTransformation("CHARGE");

		LeafiaGls.translate(0,-1,-6);
		LeafiaGls.rotate((float) equip[0],1,0,0);
		LeafiaGls.translate(0,1,6);

		LeafiaGls.translate(0,0,recoil[2]);

		LeafiaGls.shadeModel(GL11.GL_SMOOTH);

		LeafiaGls.translate(0,0.5,-1.5);
		mdl.renderPart("Gun");
		LeafiaGls.pushMatrix();
		NBTTagCompound tag;
		if (stack.hasTagCompound())
			tag = stack.getTagCompound();
		else
			tag = new NBTTagCompound();
		double ang = tag.getDouble("leafia_prev_rot")+(tag.getDouble("leafia_rot")-tag.getDouble("leafia_prev_rot"))*interp;
		LeafiaGls.rotate((float)ang,0,0,1);
		mdl.renderPart("Fan");
		LeafiaGls.popMatrix();
		//if(hasScope(stack)) lasrifle.renderPart("Scope");

		Minecraft.getMinecraft().renderEngine.bindTexture(lasrifle);
		mdl.renderPart("Grip");
		LeafiaGls.translate(0,0,charge[2]);
		mdl.renderPart("BarrelBase");
		LeafiaGls.translate(0,0,-charge[2]);
		if (!hasRefractor(stack)) mdl.renderPart("Barrel");

		LeafiaGls.translate(0,-0.5,1.5);
		Minecraft.getMinecraft().renderEngine.bindTexture(mods);
		if (hasRefractor(stack)) ResourceManager.lasrifle_mods.renderPart("BarrelShotgun");

		long shot = gun.lastShot[0]+550;
		if (System.currentTimeMillis() >= shot) {
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(0,1.5,12);
			LeafiaGls.rotate(90,0,1,0);
			renderLaserFlash(gun.lastShot[0],150,1.5D,0xff0000);
			LeafiaGls.translate(0,0,-0.25);
			renderLaserFlash(gun.lastShot[0],150,0.75D,0xff8000);
			LeafiaGls.popMatrix();
		}

		LeafiaGls.shadeModel(GL11.GL_FLAT);
	}

	@Override
	public void setupThirdPerson(ItemStack stack) {
		super.setupThirdPerson(stack);
		double scale = 1.25D;
		LeafiaGls.scale(scale, scale, scale);
		LeafiaGls.translate(0, 0, 4);
	}

	@Override
	public void setupInv(ItemStack stack) {
		super.setupInv(stack);
		double scale = 1.03125D;
		LeafiaGls.scale(scale, scale, scale);
		LeafiaGls.rotate(25, 1, 0, 0);
		LeafiaGls.rotate(45, 0, 1, 0);
		LeafiaGls.translate(0.75, 0, 0);
	}

	@Override
	public void renderOther(ItemStack stack, Object type) {
		LeafiaGls.enableLighting();

		LeafiaGls.translate(0,0.5,-1.5);
		Minecraft.getMinecraft().renderEngine.bindTexture(tex);
		mdl.renderPart("Gun");
		LeafiaGls.pushMatrix();
		NBTTagCompound tag;
		if (stack.hasTagCompound())
			tag = stack.getTagCompound();
		else
			tag = new NBTTagCompound();
		double ang = tag.getDouble("leafia_prev_rot")+(tag.getDouble("leafia_rot")-tag.getDouble("leafia_prev_rot"))*interp;
		LeafiaGls.rotate((float)ang,0,0,1);
		mdl.renderPart("Fan");
		LeafiaGls.popMatrix();
		//if(hasScope(stack)) lasrifle.renderPart("Scope");

		Minecraft.getMinecraft().renderEngine.bindTexture(lasrifle);
		mdl.renderPart("Grip");
		mdl.renderPart("BarrelBase");
		if (!hasRefractor(stack)) mdl.renderPart("Barrel");

		LeafiaGls.translate(0,-0.5,1.5);
		Minecraft.getMinecraft().renderEngine.bindTexture(mods);
		if (hasRefractor(stack)) ResourceManager.lasrifle_mods.renderPart("BarrelShotgun");
		LeafiaGls.shadeModel(GL11.GL_FLAT);
	}
	public boolean hasScope(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack,0,XWeaponModManager.ID_SCOPE);
	}
	public boolean hasRefractor(ItemStack stack) {
		return XWeaponModManager.hasUpgrade(stack,0,XWeaponModManager.ID_LAS_SHOTGUN);
	}
}
