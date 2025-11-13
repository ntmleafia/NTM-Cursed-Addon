package com.leafia.contents.machines.powercores.dfc.core;

import com.hbm.inventory.container.ContainerCore;
import com.hbm.inventory.gui.GuiInfoContainer;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.transformer.LeafiaGls;
import com.llib.math.SIPfx;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class CoreGUI extends GuiInfoContainer {

	private static ResourceLocation texture = new ResourceLocation("leafia:textures/gui/dfc/gui_core.png");
	private TileEntityCore core;
	
	public CoreGUI(InventoryPlayer invPlayer,TileEntityCore tedf) {
		super(new ContainerCore(invPlayer, tedf));
		core = tedf;
		
		this.xSize = 176;
		this.ySize = 204;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);

		IMixinTileEntityCore mixin = (IMixinTileEntityCore)core; 
		
		this.core.tanks[0].renderTankInfo(this, mouseX, mouseY, this.guiLeft + 8, this.guiTop + 7, 16, 80);
		this.core.tanks[1].renderTankInfo(this, mouseX, mouseY, this.guiLeft + 152, this.guiTop + 7, 16, 80);
/*
		String[] heat = new String[] { "Heat Saturation: " + core.heat + "%" };
		String[] field = new String[] { "Restriction Field: " + core.field + "%" };
		this.drawCustomInfoStat(mouseX, mouseY, guiLeft + 53, guiTop + 97, 70, 4, mouseX, mouseY, heat);
		this.drawCustomInfoStat(mouseX, mouseY, guiLeft + 53, guiTop + 101, 70, 4, mouseX, mouseY, field);*/

		this.drawCustomInfoStat(mouseX, mouseY, guiLeft + 7, guiTop + 98, 70, 4, mouseX, mouseY,
				new String[]{"Temperature: "+((mixin.getDFCTemperature() >= IMixinTileEntityCore.failsafeLevel) ? "ERROR" : String.format("%01.1f",mixin.getDFCTemperature())+"°C"+"§8 / "+mixin.getDFCMeltingPoint())});
		this.drawCustomInfoStat(mouseX, mouseY, guiLeft + 7, guiTop + 102, 70, 4, mouseX, mouseY,
				new String[]{"Stabilization: "+Math.round(mixin.getDFCStabilization()*100)+"%"});
		this.drawCustomInfoStat(mouseX, mouseY, guiLeft + 84, guiTop + 98, 70, 4, mouseX, mouseY,
				new String[]{"Contained Energy: "+((mixin.getDFCContainedEnergy() >= IMixinTileEntityCore.failsafeLevel) ? "ERROR" : SIPfx.formatNoSpace("%01.3f",mixin.getDFCContainedEnergy()*1000,false)+"SPK")});
		this.drawCustomInfoStat(mouseX, mouseY, guiLeft + 84, guiTop + 102, 70, 4, mouseX, mouseY,
				new String[]{"Expelling Energy: "+String.format("%01.3f",mixin.getDFCExpellingEnergy()/1000)+"MSPK/s"});
		this.drawCustomInfoStat(mouseX, mouseY, guiLeft + 161, guiTop + 98, 8, 8, mouseX, mouseY,
				new String[]{"Potential: "+Math.round(mixin.getDFCPotentialGain()*100)+"%"});

		super.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer( int i, int j) {
		String name = this.core.hasCustomName() ? this.core.getName() : I18n.format(this.core.getName(), new Object[0]).trim();
		this.fontRenderer.drawString(name, this.xSize - 8 - this.fontRenderer.getStringWidth(name), this.ySize - 96 + 2, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawDefaultBackground();
		IMixinTileEntityCore mixin = (IMixinTileEntityCore)core;
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		/*
		int i = core.getHeatScaled(70);
		if(i > 70)
			i = 70;
		drawTexturedModalRect(guiLeft + 53, guiTop + 98, 0, 204, i, 4);
		
		int j = core.getFieldScaled(70);
		if(j > 70)
			j = 70;
		drawTexturedModalRect(guiLeft + 53, guiTop + 102, 0, 208, j, 4);
		 */
		if (mixin.getDFCTemperature() < mixin.getDFCMeltingPoint())
			drawTexturedModalRect(guiLeft + 7, guiTop + 98, 0, 204,
					(int)MathHelper.clampedLerp(0,70,mixin.getDFCTemperature()/mixin.getDFCMeltingPoint()), 4);
		else
			drawTexturedModalRect(guiLeft+7,guiTop+98,0,224+4*Math.floorDiv(Math.floorMod(mixin.getDFCTicks(),8),4),70,4);
		drawTexturedModalRect(guiLeft + 7, guiTop + 102, 0, 208,
				(int)MathHelper.clampedLerp(0,70,(IMixinTileEntityCore.getStabilizationDiv(mixin.getDFCStabilization())-1)/10), 4);
		drawTexturedModalRect(guiLeft + 84, guiTop + 98, 0, 216,
				(int)MathHelper.clampedLerp(0,70,mixin.getDFCContainedEnergy()/100_000), 4); // 1MSPK ~ 1PSPK (= 5EHE)
		drawTexturedModalRect(guiLeft + 84, guiTop + 102, 0, 220,
				(int)MathHelper.clampedLerp(0,70,mixin.getDFCExpellingEnergy()/(100_000)), 4);
		LeafiaGls.inLocalSpace(()->{
			LeafiaGls.translate(guiLeft+165,guiTop+102,0);
			LeafiaGls.scale(2/5f);
			LeafiaGls.rotate((float)Math.min((mixin.getDFCPotentialGain()-1)/9,mixin.getDFCClientMaxDial())*360,0,0,1);
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(-2.5,-9.5,0);
			drawTexturedModalRect(0,0,176,0,5,12);
			LeafiaGls.popMatrix();
		});

		if(mixin.isDFCHasCore())
			drawTexturedModalRect(guiLeft + 70, guiTop + 29, 220, 0, 36, 36);

		this.core.tanks[0].renderTank(this.guiLeft + 8, this.guiTop + 88, (double)this.zLevel, 16, 80);
		this.core.tanks[1].renderTank(this.guiLeft + 152, this.guiTop + 88, (double)this.zLevel, 16, 80);
	}
}