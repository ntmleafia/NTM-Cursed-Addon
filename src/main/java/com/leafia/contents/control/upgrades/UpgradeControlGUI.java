package com.leafia.contents.control.upgrades;

import com.hbm.util.I18nUtil;
import com.leafia.contents.control.upgrades.AddonUpgradeItem.ControlUpgradeFreqPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.gui.GuiScreenLeafia;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

import static com.hbm.render.NTMRenderHelper.bindTexture;

public class UpgradeControlGUI extends GuiScreenLeafia {
	public static final ResourceLocation tex = new ResourceLocation("leafia","textures/gui/upgrade_control.png");
	protected GuiTextField frequency;
	public String defaultFreq;
	public UpgradeControlGUI(String defaultFreq) {
		xSize = 125;
		ySize = 42;
		this.defaultFreq = defaultFreq;
	}
	private static final int TEXT_COLOR = 0x00FF00;
	private static final int DISABLED_TEXT_COLOR = 0x00FF00;
	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		int oX = 4;
		int oY = 4;
		this.frequency = new GuiTextField(0, this.fontRenderer, guiLeft + 25 + oX, guiTop + 18 + oY, 90 - oX * 2, 14);
		this.frequency.setTextColor(TEXT_COLOR);
		this.frequency.setDisabledTextColour(DISABLED_TEXT_COLOR);
		this.frequency.setEnableBackgroundDrawing(false);
		this.frequency.setMaxStringLength(10);
		this.frequency.setText(defaultFreq);
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		this.drawDefaultBackground();
		this.drawGuiContainerBackgroundLayer();
		super.drawScreen(mouseX,mouseY,f);
		GlStateManager.disableLighting();
		this.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GlStateManager.enableLighting();
	}
	private void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String name = I18nUtil.resolveKey("item.upgrade_control.name");
		this.fontRenderer.drawString(name, this.guiLeft + this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, this.guiTop + 6, 0x404040);
	}
	private void drawGuiContainerBackgroundLayer() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(tex);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		this.frequency.drawTextBox();
	}
	@Override
	protected void mouseClicked(int mouseX,int mouseY,int mouseButton) throws IOException {
		super.mouseClicked(mouseX,mouseY,mouseButton);
		frequency.mouseClicked(mouseX,mouseY,mouseButton);
	}
	@Override
	protected void keyTyped(char c,int key) throws IOException {
		if(this.frequency.textboxKeyTyped(c, key))
			return;
		super.keyTyped(c, key);
	}
	@Override
	public void onGuiClosed() {
		LeafiaCustomPacket.__start(new ControlUpgradeFreqPacket(frequency.getText())).__sendToServer();
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}
}
