// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.rbmk.columns.control_auto.container;

import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.container.ContainerRBMKControlAuto;
import com.hbm.packet.toserver.NBTControlPacket;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKControlAuto;
import com.leafia.Tags;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityRBMKControlAuto;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Arrays;

import static com.hbm.util.SoundUtil.playClickSound;

public class RBMKControlAutoGUI extends LCEGuiInfoContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MODID, "textures/gui/reactors/rbmk_control_auto_az5able.png");
    private static final String[] PARAMETER_KEYS = {"levelUpper", "levelLower", "heatUpper", "heatLower"};
    private static final String[] FUNCTIONS = {"Linear", "Quadratic", "Inverse Quadratic"};
    private static final String[][] PARAMETER_HELP = {
            {"Level at maximum heat", "Normally lower than the level at minimum heat"},
            {"Level at minimum heat", "Normally higher than the level at maximum heat"},
            {"Maximum heat", "Must exceed the minimum heat"},
            {"Minimum heat", "Must be below the maximum heat"},
            {"Save parameters"}
    };
    private final TileEntityRBMKControlAuto rod;
    private final GuiTextField[] fields = new GuiTextField[PARAMETER_KEYS.length];
    private FiaUIRect rectBulb, rectAll, rectOne;

    public RBMKControlAutoGUI(InventoryPlayer player, TileEntityRBMKControlAuto rod) {
        super(new ContainerRBMKControlAuto(player, rod));
        this.rod = rod;
        xSize = 176;
        ySize = 186;
    }

    @Override
    public void initGui() {
        super.initGui();
        rectBulb = new FiaUIRect(this, 151, 34, 10, 11);
        rectAll = new FiaUIRect(this, 151, 54, 10, 10);
        rectOne = new FiaUIRect(this, 151, 70, 10, 10);
        Keyboard.enableRepeatEvents(true);
        double[] values = {rod.levelUpper, rod.levelLower, rod.heatUpper, rod.heatLower};
        for (int index = 0; index < fields.length; index++) {
            var field = new GuiTextField(0, fontRenderer, guiLeft + 17, guiTop + 27 + 11 * index, 26, 6);
            field.setTextColor(-1);
            field.setDisabledTextColour(-1);
            field.setEnableBackgroundDrawing(false);
            field.setMaxStringLength(index < 2 ? 3 : 4);
            field.setText(Integer.toString((int) values[index]));
            fields[index] = field;
        }
    }

    private boolean inside(int mouseX, int mouseY, int x, int y, int width, int height) {
        int dx = mouseX - guiLeft - x, dy = mouseY - guiTop - y;
        return dx >= 0 && dx < width && dy > 0 && dy <= height;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (inside(mouseX, mouseY, 108, 29, 16, 56)) {
            drawHoveringText((int) (rod.level * 100) + "%", mouseX, mouseY);
        }
        if (inside(mouseX, mouseY, 45, 26, 28, 19)) {
            drawHoveringText("Function: " + FUNCTIONS[rod.function.ordinal()], mouseX, mouseY);
        }
        for (int parameter = 0; parameter < PARAMETER_HELP.length; parameter++) {
            if (inside(mouseX, mouseY, 15, 26 + 11 * parameter, 30, 10)) {
                drawHoveringText(Arrays.asList(PARAMETER_HELP[parameter]), mouseX, mouseY);
            }
        }
        for (int function = 0; function < FUNCTIONS.length; function++) {
            if (inside(mouseX, mouseY, 48, 48 + 11 * function, 22, 10)) {
                drawHoveringText("Select " + FUNCTIONS[function].toLowerCase() + " interpolation", mouseX, mouseY);
            }
        }
        var control = (IMixinTileEntityRBMKControlAuto) rod;
        if (rectBulb.isMouseIn(mouseX, mouseY)) {
            drawHoveringText(control.leafia$getScrammed() ? TextFormatting.GOLD + "Rod is shut off" : "Rod is active", mouseX, mouseY);
        }
        if (rectAll.isMouseIn(mouseX, mouseY)) drawHoveringText("Acknowledge All", mouseX, mouseY);
        if (rectOne.isMouseIn(mouseX, mouseY)) drawHoveringText("Acknowledge", mouseX, mouseY);
        renderHoveredToolTip(mouseX, mouseY);
    }

    private void send(NBTTagCompound data) {
        PacketThreading.createSendToServerThreadedPacket(new NBTControlPacket(data, rod.getPos()));
    }

    private int parameterValue(int index) {
        var text = fields[index].getText();
        if (NumberUtils.isCreatable(text)) {
            try {
                return (int) MathHelper.clamp(Double.parseDouble(text), 0, index < 2 ? 100 : 9999);
            } catch (NumberFormatException ignored) {
                // NumberUtils also accepts literals such as 0x10 that Double.parseDouble cannot read.
            }
        }
        return 0;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        for (var field : fields) field.mouseClicked(mouseX, mouseY, button);
        if (button != 0) return;
        if (rectOne.isMouseIn(mouseX, mouseY)) {
            if (((IMixinTileEntityRBMKControlAuto) rod).leafia$getScrammed()) {
                playClick(1);
                var data = new NBTTagCompound();
                data.setBoolean("acknowledge", false);
                send(data);
            } else playDenied();
        }
        if (rectAll.isMouseIn(mouseX, mouseY)) {
            playClick(1);
            var data = new NBTTagCompound();
            data.setBoolean("acknowledge", true);
            send(data);
        }
        if (inside(mouseX, mouseY, 15, 70, 30, 10)) {
            playClickSound();
            var data = new NBTTagCompound();
            for (int parameter = 0; parameter < fields.length; parameter++) {
                int value = parameterValue(parameter);
                fields[parameter].setText(Integer.toString(value));
                data.setDouble(PARAMETER_KEYS[parameter], value);
            }
            send(data);
        }
        for (int function = 0; function < FUNCTIONS.length; function++) {
            if (!inside(mouseX, mouseY, 48, 48 + 11 * function, 22, 10)) continue;
            playClickSound();
            var data = new NBTTagCompound();
            data.setInteger("function", function);
            send(data);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        var name = I18n.format(rod.getName());
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        if (((IMixinTileEntityRBMKControlAuto) rod).leafia$getScrammed() && System.currentTimeMillis() % 500 < 250) {
            drawTexturedModalRect(guiLeft + 144, guiTop + 26, 0, 186, 24, 25);
        }
        int height = (int) (56 * (1D - rod.level));
        if (height > 0) drawTexturedModalRect(guiLeft + 108, guiTop + 29, 176, 56 - height, 8, height);
        drawTexturedModalRect(guiLeft + 46, guiTop + 27, 184, rod.function.ordinal() * 19, 26, 19);
        if (rod.isPowered()) {
            drawTexturedModalRect(guiLeft + 136, guiTop + 21, 210, rod.hasPower ? 16 : 0, 16, 16);
        }
        for (var field : fields) field.drawTextBox();
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        for (var field : fields) if (field.textboxKeyTyped(character, keyCode)) return;
        super.keyTyped(character, keyCode);
    }
}
