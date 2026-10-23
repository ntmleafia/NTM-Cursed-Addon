// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.ControlPanel;
import com.hbm.main.ClientProxy;
import com.hbm.render.tileentity.RenderControlPanel;
import com.hbm.tileentity.machine.TileEntityControlPanel;
import com.leafia.contents.machines.controlpanel.instruments.types.nixie.NixieDisplay;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderControlPanel.class, remap = false)
public abstract class MixinRrenderControlPanel {
    @Inject(method = "render(Lcom/hbm/tileentity/machine/TileEntityControlPanel;DDDFIF)V",
            at = @At("HEAD"), require = 1)
    private void leafia$renderInset(TileEntityControlPanel tile, double x, double y, double z,
                                   float partialTicks, int destroyStage, float alpha, CallbackInfo ci) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        var matrix = ClientProxy.AUX_GL_BUFFER;
        matrix.clear();
        tile.panel.transform.store(matrix);
        matrix.flip();
        GlStateManager.multMatrix(matrix);
        tile.updateTransform();
        leafia$renderControls(tile.panel, true);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Redirect(method = "render(Lcom/hbm/tileentity/machine/TileEntityControlPanel;DDDFIF)V",
            at = @At(value = "INVOKE", target = "Lcom/hbm/inventory/control_panel/ControlPanel;render()V"), require = 1)
    private void leafia$renderOutset(ControlPanel panel) {
        leafia$renderControls(panel, false);
    }

    @Unique
    private static void leafia$renderControls(ControlPanel panel, boolean inset) {
        for (var control : panel.controls) {
            if ((control instanceof NixieDisplay) != inset) continue;
            var size = control.getSize();
            GlStateManager.pushMatrix();
            GlStateManager.translate((size[0] - 1) / 2, 0, (size[1] - 1) / 2);
            control.render();
            GlStateManager.popMatrix();
        }
    }
}
