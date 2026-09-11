package com.leafia.contents.machines.reactors.apr.blocks.core.container;

import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import static com.hbm.render.NTMRenderHelper.bindTexture;
import static com.leafia.AddonBase.getIntegrated;

public class APRMainUI extends LCEGuiInfoContainer {
	public final APRCoreTE te;
	public static final ResourceLocation tex = getIntegrated("machines/reactors/apr/gui_main.png");
	public APRMainUI(EntityPlayer player,APRCoreTE te) {
		super(new APRMainContainer(player,te));
		this.te = te;
		xSize = 211;
		ySize = 219;
	}
	@Override
	public void drawScreen(int mouseX,int mouseY,float partialTicks) {
		super.drawScreen(mouseX,mouseY,partialTicks);
		super.renderHoveredToolTip(mouseX,mouseY);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,int mouseX,int mouseY) {
		drawDefaultBackground();
		LeafiaGls.color(1,1,1);
		bindTexture(tex);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
	}
}
