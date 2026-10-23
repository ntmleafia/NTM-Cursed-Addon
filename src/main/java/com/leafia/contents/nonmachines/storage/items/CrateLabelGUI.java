// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.nonmachines.storage.items;

import com.hbm.tileentity.machine.storage.TileEntityCrateBase;
import com.hbm.util.I18nUtil;
import com.leafia.AddonBase;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCrate;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class CrateLabelGUI extends LCEGuiInfoContainer {

	private static ResourceLocation texture = new ResourceLocation("leafia:textures/gui/crate_labelling.png");

	private final TileEntityCrateBase entity;
	private final IBlockState state;
	private final GuiTextField[] fields = new GuiTextField[4];
	private final EnumFacing facing;

	public CrateLabelGUI(InventoryPlayer invPlayer,TileEntityCrateBase entity,IBlockState state,EnumFacing side) {
		super(new CrateLabelContainer(invPlayer, entity));
		this.entity = entity;
		this.xSize = 176;
		this.ySize = 185;
		this.state = state;
		facing = side;
	}

	@Override
	public void initGui() {
		super.initGui();
		IMixinTileEntityCrate mixin = (IMixinTileEntityCrate)entity;
		fields[0] = new GuiTextField(0,fontRenderer,guiLeft+61,guiTop+26-5,54,5);
		fields[0].setText(mixin.leafia$verticalLabels()[(int)MathHelper.positiveModulo(1-facing.getHorizontalIndex(),4)]);
		fields[1] = new GuiTextField(0,fontRenderer,guiLeft+61,guiTop+16+(70-16)/2-9-2,54,5);
		fields[1].setText(mixin.leafia$upperLabel());
		fields[3] = new GuiTextField(0,fontRenderer,guiLeft+61,guiTop+16+(70-16)/2+9-2,54,5);
		fields[3].setText(mixin.leafia$lowerLabel());
		fields[2] = new GuiTextField(0,fontRenderer,guiLeft+61+18,guiTop+16+(70-16)/2-2,54-18,5);
		fields[2].setMaxStringLength(2);
		fields[2].setText(mixin.leafia$middleLabel());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX,mouseY,f);
		super.renderHoveredToolTip(mouseX,mouseY);
	}

	protected void mouseClicked(int x,int y,int i) throws IOException {
		super.mouseClicked(x,y,i);
		for (GuiTextField field : fields)
			field.mouseClicked(x,y,i);
    }

	@Override
	protected void keyTyped(char typedChar,int keyCode) throws IOException {
		boolean ignore = false;
		int index = 0;
		IMixinTileEntityCrate mixin = (IMixinTileEntityCrate)entity;
		for (GuiTextField field : fields) {
			ignore = ignore || field.textboxKeyTyped(typedChar,keyCode);
			while (fontRenderer.getStringWidth(field.getText()) > fontRenderer.getStringWidth("handle with care"))
				field.setText(field.getText().substring(0,field.getText().length()-1));
			if (index == 0)
				mixin.leafia$verticalLabels()[(int)MathHelper.positiveModulo(1-facing.getHorizontalIndex(),4)] = field.getText();
			else if (index == 1)
				mixin.leafia$setUpperLabel(field.getText());
			else if (index == 2)
				mixin.leafia$setMiddleLabel(field.getText());
			else if (index == 3)
				mixin.leafia$setLowerLabel(field.getText());
			index++;
		}
		if (!ignore)
			super.keyTyped(typedChar,keyCode);
		else
			mixin.generateSyncPacket().__sendToServer();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18nUtil.resolveKey("gui.label_crate");

		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);

		IBakedModel baked = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
		tryBindQuads(baked,state,EnumFacing.NORTH);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();
		buf.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX);
		buf.pos(guiLeft+71,guiTop+26+34,zLevel).tex(0,1).endVertex();
		buf.pos(guiLeft+71+34,guiTop+26+34,zLevel).tex(1,1).endVertex();
		buf.pos(guiLeft+71+34,guiTop+26,zLevel).tex(1,0).endVertex();
		buf.pos(guiLeft+71,guiTop+26,zLevel).tex(0,0).endVertex();
		tessellator.draw();
		int index = 0;
		for (GuiTextField field : fields) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(AddonBase.solid);
			/*
			float brightness = 1;
			if (mouseX >= field.x && mouseX < field.x+field.width && mouseY >= field.y && mouseY < field.y+field.height)
				brightness *= 0.9f;
			if (field.isFocused())
				brightness *= 1.2f;
			LeafiaGls.color(0.627f*brightness,0.59f*brightness,0.447f*brightness);
			drawTexturedModalRect(field.x,field.y,0,0,field.width,field.height);*/
			if (mouseX >= field.x && mouseX < field.x+field.width && mouseY >= field.y && mouseY < field.y+field.height) {
				LeafiaGls.color(1,1,1,0.35f);
				drawTexturedModalRect(field.x,field.y,0,0,field.width,field.height);
			}

			LeafiaGls.color(1,1,1);
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(field.x+field.width/2f,field.y+field.height/2f,0);
			LeafiaGls.scale(0.5,0.5,1);
			fontRenderer.drawString(
					field.getText(),-fontRenderer.getStringWidth(field.getText())/2,-4,
					(index == 0 || index == 4) ? 4210752 : 0xFFFFFF
			);
			LeafiaGls.popMatrix();
			index++;
		}
	}

	void bindByIconName(String resource) {
		// convert format like "hbm:         blocks/brick_concrete    "
		//                  to "hbm:textures/blocks/brick_concrete.png"
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(resource.replaceFirst("(\\w+:)?(.*)","$1textures/$2.png")));
	}

	void tryBindQuads(IBakedModel baked,IBlockState display,EnumFacing face) {
		try {
			List<BakedQuad> quads = baked.getQuads(display,face,0);
			if (quads.size() > 0)
				bindByIconName(quads.get(0).getSprite().getIconName());
			else
				bindByIconName(baked.getParticleTexture().getIconName());
		} catch (IllegalArgumentException ignored) {} // FUCK YOUU
	}
}
