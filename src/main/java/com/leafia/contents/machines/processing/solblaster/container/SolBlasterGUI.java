package com.leafia.contents.machines.processing.solblaster.container;

import com.hbm.items.ModItems;
import com.leafia.contents.machines.processing.solblaster.SolBlasterTE;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import static com.leafia.AddonBase.getIntegrated;

public class SolBlasterGUI extends LCEGuiInfoContainer {
	final SolBlasterTE te;
	public static final ResourceLocation rsc = getIntegrated("machines/crafting/solblaster/gui.png");
	public SolBlasterGUI(EntityPlayer player,SolBlasterTE te) {
		super(new SolBlasterContainer(player,te));
		xSize = 176;
		ySize = 227;
		this.te = te;
	}
	@Override
	public void drawScreen(int mouseX,int mouseY,float partialTicks) {
		super.drawScreen(mouseX,mouseY,partialTicks);
		super.renderHoveredToolTip(mouseX,mouseY);
	}
	public boolean checkForItem(Item item,int slot) {
		return te.inventory.getStackInSlot(slot).getItem() == item;
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.te.hasCustomName() ? this.te.getName() : I18n.format(this.te.getName(), new Object[0]);
		this.fontRenderer.drawString(name, 124 - this.fontRenderer.getStringWidth(name) / 2, 47, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,int mouseX,int mouseY) {
		drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(rsc);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		boolean core = checkForItem(ModItems.solinium_core,0);
		boolean[] lens = new boolean[4];
		for (int i = 0; i < 4; i++) {
			lens[i] = checkForItem(ModItems.early_explosive_lenses,i+1) || checkForItem(ModItems.explosive_lenses,i+1);
			int x = i%2*23;
			int y = i/2*23;
			if (lens[i])
				drawTexturedModalRect(guiLeft+8+x,guiTop+8+y,176+x,y,23,23);
		}
		if (core && lens[0] && lens[1] && lens[2] && lens[3])
			drawTexturedModalRect(guiLeft+53,guiTop+86,176,46,16,16);
	}
}
