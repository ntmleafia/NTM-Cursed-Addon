package com.leafia.contents.machines.reactors.lftr.processing.separator.container;

import com.hbm.inventory.gui.GUIScreenRecipeSelector;
import com.hbm.inventory.gui.GuiInfoContainer;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.items.machine.ItemBlueprints;
import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorTE;
import com.leafia.contents.machines.reactors.lftr.processing.separator.recipes.SaltSeparatorRecipes;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.io.IOException;

public class SaltSeparatorGUI extends LCEGuiInfoContainer {
	private final SaltSeparatorTE separator;
	static final ResourceLocation tex = new ResourceLocation("leafia","textures/gui/lftr/gui_separator.png");
	public SaltSeparatorGUI(InventoryPlayer invPlayer,SaltSeparatorTE separator) {
		super(new SaltSeparatorContainer(invPlayer,separator.getCheckedInventory()));
		this.separator = separator;
		this.xSize = 176;
		this.ySize = 256;
	}
	@Override
	public void initGui() {
		super.initGui();
	}
	protected boolean checkClick(int x, int y, int left, int top, int sizeX, int sizeY) {
		return guiLeft + left <= x
				&& guiLeft + left + sizeX > x
				&& guiTop + top < y
				&& guiTop + top + sizeY >= y;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		super.renderHoveredToolTip(mouseX, mouseY);
		for (int i = 0; i < 2; i++)
			LeafiaClientUtil.renderTankInfo(separator.inputTanks[i], this, mouseX, mouseY, guiLeft + 26+i*18, guiTop + 18, 16, 34);
		for(int i = 0; i < 3; i++)
			LeafiaClientUtil.renderTankInfo(separator.outputTanks[i], this, mouseX, mouseY, guiLeft + 80 + i * 18, guiTop + 18, 16, 34);

		LeafiaClientUtil.renderTankInfo(this,mouseX,mouseY,guiLeft+8,guiTop+18,16,70,separator.saltTank,separator.saltType.getFF());
		LeafiaClientUtil.renderTankInfo(this,mouseX,mouseY,guiLeft+8,guiTop+99,52,7,separator.bufferIn,separator.saltType.getFF());
		LeafiaClientUtil.renderTankInfo(this,mouseX,mouseY,guiLeft+80,guiTop+99,52,7,separator.bufferOut,separator.saltType.getFF());

		LeafiaClientUtil.renderTankInfo(separator.concIn,this,mouseX,mouseY,guiLeft+8,guiTop+108,52,7);
		LeafiaClientUtil.renderTankInfo(separator.concOut,this,mouseX,mouseY,guiLeft+80,guiTop+108,52,7);

		this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 152, guiTop + 18, 16, 61, separator.power, separator.maxPower);

