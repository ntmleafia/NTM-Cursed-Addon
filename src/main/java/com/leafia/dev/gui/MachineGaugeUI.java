// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.gui;

import com.hbm.inventory.gui.GuiInfoContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Arrays;

/** Common texture, title and gauge operations for machine inventory screens. */
public abstract class MachineGaugeUI extends GuiInfoContainer {
    private final ResourceLocation texture;

    protected MachineGaugeUI(Container container, ResourceLocation texture, int height) {
        super(container);
        this.texture = texture;
        xSize = 176;
        ySize = height;
    }

    protected abstract String inventoryTitle();
    protected abstract void drawMachine();
    protected abstract void drawMachineTooltips(int mouseX, int mouseY);

    protected final String title(String name, boolean customName) {
        return customName ? name : I18n.format(name);
    }

    @Override
    protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        var name = inventoryTitle();
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040);
    }

    @Override
    protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawMachine();
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawMachineTooltips(mouseX, mouseY);
        renderHoveredToolTip(mouseX, mouseY);
    }

    protected final void gauge(int x, int bottom, int textureX, int textureHeight, int width, int filled) {
        drawTexturedModalRect(guiLeft + x, guiTop + bottom - filled, textureX, textureHeight - filled, width, filled);
    }

    protected final boolean hovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        int dx = mouseX - guiLeft - x;
        int dy = mouseY - guiTop - y;
        return dx >= 0 && dx < width && dy > 0 && dy <= height;
    }

    protected final void metric(int mouseX, int mouseY, int x, int y, int width, int height,
                                String label, int value, int maximum, boolean percent) {
        if (hovered(mouseX, mouseY, x, y, width, height)) {
            drawHoveringText(Arrays.asList(label, percent ? value + "%" : value + "/" + maximum), mouseX, mouseY);
        }
    }
}
