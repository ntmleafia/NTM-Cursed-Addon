package com.leafia.contents.machines.powercores.dfc.components.creativeemitter;

import com.hbm.inventory.gui.GuiInfoContainer;
import com.hbm.lib.Library;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toserver.AuxButtonPacket;
import com.leafia.AddonBase;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.llib.math.SIPfx;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class CEmitterGUI extends GuiInfoContainer {

    private static final ResourceLocation texture = new ResourceLocation(AddonBase.MODID + ":textures/gui/dfc/gui_cemitter.png");
    protected short saveButtonCoolDown = 0;
    int focused = -1;
    private final CEmitterTE emitter;
    private final GuiTextField[] fields = new GuiTextField[4];

    public CEmitterGUI(EntityPlayer invPlayer,CEmitterTE tedf) {
        super(new CEmitterContainer(invPlayer, tedf));
        emitter = tedf;

        this.xSize = 176;
        this.ySize = 170;
    }

    public void initGui() {

        super.initGui();

        Keyboard.enableRepeatEvents(true);
        for (int z = 0; z < 4; z++) {
            fields[z] = new GuiTextField(0, this.fontRenderer, guiLeft + 12, guiTop + 20 + 14 * z, 29, 7);
            fields[z].setTextColor(0x5BBC00);
            fields[z].setDisabledTextColour(0x499500);
            fields[z].setEnableBackgroundDrawing(false);
            fields[z].setMaxStringLength(5);
        }
        syncTextField();
    }

    public void syncTextField() {
        for (int z = 0; z < 4; z++)
            fields[z].setText(SIPfx.custom((x, s) -> x.longValue() + s.substring(1), emitter.joulesT[z], false));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);

        if (emitter.changed) {
            emitter.changed = false;
            syncTextField();
        }

        String[] output = new String[]{"Output: " + Library.getShortNumber(emitter.prev) + "SPK"};
        this.drawCustomInfoStat(mouseX, mouseY, guiLeft + 81, guiTop + 14, 8, 39, mouseX, mouseY, output);

        //FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 13, guiTop + 20, 16, 52, emitter.tank, ModForgeFluids.cryogel);
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void mouseClicked(int x, int y, int i) throws IOException {
        super.mouseClicked(x, y, i);
        for (int z = 0; z < 4; z++) {
            fields[z].mouseClicked(x, y, i);
            if (guiLeft + 61 <= x && guiLeft + 61 + 10 >= x && guiTop + 17 + 14 * z <= y && guiTop + 17 + 13 + 14 * z >= y) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                LeafiaPacket._start(emitter).__write(0, z).__sendToServer();
            }
        }
/*
    	if(guiLeft + 124 <= x && guiLeft + 124 + 18 > x && guiTop + 56 < y && guiTop + 56 + 18 >= y) {

    		if(saveButtonCoolDown == 0 && NumberUtils.isCreatable(field.getText())) {
    			int j = MathHelper.clamp(Integer.parseInt(field.getText()), 1, 100);
    			field.setText(j + "");
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	    		PacketDispatcher.wrapper.sendToServer(new AuxButtonPacket(emitter.getPos(), j, 0));
	    		saveButtonCoolDown = 20;
    		}
    	}
*/
        if (guiLeft + 151 - 75 <= x && guiLeft + 151 + 18 - 75 > x && guiTop + 56 < y && guiTop + 56 + 18 >= y) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            PacketDispatcher.wrapper.sendToServer(new AuxButtonPacket(emitter.getPos(), 0, 1));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j) {
        String name = "Creative DFC Emitter (Debug)";//I18n.format(this.emitter.getInventoryName());
        this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);

        String inventory = I18n.format("container.inventory");
        this.fontRenderer.drawString(inventory, this.xSize - 8 - this.fontRenderer.getStringWidth(inventory), this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        int lastFocused = focused;
        focused = -1;
        for (int z = 0; z < 4; z++) {
            if (fields[z].isFocused()) {
                drawTexturedModalRect(guiLeft + 11, guiTop + 19 + 14 * z, 194, 0, 31, 9);
                focused = z;
            }
        }
        try {
            if (lastFocused != -1 && focused != lastFocused)
                LeafiaPacket._start(emitter).__write(lastFocused + 1, (long) SIPfx.parse(fields[lastFocused].getText())).__sendToServer();
        } catch (NumberFormatException ignored) {
        }
        drawTexturedModalRect(guiLeft + 61, guiTop + 17 + 14 * emitter.selecting, 194, 9, 10, 13);

        if (emitter.isOn) {
            drawTexturedModalRect(guiLeft + 151 - 76, guiTop + 56, 192 - 16, 0, 18, 18);
        }

        int emitterWatts = emitter.getWattsScaled(35);
        drawTexturedModalRect(guiLeft + 81, guiTop + 52 - emitterWatts, 176, 87 - emitterWatts, 4, emitterWatts);

        int i = (int) emitter.getPowerScaled(52);
        drawTexturedModalRect(guiLeft + 49, guiTop + 73 - i, 176, 52 - i, 16, i);

        if (emitter.isOn && emitter.power > 500000) drawTexturedModalRect(guiLeft + 149 + 2, guiTop + 33, 176, 53, 80, 3);

        if (saveButtonCoolDown > 0) {
            drawTexturedModalRect(guiLeft + 124, guiTop + 56, 192, 18, 18, 18);
            saveButtonCoolDown--;
        }

        for (int z = 0; z < 4; z++)
            fields[z].drawTextBox();

        emitter.tank.renderTank(guiLeft + 13, guiTop + 101, zLevel, 16, 52);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 13 && focused != -1) fields[focused].setFocused(false);
        for (int z = 0; z < 4; z++)
            if (fields[z].textboxKeyTyped(typedChar, keyCode)) return;
        super.keyTyped(typedChar, keyCode);
    }
}