		if(guiLeft + 7 <= mouseX && guiLeft + 7 + 18 > mouseX && guiTop + 125 < mouseY && guiTop + 125 + 18 >= mouseY) {
			if(this.separator.module.recipe != null && SaltSeparatorRecipes.INSTANCE.recipeNameMap.containsKey(this.separator.module.recipe)) {
				GenericRecipe recipe = SaltSeparatorRecipes.INSTANCE.recipeNameMap.get(this.separator.module.recipe);
				this.drawHoveringText(recipe.print(), mouseX, mouseY);
			} else {
				this.drawHoveringText(TextFormatting.YELLOW + I18nUtil.resolveKey("gui.recipe.setRecipe"), mouseX, mouseY);
			}
		}
	}
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);

		if(this.checkClick(x, y, 7, 125, 18, 18)) GUIScreenRecipeSelector.openSelector(SaltSeparatorRecipes.INSTANCE, separator, separator.module.recipe, 0, ItemBlueprints.grabPool(separator.inventory.getStackInSlot(1)), this);
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.separator.hasCustomName() ? this.separator.getName() : I18n.format(this.separator.getName());

		this.fontRenderer.drawString(name, 70 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	public void renderItem(ItemStack stack,int x,int y) {
		renderItem(stack, x, y, 100F);
	}
	public void renderItem(ItemStack stack, int x, int y, float layer) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) 240, (float) 240);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		itemRender.zLevel = layer;
		itemRender.renderItemAndEffectIntoGUI(stack, guiLeft + x, guiTop + y);
		itemRender.zLevel = 0.0F;
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,int mouseX,int mouseY) {
		super.drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		int p = (int) (separator.power * 61 / separator.maxPower);
		drawTexturedModalRect(guiLeft + 152, guiTop + 79 - p, 176, 61 - p, 16, p);

		if(separator.module.progress > 0) {
			int j = (int) Math.ceil(70 * separator.module.progress);
			drawTexturedModalRect(guiLeft + 62, guiTop + 126, 176, 61, j, 16);
		}

		GenericRecipe recipe = SaltSeparatorRecipes.INSTANCE.recipeNameMap.get(separator.module.recipe);

		/// LEFT LED
		if(separator.didProcess) {
			drawTexturedModalRect(guiLeft + 51, guiTop + 121, 195, 0, 3, 6);
		} else if(recipe != null) {
			drawTexturedModalRect(guiLeft + 51, guiTop + 121, 192, 0, 3, 6);
		}

		/// RIGHT LED
		if(separator.didProcess) {
			drawTexturedModalRect(guiLeft + 56, guiTop + 121, 195, 0, 3, 6);
		} else if(recipe != null && separator.power >= recipe.power) {
			drawTexturedModalRect(guiLeft + 56, guiTop + 121, 192, 0, 3, 6);
		}

		this.renderItem(recipe != null ? recipe.getIcon() : GuiInfoContainer.TEMPLATE_FOLDER, 8, 126);

		if(recipe != null && recipe.inputItem != null) {
			/*for(int i = 0; i < recipe.inputItem.length; i++) {
				Slot slot = this.inventorySlots.inventorySlots.get(separator.module.inputSlots[i]);
				if(!slot.getHasStack()) this.renderItem(recipe.inputItem[i].extractForCyclingDisplay(20), slot.xPos, slot.yPos, 10F);
			}*/

			Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.color(1F, 1F, 1F, 0.5F);
			GlStateManager.enableBlend();
			this.zLevel = 300F;
			/*for(int i = 0; i < recipe.inputItem.length; i++) {
				Slot slot = this.inventorySlots.inventorySlots.get(separator.module.inputSlots[i]);
				if(!slot.getHasStack()) drawTexturedModalRect(guiLeft + slot.xPos, guiTop + slot.yPos, slot.xPos, slot.yPos, 16, 16);
			}*/
			this.zLevel = 0F;
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.disableBlend();
		}

		for (int i = 0; i < 2; i++)
			separator.inputTanks[i].renderTank(guiLeft + 26+i*18, guiTop + 52, this.zLevel, 16, 34);
		for(int i = 0; i < 3; i++)
			separator.outputTanks[i].renderTank(guiLeft + 80 + i * 18, guiTop + 52, this.zLevel, 16, 34);
		LeafiaClientUtil.drawLiquid(separator.saltTank,guiLeft+8,guiTop+88,zLevel,16,70,0,28);
		LeafiaGls.pushMatrix(); {
			LeafiaGls.translate(guiLeft+8,guiTop+99,0);
			LeafiaGls.rotate(90,0,0,1);
			LeafiaClientUtil.drawLiquid(separator.bufferIn,0,0,zLevel,7,52,0,28);
		} LeafiaGls.popMatrix();
		LeafiaGls.pushMatrix(); {
			LeafiaGls.translate(guiLeft+80,guiTop+99,0);
			LeafiaGls.rotate(90,0,0,1);
			LeafiaClientUtil.drawLiquid(separator.bufferOut,0,0,zLevel,7,52,0,28);
		} LeafiaGls.popMatrix();
		separator.concIn.renderTank(guiLeft+8,guiTop+108+7,zLevel,52,7,1);
		separator.concOut.renderTank(guiLeft+80,guiTop+108+7,zLevel,52,7,1);
	}
}
