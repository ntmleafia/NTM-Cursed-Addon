package com.leafia.contents.machines.powercores.dfc.components.absorber;

import com.hbm.inventory.container.ContainerCoreReceiver;
import com.hbm.inventory.gui.GuiInfoContainer;
import com.hbm.lib.Library;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCoreReceiver;
import com.llib.LeafiaLib.NumScale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class CoreReceiverGUI extends GuiInfoContainer {

	private static ResourceLocation texture = new ResourceLocation("leafia:textures/gui/dfc/gui_receiver.png");
	private TileEntityCoreReceiver receiver;
	private GuiTextField field;

	@Override
	public void initGui() {
		IMixinTileEntityCoreReceiver mixin = (IMixinTileEntityCoreReceiver)receiver;
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.field = new GuiTextField(0, this.fontRenderer, guiLeft + 15, guiTop + 71, 24, 10);
		this.field.setTextColor(0x5BBC00);
		this.field.setDisabledTextColour(0x499500);
		this.field.setEnableBackgroundDrawing(false);
		this.field.setMaxStringLength(3);
		this.field.setText(String.valueOf(mixin.getLevel()*100));
	}

	public CoreReceiverGUI(EntityPlayer invPlayer,TileEntityCoreReceiver tedf) {
		super(new CoreReceiverContainer(invPlayer, tedf));
		receiver = tedf;
		
		this.xSize = 205;//176;
		this.ySize = 167;//166;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);

		receiver.tank.renderTankInfo(this, mouseX, mouseY, guiLeft + 17-7, guiTop + 16-11, 16, 52);
		//this.drawElectricityInfo(this,mouseX, mouseY, guiLeft + 46, guiTop + 6, 16, 52, receiver.power, NumScale.PETA);
		this.drawCustomInfo(mouseX, mouseY, guiLeft + 46, guiTop + 6, 16, 52, new String[]{Library.getShortNumber(receiver.power)+"HE"});
		super.renderHoveredToolTip(mouseX, mouseY);
	}
	int saveButtonCooldown = 0;
	@Override
	protected void mouseClicked(int x,int y,int i) throws IOException {
		super.mouseClicked(x,y,i);
		this.field.mouseClicked(x, y, i);
		if (guiLeft+50 < x && guiLeft+50+18 > x && guiTop+65 < y && guiTop+65+18 > y && i == 0) {
			if (saveButtonCooldown <= 0) {
				saveButtonCooldown = 20;
				try {
					double level = MathHelper.clamp(Double.parseDouble(field.getText()) / 100d,0,1);
					field.setText(String.valueOf(level*100));
					LeafiaPacket._start(receiver).__write(0,level).__sendToServer();
					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK,1.0F));
				} catch (NumberFormatException ignored) {}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer( int i, int j) {
		IMixinTileEntityCoreReceiver mixin = (IMixinTileEntityCoreReceiver)receiver;
		String name = this.receiver.hasCustomName() ? this.receiver.getName() : I18n.format(this.receiver.getName(), new Object[0]);
		this.fontRenderer.drawString(name, 114 - this.fontRenderer.getStringWidth(name)/2, 7, 4210752);

		this.fontRenderer.drawString("Input:", /*54+29*/98-this.fontRenderer.getStringWidth("Input:")/2+5, 21, 4210752);
		String sparks = Library.getShortNumber(receiver.joules) + "SPK";
		this.fontRenderer.drawString(sparks, 161+29-this.fontRenderer.getStringWidth(sparks), 21, 0x4EB3DB);
		this.fontRenderer.drawString("Relaying:", /*54+29*/98-this.fontRenderer.getStringWidth("Relaying:")/2+5, 21+20, 4210752);
		String relayin = Library.getShortNumber(mixin.syncSpk()) + "SPK";
		this.fontRenderer.drawString(relayin, 161+29-this.fontRenderer.getStringWidth(relayin), 21+20, 0x4EB3DB);
		this.fontRenderer.drawString("Output:", /*54+29*/98-this.fontRenderer.getStringWidth("Output:")/2, 21+20*2, 4210752);
		String power = Library.getShortNumber((long)mixin.joulesPerSec()*5000*20) + "HE/s";
		this.fontRenderer.drawString(power, 161+29-this.fontRenderer.getStringWidth(power), 21+20*2, 0xD84EDB);

		String inventory = I18n.format("container.inventory");
		this.fontRenderer.drawString(inventory, this.xSize - 8 - this.fontRenderer.getStringWidth(inventory), this.ySize - 96 + 2 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawDefaultBackground();
		IMixinTileEntityCoreReceiver mixin = (IMixinTileEntityCoreReceiver)receiver;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		int px = (int)Math.round(35*mixin.getLevel());
		drawTexturedModalRect(guiLeft+192-px,guiTop+9,256-px,32,px,4);

		if (field.isFocused())
			drawTexturedModalRect(guiLeft + 7, guiTop + 67, 221, 0, 32, 14);

		if (saveButtonCooldown > 0) {
			saveButtonCooldown--;
			drawTexturedModalRect(guiLeft+50,guiTop+65,221,14,18,18);
		}
		int i = (int) (MathHelper.clamp(receiver.joules/(double)(NumScale.GIGA*100L),0,1)*52);
		drawTexturedModalRect(guiLeft + 46, guiTop + 58 - i, 205, 52 - i, 16, i);

		this.field.drawTextBox();
		receiver.tank.renderTank(guiLeft+10, guiTop+69-11, zLevel, 16, 52);
	}
	protected void keyTyped(char p_73869_1_, int p_73869_2_) throws IOException
	{
		if (this.field.textboxKeyTyped(p_73869_1_, p_73869_2_)) { }
		else {
			super.keyTyped(p_73869_1_, p_73869_2_);
		}
	}
}