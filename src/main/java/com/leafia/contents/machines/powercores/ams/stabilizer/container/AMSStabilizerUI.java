// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.stabilizer.container;

import com.leafia.AddonBase;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerTE;
import com.leafia.dev.gui.MachineGaugeUI;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class AMSStabilizerUI extends MachineGaugeUI {
    private static final ResourceLocation TEXTURE = AddonBase.getIntegrated("machines/ams/gui_stabilizer.png");
    private final AMSStabilizerTE machine;

    public AMSStabilizerUI(InventoryPlayer player, AMSStabilizerTE machine) {
        super(new AMSStabilizerContainer(player, machine), TEXTURE, 166);
        this.machine = machine;
    }

    @Override
    protected String inventoryTitle() {
        return title(machine.getInventoryName(), machine.hasCustomInventoryName());
    }

    @Override
    protected void drawMachine() {
        gauge(134, 69, 192, 52, 16, (int) machine.getPowerScaled(52));
        gauge(152, 69, 208, 52, 16, machine.getEfficiencyScaled(52));
        gauge(8, 69, 176, 52, 16, machine.getHeatScaled(52));
        if (machine.mode > 0) {
            drawTexturedModalRect(guiLeft + 98, guiTop + 17, 176, 84 + 16 * machine.mode, 16, 16);
        }
        if (machine.warning > 0) {
            drawTexturedModalRect(guiLeft + 80, guiTop + 17, 176, 36 + 16 * machine.warning, 16, 16);
        }
        machine.tank.renderTank(guiLeft + 26, guiTop + 69, zLevel, 16, 52);
    }

    @Override
    protected void drawMachineTooltips(int mouseX, int mouseY) {
        machine.tank.renderTankInfo(this, mouseX, mouseY, guiLeft + 26, guiTop + 17, 16, 52);
        drawElectricityInfo(this, mouseX, mouseY, guiLeft + 134, guiTop + 17, 16, 52, machine.power, AMSStabilizerTE.maxPower);
        metric(mouseX, mouseY, 152, 17, 16, 52, "Efficiency:", machine.efficiency, 0, true);
        metric(mouseX, mouseY, 8, 17, 16, 52, "Heat:", machine.heat, AMSStabilizerTE.maxHeat, false);
    }
}
