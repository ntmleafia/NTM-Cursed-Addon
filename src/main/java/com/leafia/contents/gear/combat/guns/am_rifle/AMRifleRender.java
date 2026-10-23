// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.combat.guns.am_rifle;

import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.items.weapon.sedna.mods.XWeaponModManager;
import com.hbm.main.ResourceManager;
import com.hbm.render.anim.sedna.HbmAnimationsSedna;
import com.hbm.render.item.weapon.sedna.ItemRenderWeaponBase;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
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
        if (ItemGunBaseNT.getIsAiming(stack)) return 2.5F;
        return -0.25F;
    }

    @Override
    public float getViewFOV(ItemStack stack, float fov) {
        var progress = ItemGunBaseNT.prevAimingProgress
                + (ItemGunBaseNT.aimingProgress - ItemGunBaseNT.prevAimingProgress) * interp;
        AMRifle.isADS = progress == 1;
        return fov * (1 - progress * (hasScope(stack) ? 7 / 8F : 0.2F));
    }

    @Override
    public void setupFirstPerson(ItemStack stack) {
        LeafiaGls.translate(0, 0, 0.875);
        var scoped = hasScope(stack);
        standardAimingTransform(stack, -1.5F * 0.8F, -1.5F * 0.8F, 2.5F * 0.8F,
                0, scoped ? -8.375 / 8D : -7.25 / 8D, scoped ? 1.2 : 1);
    }

    @Override
    public void renderFirstPerson(ItemStack stack) {
        var scoped = hasScope(stack);
        if (scoped && ItemGunBaseNT.prevAimingProgress == 1 && ItemGunBaseNT.aimingProgress == 1) return;
        var gun = (ItemGunBaseNT) stack.getItem();
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
        LeafiaGls.scale(0.3125D, 0.3125D, 0.3125D);
        var equip = HbmAnimationsSedna.getRelevantTransformation("EQUIP");
        var recoil = HbmAnimationsSedna.getRelevantTransformation("RECOIL");
        var charge = HbmAnimationsSedna.getRelevantTransformation("CHARGE");
        LeafiaGls.translate(0, -1, -6);
        LeafiaGls.rotate((float) equip[0], 1, 0, 0);
        LeafiaGls.translate(0, 1, 6);
        LeafiaGls.translate(0, 0, recoil[2]);
        LeafiaGls.shadeModel(GL11.GL_SMOOTH);
        renderAssembly(stack, scoped, hasRefractor(stack), charge[2], false);
        if (System.currentTimeMillis() >= gun.lastShot[0] + 550) {
            LeafiaGls.pushMatrix();
            LeafiaGls.translate(0, 1.5, 12);
            LeafiaGls.rotate(90, 0, 1, 0);
            renderLaserFlash(gun.lastShot[0], 150, 1.5D, 0xff0000);
            LeafiaGls.translate(0, 0, -0.25);
            renderLaserFlash(gun.lastShot[0], 150, 0.75D, 0xff8000);
            LeafiaGls.popMatrix();
        }
        LeafiaGls.shadeModel(GL11.GL_FLAT);
    }

    @Override
    public void setupThirdPerson(ItemStack stack) {
        super.setupThirdPerson(stack);
        LeafiaGls.scale(1.25D, 1.25D, 1.25D);
        LeafiaGls.translate(0, 0, 4);
    }

    @Override
    public void setupInv(ItemStack stack) {
        super.setupInv(stack);
        LeafiaGls.scale(1.03125D, 1.03125D, 1.03125D);
        LeafiaGls.rotate(25, 1, 0, 0);
        LeafiaGls.rotate(45, 0, 1, 0);
        LeafiaGls.translate(0.75, 0, 0);
    }

    @Override
    public void renderOther(ItemStack stack, Object type) {
        LeafiaGls.enableLighting();
        renderAssembly(stack, hasScope(stack), hasRefractor(stack), 0, true);
        LeafiaGls.shadeModel(GL11.GL_FLAT);
    }

    private void renderAssembly(ItemStack stack, boolean scoped, boolean refractor,
                                double charge, boolean bindBody) {
        LeafiaGls.translate(0, 0.5, -1.5);
        if (bindBody) {
            LeafiaGls.shadeModel(GL11.GL_SMOOTH);
            Minecraft.getMinecraft().renderEngine.bindTexture(tex);
        }
        mdl.renderPart("Gun");
        LeafiaGls.pushMatrix();
        var angle = 0D;
        if (stack.hasTagCompound()) {
            var data = stack.getTagCompound();
            var previous = data.getDouble("leafia_prev_rot");
            angle = previous + (data.getDouble("leafia_rot") - previous) * interp;
        }
        LeafiaGls.rotate((float) angle, 0, 0, 1);
        mdl.renderPart("Fan");
        LeafiaGls.popMatrix();
        if (scoped) {
            Minecraft.getMinecraft().renderEngine.bindTexture(ResourceManager.lilmac_scope_tex);
            LeafiaGls.pushMatrix();
            LeafiaGls.translate(0, -0.85, -1.25);
            LeafiaGls.rotate(90, 0, 1, 0);
            LeafiaGls.scale(0.75);
            ResourceManager.lilmac.renderPart("Scope");
            LeafiaGls.popMatrix();
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(lasrifle);
        mdl.renderPart("Grip");
        if (!bindBody) LeafiaGls.translate(0, 0, charge);
        mdl.renderPart("BarrelBase");
        if (!bindBody) LeafiaGls.translate(0, 0, -charge);
        if (!refractor) mdl.renderPart("Barrel");
        LeafiaGls.translate(0, -0.5, 1.5);
        Minecraft.getMinecraft().renderEngine.bindTexture(mods);
        if (refractor) ResourceManager.lasrifle_mods.renderPart("BarrelShotgun");
    }

    public boolean hasScope(ItemStack stack) {
        return XWeaponModManager.hasUpgrade(stack, 0, XWeaponModManager.ID_SCOPE);
    }

    public boolean hasRefractor(ItemStack stack) {
        return XWeaponModManager.hasUpgrade(stack, 0, XWeaponModManager.ID_LAS_SHOTGUN);
    }
}
