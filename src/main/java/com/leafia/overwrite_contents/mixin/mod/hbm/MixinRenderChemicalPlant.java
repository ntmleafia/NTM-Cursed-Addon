// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.ResourceManager;
import com.hbm.render.tileentity.RenderChemicalPlant;
import com.hbm.tileentity.machine.TileEntityMachineChemicalPlant;
import com.leafia.dev.firestorm.IFirestormTE;
import com.leafia.init.ResourceInit.FirestormAssets;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderChemicalPlant.class)
public abstract class MixinRenderChemicalPlant extends TileEntitySpecialRenderer<TileEntityMachineChemicalPlant> {
    @Inject(method = "render(Lcom/hbm/tileentity/machine/TileEntityMachineChemicalPlant;DDDFIF)V",
            at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void leafia$renderDestroyed(TileEntityMachineChemicalPlant plant, double x, double y, double z,
                                       float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        if (!((IFirestormTE) plant).isDestroyed()) return;
        int quarterTurns = switch (plant.getBlockMetadata() - BlockDummyable.offset) {
            case 4 -> 1;
            case 3 -> 2;
            case 5 -> 3;
            default -> 0;
        };
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        GlStateManager.rotate(90F, 0F, 1F, 0F);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.rotate(quarterTurns * 90F, 0F, 1F, 0F);
        bindTexture(ResourceManager.chemical_plant_tex);
        LeafiaGls.disableCull();
        FirestormAssets.chem_destroyed.renderAll();
        LeafiaGls.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();
        ci.cancel();
    }
}
