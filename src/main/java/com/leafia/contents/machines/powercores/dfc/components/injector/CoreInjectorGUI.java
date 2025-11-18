package com.leafia.contents.machines.powercores.dfc.components.injector;

import com.hbm.forgefluid.FFUtils;
import com.hbm.inventory.container.ContainerCoreInjector;
import com.hbm.inventory.gui.GuiInfoContainer;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityCore;
import com.hbm.tileentity.machine.TileEntityCoreInjector;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class CoreInjectorGUI extends GuiInfoContainer {

	private static ResourceLocation texture = new ResourceLocation("leafia:textures/gui/dfc/gui_injector.png");
	private TileEntityCoreInjector injector;
	
	public CoreInjectorGUI(InventoryPlayer invPlayer,TileEntityCoreInjector tedf) {
		super(new CoreInjectorContainer(invPlayer, tedf));
		injector = tedf;
		
		this.xSize = 176;
		this.ySize = 166;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);

		this.injector.tanks[0].renderTankInfo(this, mouseX, mouseY, this.guiLeft + 35, this.guiTop + 16, 16, 52);
		this.injector.tanks[1].renderTankInfo(this, mouseX, mouseY, this.guiLeft + 125, this.guiTop + 16, 16, 52);
		super.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer( int i, int j) {

		String name = this.injector.hasCustomName() ? this.injector.getName() : I18n.format(this.injector.getName(), new Object[0]);
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		IMixinTileEntityInjector mixin = (IMixinTileEntityInjector)injector;
		if (mixin.lastGetCore() != null) {
			TileEntityCore core = mixin.lastGetCore();
			IMixinTileEntityCore mixin1 = (IMixinTileEntityCore)core;
			if (mixin1.getDFCTemperature() >= 1500)
				drawTexturedModalRect(guiLeft+53,guiTop+15,176,87,70,70);
			else if (mixin1.getDFCTemperature() >= 100)
				drawTexturedModalRect(guiLeft+53,guiTop+15,176,29,70,57);
			else if (core.tanks[0].getFill() > 0 && core.tanks[1].getFill() > 0)
				drawTexturedModalRect(guiLeft+72,guiTop+36,176,0,32,28);
		}

		this.injector.tanks[0].renderTank(this.guiLeft + 35, this.guiTop + 69, (double)this.zLevel, 16, 52);
		this.injector.tanks[1].renderTank(this.guiLeft + 125, this.guiTop + 69, (double)this.zLevel, 16, 52);
	}
}
