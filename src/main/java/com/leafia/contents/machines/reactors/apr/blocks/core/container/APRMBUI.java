package com.leafia.contents.machines.reactors.apr.blocks.core.container;

import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.GuiScreenLeafia;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

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
	public static class ScrollItem {
		public GuiTextField field;
		public int index;
		public FiaUIRect deleteOrAdd;
	}
	@Override
	public void initGui() {
		super.initGui();
		regenItems();
	}
	public final List<ScrollItem> items = new ArrayList<>();
	public void regenItems() {
		items.clear();
		for (int i = 0; i < chambers.size(); i++) {
			int r = chambers.get(i);
			GuiTextField field = new GuiTextField(100+i,fontRenderer,guiLeft+113+2,0,30,10);
			field.setTextColor(0x5BBC00);
			field.setDisabledTextColour(0x499500);
			field.setEnableBackgroundDrawing(false);
			field.setMaxStringLength(2);
			field.setText(Integer.toString(r));
			ScrollItem item = new ScrollItem();
			item.field = field;
			item.deleteOrAdd = new FiaUIRect(this,148,0,19,19);
			items.add(item);
		}
		ScrollItem item = new ScrollItem();
		item.deleteOrAdd = new FiaUIRect(this,80,0,19,19);
		items.add(item);
	}
	@FunctionalInterface
	public interface ScrollItemConsumer {
		void accept(int i,ScrollItem item,int mx,int my);
	}
	public void forItems(ScrollItemConsumer consumer,int mx,int my) {
		int size = items.size();
		for (int i = scrollPos; i < scrollPos+4; i++) {
			if (items.size() != size) break;
			if (i >= items.size()) break;
			ScrollItem item = items.get(i);
			consumer.accept(i,item,mx,my);
		}
	}
	public int scrollPos = 0;
	public void updatePosition(int i,ScrollItem item,int mouseX,int mouseY) {
		if (item.field != null)
			item.field.y = guiTop+13+4-(scrollPos-i)*25;
		item.deleteOrAdd.y = 11-(scrollPos-i)*25;
	}
	public void renderItems(int i,ScrollItem item,int mouseX,int mouseY) {
		if (item.field != null) {
			drawTexturedModalRect(guiLeft+9,guiTop+9-(scrollPos-i)*25,0,130,160,23);
			fontRenderer.drawString(I18nUtil.resolveKey("desc.leafia.apr_mb.radius"),guiLeft+15,guiTop+17-(scrollPos-i)*25,4210752);
			bindTexture(tex);
			item.field.drawTextBox();
			bindTexture(tex);
			LeafiaGls.color(0.9f,0.9f,0.9f);
			if (item.deleteOrAdd.isMouseIn(mouseX,mouseY))
				drawTexturedModalByFiaRect(item.deleteOrAdd,139,132);
			LeafiaGls.color(1,1,1);
		} else {
			if (item.deleteOrAdd.isMouseIn(mouseX,mouseY))
				LeafiaGls.color(0.9f,0.9f,0.9f);
			drawTexturedModalByFiaRect(item.deleteOrAdd,160,130);
			LeafiaGls.color(1,1,1);
		}
	}
	public void renderHoveredInfo(int i,ScrollItem item,int mouseX,int mouseY) {
		if (item.field != null) {
			if (item.deleteOrAdd.isMouseIn(mouseX,mouseY))
				drawHoveringText(I18nUtil.resolveKey("desc.leafia.apr_mb.delete"),mouseX,mouseY);
		} else {
			if (item.deleteOrAdd.isMouseIn(mouseX,mouseY))
				drawHoveringText(I18nUtil.resolveKey("desc.leafia.apr_mb.add"),mouseX,mouseY);
		}
	}
	public void onClick(int i,ScrollItem item,int mouseX,int mouseY) {
		if (item.field != null) {
			if (item.deleteOrAdd.isMouseIn(mouseX,mouseY)) {
				chambers.remove(i);
				regenItems();
				playClick(1);
			}
		} else {
			if (item.deleteOrAdd.isMouseIn(mouseX,mouseY)) {
				chambers.add(5);
				regenItems();
				playClick(1);
			}
		}
	}
	@Override
	public void drawScreen(int mouseX,int mouseY,float partialTicks) {
		super.drawScreen(mouseX,mouseY,partialTicks);
		forItems(this::renderHoveredInfo,mouseX,mouseY);
	}
	@Override
	protected void mouseClicked(int mouseX,int mouseY,int mouseButton) throws IOException {
		super.mouseClicked(mouseX,mouseY,mouseButton);
		for (ScrollItem item : items) {
			if (item.field != null)
				item.field.mouseClicked(mouseX,mouseY,mouseButton);
		}
		forItems(this::onClick,mouseX,mouseY);
	}
	@Override
	protected void keyTyped(char typedChar,int keyCode) throws IOException {
		for (ScrollItem item : items) {
			if (item.field != null) {
				if (item.field.textboxKeyTyped(typedChar,keyCode))
					return;
			}
		}
		super.keyTyped(typedChar,keyCode);
	}
	@Override
	protected void drawGuiScreenBackgroundLayer(float partialTicks,int mouseX,int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1,1,1,1);
		bindTexture(tex);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		forItems(this::updatePosition,mouseX,mouseY);
		forItems(this::renderItems,mouseX,mouseY);
	}
}
