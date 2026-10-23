// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.base.container;

import com.leafia.AddonBase;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import com.leafia.dev.gui.MachineGaugeUI;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.GlStateManager;

public class AMSBaseUI extends MachineGaugeUI {
    private static final ResourceLocation TEXTURE = AddonBase.getIntegrated("machines/ams/gui_base.png");
    private static final String[] RESONATOR_ERROR = {
            "Error: Three satellite ID-chips linked", "to xenium resonators are required", "for this machine to work!"
    };
    private final AMSBaseTE base;

    public AMSBaseUI(InventoryPlayer player, AMSBaseTE base) {
        super(new AMSBaseContainer(player, base), TEXTURE, 222);
        this.base = base;
    }

    @Override
    protected String inventoryTitle() {
        return title(base.getInventoryName(), base.hasCustomInventoryName());
    }

    @Override
    protected void drawMachine() {
        gauge(116, 124, 206, 106, 7, (int) base.getPowerScaled(106));
        gauge(44, 124, 192, 106, 7, base.getFieldScaled(106));
        gauge(53, 124, 199, 106, 7, base.getEfficiencyScaled(106));
        gauge(125, 124, 213, 106, 7, base.getHeatScaled(106));
        if (base.mode > 0) {
            drawTexturedModalRect(guiLeft + 89, guiTop + 18, 176, 32 + 16 * base.mode, 16, 16);
        }
        if (base.warning > 0) {
            drawTexturedModalRect(guiLeft + 71, guiTop + 18, 176, -16 + 16 * base.warning, 16, 16);
        }
        if (base.color > -1) {
            GlStateManager.color(((base.color >> 16) & 255) / 255F, ((base.color >> 8) & 255) / 255F, (base.color & 255) / 255F, 1);
            drawTexturedModalRect(guiLeft + 61, guiTop + 44, 176, 160, 54, 54);
            GlStateManager.color(1, 1, 1, 1);
            drawTexturedModalRect(guiLeft + 61, guiTop + 44, 176, 106, 54, 54);
        }
        if (!base.hasResonators()) {
            drawInfoPanel(guiLeft - 16, guiTop + 36, 16, 16, 6);
        }
        for (int tank = 0; tank < 4; tank++) {
            base.tanks[tank].renderTank(guiLeft + ((tank & 1) == 0 ? 26 : 134),
                    guiTop + (tank < 2 ? 70 : 124), zLevel, 16, 52);
        }
    }

    @Override
    protected void drawMachineTooltips(int mouseX, int mouseY) {
        for (int tank = 0; tank < 4; tank++) {
            base.tanks[tank].renderTankInfo(this, mouseX, mouseY,
                    guiLeft + ((tank & 1) == 0 ? 26 : 134), guiTop + (tank < 2 ? 18 : 72), 16, 52);
        }
        drawElectricityInfo(this, mouseX, mouseY, guiLeft + 116, guiTop + 20, 7, 104, base.power, AMSBaseTE.maxPower);
        metric(mouseX, mouseY, 44, 18, 7, 106, "Restriction Field:", base.field, 0, true);
        metric(mouseX, mouseY, 53, 18, 7, 106, "Efficiency:", base.efficiency, 0, true);
        metric(mouseX, mouseY, 125, 18, 7, 106, "Heat:", base.heat, AMSBaseTE.maxHeat, false);
        if (!base.hasResonators()) {
            drawCustomInfoStat(mouseX, mouseY, guiLeft - 16, guiTop + 36, 16, 16,
                    guiLeft - 8, guiTop + 52, RESONATOR_ERROR);
        }
    }
}
