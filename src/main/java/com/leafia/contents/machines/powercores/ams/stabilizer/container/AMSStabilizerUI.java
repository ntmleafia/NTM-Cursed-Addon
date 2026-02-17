package com.leafia.contents.machines.powercores.ams.stabilizer.container;

import com.hbm.inventory.gui.GuiInfoContainer;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerTE;
import com.leafia.init.ResourceInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AMSStabilizerUI extends GuiInfoContainer {

	private static ResourceLocation texture = ResourceInit.getIntegrated("ams/gui_stabilizer.png");
	private AMSStabilizerTE limiter;

	public AMSStabilizerUI(InventoryPlayer invPlayer,AMSStabilizerTE tedf) {
		super(new AMSStabilizerContainer(invPlayer, tedf));
		limiter = tedf;
		
		this.xSize = 176;
		this.ySize = 166;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);

		limiter.tank.renderTankInfo(this, mouseX, mouseY, guiLeft + 26, guiTop + 69 - 52, 16, 52);
		this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 134, guiTop + 69 - 52, 16, 52, limiter.power, AMSStabilizerTE.maxPower);
		this.drawCustomInfo(mouseX, mouseY, guiLeft + 152, guiTop + 69 - 52, 16, 52, new String[] { "Efficiency:", limiter.efficiency + "%" });
		this.drawCustomInfo(mouseX, mouseY, guiLeft + 8, guiTop + 69 - 52, 16, 52, new String[] { "Heat:", limiter.heat + "/" + AMSStabilizerTE.maxHeat });
		super.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer( int i, int j) {
		String name = this.limiter.hasCustomInventoryName() ? this.limiter.getInventoryName() : I18n.format(this.limiter.getInventoryName());
		
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		int i = (int) limiter.getPowerScaled(52);
		drawTexturedModalRect(guiLeft + 134, guiTop + 69 - i, 192, 52 - i, 16, i);
		
		int j = limiter.getEfficiencyScaled(52);
		drawTexturedModalRect(guiLeft + 152, guiTop + 69 - j, 208, 52 - j, 16, j);
		
		int k = limiter.getHeatScaled(52);
		drawTexturedModalRect(guiLeft + 8, guiTop + 69 - k, 176, 52 - k, 16, k);
		
		int l = limiter.mode;
		if(l > 0)
			drawTexturedModalRect(guiLeft + 98, guiTop + 17, 176, 84 + 16 * l, 16, 16);
		
		int m = limiter.warning;
		if(m > 0)
			drawTexturedModalRect(guiLeft + 80, guiTop + 17, 176, 36 + 16 * m, 16, 16);
		
		limiter.tank.renderTank(guiLeft+26, guiTop+97-28, zLevel, 16, 52);
	}
}