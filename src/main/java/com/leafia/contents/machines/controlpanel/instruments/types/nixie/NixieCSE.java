// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.nixie;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public class NixieCSE extends SubElementBaseConfig {

    private static final int[] TRANSFORM = {132, 112, 88, 88};
    private int length;

    GuiSlider slide_length;

    public NixieCSE(GuiControlEdit gui,Map<String, DataValue> map) {
        super(gui,map);
        this.length = (int)map.get("length").getNumber();
    }

    @Override
    public void fillConfigs(Map<String, DataValue> configs) {
        putFloatConfig(configs, "length",length);
    }

    @Override
    public void initGui() {
        int cX = gui.width/2;
        int cY = gui.height/2;
        slide_length = gui.addButton(new GuiSlider(gui.currentButtonId(), cX+10, gui.getGuiTop()+70, 75, 15, "Digits ", "", 0, 8,length, false, true));
        super.initGui();
    }

    @Override
    public void drawScreen() {
        GlStateManager.disableLighting();
        gui.mc.getTextureManager().bindTexture(ResourceManager.white);
        GlStateManager.enableLighting();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        length = slide_length.getValueInt();
    }

    @Override
    public void enableButtons(boolean enable) {
        slide_length.visible = enable;
        slide_length.enabled = enable;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int[] getPreviewTransform() {
        return TRANSFORM;
    }
}
