package com.leafia.contents.machines.reactors.apr.blocks.core.container;

import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import com.leafia.dev.gui.GuiScreenLeafia;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.render.NTMRenderHelper.bindTexture;
import static com.leafia.AddonBase.getIntegrated;

public class APRMBUI extends GuiScreenLeafia {
	public final APRCoreTE te;
	public static final ResourceLocation tex = getIntegrated("machines/reactors/apr/gui_assembly.png");
	public List<Integer> chambers = new ArrayList<>();
	public APRMBUI(APRCoreTE te) {
		this.te = te;
		xSize = 195;
		ySize = 130;
		chambers.addAll(te.chambers);
	}
	@Override
	protected void drawGuiScreenBackgroundLayer(float partialTicks,int mouseX,int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1,1,1,1);
		bindTexture(tex);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
	}
}
