// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.emitter.container;

import com.leafia.AddonBase;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterTE;
import com.leafia.dev.gui.MachineGaugeUI;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class AMSEmitterUI extends MachineGaugeUI {
    private static final ResourceLocation TEXTURE = AddonBase.getIntegrated("machines/ams/gui_emitter.png");
    private final AMSEmitterTE machine;

    public AMSEmitterUI(InventoryPlayer player, AMSEmitterTE machine) {
        super(new AMSEmitterContainer(player, machine), TEXTURE, 166);
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
        if (machine.warning > 0) {
            drawTexturedModalRect(guiLeft + 80, guiTop + 17, 176, 36 + 16 * machine.warning, 16, 16);
        }
        machine.tank.renderTank(guiLeft + 26, guiTop + 69, zLevel, 16, 52);
    }

    @Override
    protected void drawMachineTooltips(int mouseX, int mouseY) {
        machine.tank.renderTankInfo(this, mouseX, mouseY, guiLeft + 26, guiTop + 17, 16, 52);
        drawElectricityInfo(this, mouseX, mouseY, guiLeft + 134, guiTop + 17, 16, 52, machine.power, AMSEmitterTE.maxPower);
        metric(mouseX, mouseY, 152, 17, 16, 52, "Power:", machine.efficiency, 0, true);
        metric(mouseX, mouseY, 8, 17, 16, 52, "Heat:", machine.heat, AMSEmitterTE.maxHeat, false);
    }
}
