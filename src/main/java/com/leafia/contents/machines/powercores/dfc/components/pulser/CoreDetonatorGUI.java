package com.leafia.contents.machines.powercores.dfc.components.pulser;

import com.hbm.util.I18nUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore;
import com.leafia.settings.AddonConfig;
import com.leafia.transformer.LeafiaGls;
import com.leafia.unsorted.BullshitInDavenportIowa;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class CoreDetonatorGUI extends LCEGuiInfoContainer {
	public static CoreDetonatorGUI openGUI = null;
	String auth = "";
	String code = "";
	boolean blocked = false;
	boolean unlocked = false;
	CoreDetonatorTE te;
	static final ResourceLocation rsc = new ResourceLocation("leafia","textures/gui/dfc/gui_pulser.png");
	public CoreDetonatorGUI(EntityPlayer player,CoreDetonatorTE te) {
		super(new CoreDetonatorContainer(player,te));
		xSize = 176;
		ySize = 166;
		this.te = te;
		openGUI = this;
	}
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		openGUI = null;
	}
	FiaUIRect[] keypad = new FiaUIRect[12];
	FiaUIRect nukeInfo;
	FiaUIRect switch1;
	FiaUIRect switch2;
	FiaUIRect charge;
	@Override
	public void initGui() {
		super.initGui();
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 4; y++) {
				int index = x+y*3;
				keypad[index] = new FiaUIRect(this,10+10*x,18+10*y,9,9);
			}
		}
		nukeInfo = new FiaUIRect(this,86,59,67,16);
		switch1 = new FiaUIRect(this,7,61,18,18);
		switch2 = new FiaUIRect(this,33,63,14,14);
		charge = new FiaUIRect(this,83,17,4,35);
	}
	@Override
	public void drawScreen(int mouseX,int mouseY,float partialTicks) {
		super.drawScreen(mouseX,mouseY,partialTicks);
		if (unlocked) {
			this.drawElectricityInfo(this,mouseX,mouseY,guiLeft+57,guiTop+20,16,52,te.power,te.getMaxPower());
			if (switch1.isMouseIn(mouseX,mouseY)) {
				if (te.isOn)
					drawHoveringText(I18nUtil.resolveKey("tile.dfc_pulser.gui.switch1.on"),mouseX,mouseY);
				else
					drawHoveringText(I18nUtil.resolveKey("tile.dfc_pulser.gui.switch1.off"),mouseX,mouseY);
			}
			if (switch2.isMouseIn(mouseX,mouseY))
				drawHoveringText(TextFormatting.RED+I18nUtil.resolveKey("tile.dfc_pulser.gui.switch2"),mouseX,mouseY);
			if (charge.isMouseIn(mouseX,mouseY)) {
				float timeLeft = 30;
				if (te.lastGetCore instanceof IMixinTileEntityCore mixin) {
					timeLeft = 30-mixin.getDetonationTimer()/20f;
					if (mixin.getDetonation())
						timeLeft -= partialTicks/20f;
				}
				String unit = "s";
				if (AddonConfig.bullshitUnits) {
					unit = "stevejob";
					timeLeft = (float)BullshitInDavenportIowa.SToSj(timeLeft);
				}
				drawHoveringText(TextFormatting.RED+String.format("%02.2f"+unit,timeLeft),mouseX,mouseY);
			}
			IMixinTileEntityCore mixin = (IMixinTileEntityCore)te.lastGetCore;
			if (nukeInfo.isMouseIn(mouseX,mouseY)) {
				int power = mixin.lastPulserPower();
				if (power >= 1) {
					if (power == 1)
						drawHoveringText(I18nUtil.resolveKey("tile.dfc_pulser.gui.radius",I18nUtil.resolveKey("tile.dfc_pulser.gui.radius.minimal")),mouseX,mouseY);
					else {
						int radius = 130*(power-1);
						if (power >= 5)
							radius += 30*(power-8);
						if (power >= 8)
							radius += 100*(power-8);
						if (!AddonConfig.bullshitUnits)
							drawHoveringText(I18nUtil.resolveKey("tile.dfc_pulser.gui.radius",radius+"m"),mouseX,mouseY);
						else
							drawHoveringText(I18nUtil.resolveKey("tile.dfc_pulser.gui.radius",BullshitInDavenportIowa.MToBn(radius)+"banana"),mouseX,mouseY);
					}
				}
			}
		}
		super.renderHoveredToolTip(mouseX,mouseY);
	}
	@Override
	protected void drawGuiContainerForegroundLayer( int i, int j) {
		String name = I18n.format(te.getName());
		this.fontRenderer.drawString(name,this.xSize/2-this.fontRenderer.getStringWidth(name)/2,6,4210752);
		String inventory = I18n.format("container.inventory");
		//this.fontRenderer.drawString(inventory,this.xSize-8-this.fontRenderer.getStringWidth(inventory),this.ySize-96+2,4210752);
		String message = "";
		if (te.lastGetCore == null)
			message = I18nUtil.resolveKey("tile.dfc_pulser.gui.status.connect");
		else {
			if (!unlocked) {
				if (!te.local$codeSet)
					message = I18nUtil.resolveKey("tile.dfc_pulser.gui.status.new",code);
				else
					message = I18nUtil.resolveKey("tile.dfc_pulser.gui.status.code",code);
			}
		}
		if (!message.isEmpty())
			fontRenderer.drawString(message,xSize/2f+10-fontRenderer.getStringWidth(message)/2f,41,0xFFFFFF,true);
	}
	public void playKeypad() {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(LeafiaSoundEvents.UI_BUTTON_KEYPAD,1));
	}
	@Override
	public void playClick(float pitch) {
		super.playClick(pitch);
	}
	@Override
	public void playDenied() {
		super.playDenied();
	}
	@Override
	protected void mouseClicked(int mouseX,int mouseY,int mouseButton) throws IOException {
		super.mouseClicked(mouseX,mouseY,mouseButton);
		if (mouseButton == 0) {
			if (switch1.isMouseIn(mouseX,mouseY)) {
				if (unlocked) {
					playClick(1);
					LeafiaPacket._start(te).__write(0,auth).__write(1,!te.isOn).__sendToServer();
				} else
					playDenied();
			}
			if (switch2.isMouseIn(mouseX,mouseY)) {
				if (unlocked) {
					playClick(1);
					LeafiaPacket._start(te).__write(0,auth).__write(2,false).__sendToServer();
				} else
					playDenied();
			}
			for (int i = 0; i < 12; i++) {
				FiaUIRect rect = keypad[i];
				if (rect.isMouseIn(mouseX,mouseY) && !blocked) {
					if (!unlocked) {
						int n = i+1;
						if (n == 10)
							n = 0;
						if (n == 11) {
							if (!code.isEmpty()) {
								blocked = true;
								LeafiaPacket._start(te).__write(30,code).__sendToServer();
							} else
								playDenied();
						} else {
							if (n == 12)
								code = "";
							else
								code = code + n;
							playKeypad();
						}
					} else
						playDenied();
				}
			}
		}
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,int mouseX,int mouseY) {
		drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(rsc);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		LeafiaGls.color(0.9f,0.9f,0.9f);
		if (!unlocked) {
			for (FiaUIRect rect : keypad) {
				if (rect.isMouseIn(mouseX,mouseY))
					drawTexturedModalByFiaRect(rect,rect.x,rect.y);
			}
		}
		LeafiaGls.color(1,1,1);
		if (te.isOn)
			drawTexturedModalByFiaRect(switch1,192,0);
		if (te.lastGetCore != null) {
			IMixinTileEntityCore mixin = (IMixinTileEntityCore)te.lastGetCore;
			if (mixin.getDetonation())
				drawTexturedModalByFiaRect(switch2,210,0);
			int power = mixin.lastPulserPower();
			if (power >= 8) {
				drawTexturedModalRect(guiLeft+137,guiTop+59,240,18,16,16);
				LeafiaGls.color(0.5f,0.5f,0.5f);
			}
			if (power >= 5) {
				drawTexturedModalRect(guiLeft+120,guiTop+59,224,18,16,16);
				LeafiaGls.color(0.5f,0.5f,0.5f);
			}
			if (power >= 2) {
				drawTexturedModalRect(guiLeft+103,guiTop+59,208,18,16,16);
				LeafiaGls.color(0.5f,0.5f,0.5f);
			}
			if (power >= 1)
				drawTexturedModalRect(guiLeft+86,guiTop+59,192,18,16,16);
			LeafiaGls.color(1,1,1);
		}
		int det = te.getDetScaled(35);
		drawTexturedModalRect(guiLeft+83,guiTop+52-det,176,87-det,4,det);
		int i = (int) te.getPowerScaled(52);
		drawTexturedModalRect(guiLeft+57,guiTop+73-i,176,52-i,16,i);
	}
}
