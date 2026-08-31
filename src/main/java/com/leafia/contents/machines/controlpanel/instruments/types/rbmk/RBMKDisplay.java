package com.leafia.contents.machines.controlpanel.instruments.types.rbmk;

import com.hbm.blocks.generic.BlockControlPanel;
import com.hbm.blocks.machine.rbmk.RBMKBase;
import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.render.loader.IModelCustom;
import com.hbm.render.util.NTMBufferBuilder;
import com.hbm.render.util.NTMImmediate;
import com.hbm.tileentity.machine.rbmk.RBMKColumn;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.leafia.contents.machines.controlpanel.instruments.AddonControlType;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.dev.render.LeafiaBrush.BrushMode;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.leafia.AddonBase.getIntegrated;

public class RBMKDisplay extends Control {
	static final ResourceLocation tex = getIntegrated("control_panel/instruments/rbmk.png");
	int width = 1;
	int height = 1;
	int offsetU = 0;
	int offsetV = 0;
	static final float sc = 1; //10/9f; screw it!
	public RBMKDisplay(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
		configMap.put("width",new DataValueFloat(width));
		configMap.put("height",new DataValueFloat(height));
		configMap.put("offsetU",new DataValueFloat(offsetU));
		configMap.put("offsetV",new DataValueFloat(offsetV));
	}
	@SideOnly(Side.CLIENT)
	@Override
	public SubElementBaseConfig getConfigSubElement(GuiControlEdit gui,Map<String,DataValue> configs) {
		return new RBMKDisplayCSE(gui,configs);
	}
	@SideOnly(Side.CLIENT)
	public void renderControl(float[] renderBox,Control selectedControl,GuiControlEdit gui) {
		NTMBufferBuilder buf = NTMImmediate.INSTANCE.beginPositionTexColorQuads(1);
		int packedColor = NTMBufferBuilder.packColor(1.0F,this == selectedControl ? 0.8F : 1.0F,1.0F,1.0F);
		appendGuiQuad(buf,renderBox[0],renderBox[1],renderBox[2],renderBox[3],offsetU/2f,offsetV/2f,width/2f+offsetU/2f,height/2f+offsetV/2f,packedColor);
		NTMImmediate.INSTANCE.draw();
		LeafiaBrush brush = LeafiaBrush.instance;
		float x = (renderBox[0]+renderBox[2])/2;
		float y = (renderBox[1]+renderBox[3])/2;
		LeafiaGls.disableTexture2D();
		brush.startDrawing(BrushMode.LINES,DefaultVertexFormats.POSITION_COLOR);
		brush.addVertexWithColor(x-width/2f,y-height/2f,0,1,1,1,1);
		brush.addVertexWithColor(x+width/2f,y-height/2f,0,1,1,1,1);

		brush.addVertexWithColor(x+width/2f,y-height/2f,0,1,1,1,1);
		brush.addVertexWithColor(x+width/2f,y+height/2f,0,1,1,1,1);

		brush.addVertexWithColor(x+width/2f,y+height/2f,0,1,1,1,1);
		brush.addVertexWithColor(x-width/2f,y+height/2f,0,1,1,1,1);

		brush.addVertexWithColor(x-width/2f,y+height/2f,0,1,1,1,1);
		brush.addVertexWithColor(x-width/2f,y-height/2f,0,1,1,1,1);
		brush.draw();
		LeafiaGls.enableTexture2D();
	}
	@Override
	protected void onConfigMapChanged() {
		for (Entry<String,DataValue> entry : configMap.entrySet()) {
			switch(entry.getKey()) {
				case "width" -> width = (int)entry.getValue().getNumber();
				case "height" -> height = (int)entry.getValue().getNumber();
				case "offsetU" -> offsetU = entry.getValue().getBoolean() ? 1 : 0;
				case "offsetV" -> offsetV = entry.getValue().getBoolean() ? 1 : 0;
			}
		}
	}
	@Override
	public ControlType getControlType() {
		return AddonControlType.RBMK_DISPLAY;
	}
	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}
	@Override
	public float[] getSize() {
		return new float[]{ sc,sc,0.1f };
	}
	@Override
	public void render() {
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		LeafiaGls.color(1,1,1);
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX,0.005,posY);
		texmg.bindTexture(tex);
		LeafiaBrush brush = LeafiaBrush.instance;
		brush.startDrawingQuads();
		brush.addVertexWithUV(-width/2f*sc,0,height/2f*sc,offsetU/2f,height/2f+offsetV/2f);
		brush.addVertexWithUV(width/2f*sc,0,height/2f*sc,width/2f+offsetU/2f,height/2f+offsetV/2f);
		brush.addVertexWithUV(width/2f*sc,0,-height/2f*sc,width/2f+offsetU/2f,offsetV/2f);
		brush.addVertexWithUV(-width/2f*sc,0,-height/2f*sc,offsetU/2f,offsetV/2f);
		brush.draw();
		LeafiaGls.translate(0,0.005,0);
		World world = panel.parent.getControlWorld();
		IBlockState state = world.getBlockState(panel.parent.getControlPos());
		String error = null;
		if (taggedLinks.isEmpty()) error = "RBMK$not linked";
		else if (taggedLinks.size() > 1) error = "Must have only$1 position$linked";
		if (error != null) {
			LeafiaGls.rotate(90,1,0,0);
			LeafiaGls.scale(0.1);
			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			String[] str = error.split("\\$");
			for (int i = 0; i < str.length; i++) {
				float line = -(str.length-1)+i;
				String s = str[i];
				font.drawString(s,-font.getStringWidth(s)/2,(int)(line*11),0xFFFFFF);
			}
		} else {
			BlockPos center = (BlockPos)taggedLinks.values().toArray()[0];
			EnumFacing facing = state.getValue(BlockControlPanel.FACING).getOpposite();
			LeafiaGls.scale(1/0.125*sc);
			LeafiaGls.rotate(90,0,1,0);
			LeafiaGls.rotate(90,0,0,1);
			LeafiaGls.disableTexture2D();
			brush.startDrawing(BrushMode.QUADS,DefaultVertexFormats.POSITION_COLOR);
			float startX = -(width-1)/2f;
			float startY = -(height-1)/2f;
			int startXInt = (int)Math.floor(startX);
			int startYInt = (int)Math.floor(startY);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					BlockPos pos = center.offset(facing,-startYInt-y).offset(facing.rotateY(),startXInt+x);
					Block block = world.getBlockState(pos).getBlock();
					if (block instanceof RBMKBase rbmk) {
						pos = rbmk.findCore(world,pos);
						if (pos != null)
							processColumn(world,pos,brush.buf,(startY+y)*0.125,(startX+x)*0.125);
					}
				}
			}
			brush.draw();
			LeafiaGls.enableTexture2D();
		}
		LeafiaGls.popMatrix();
	}
	@SideOnly(Side.CLIENT)
	void processColumn(World world,BlockPos pos,BufferBuilder buf,double ky,double kz) {
		if (world.getTileEntity(pos) instanceof TileEntityRBMKBase rbmk) {
			RBMKColumn col = rbmk.getConsoleData();
			float r = 1.0F;
			float g = 1.0F;
			float b = 1.0F;
			if(col instanceof RBMKColumn.ControlColumn && ((RBMKColumn.ControlColumn) col).color >= 0) {
				short colorType = ((RBMKColumn.ControlColumn) col).color;
				if(colorType == 0) { g = 0.0F; b = 0.0F; }
				else if(colorType == 1) { b = 0.0F; }
				else if(colorType == 2) { r = 0.0F; g = 0.5F; b = 0.0F; }
				else if(colorType == 3) { r = 0.0F; g = 0.0F; }
				else if(colorType == 4) { r = 0.5F; g = 0.0F; }
			} else {
				double heat = col.maxHeat > 0 ? Math.min(col.heat / col.maxHeat,1) : 0;
				double baseColor = 0.65D;// + (i % 2) * 0.05D; what's this
				r = (float) (baseColor + ((1 - baseColor) * heat));
				g = (float) baseColor;
				b = (float) baseColor;
			}
			drawColumn(buf,0,ky,kz,r,g,b);
			switch (col.type) {
				case FUEL:
				case FUEL_SIM:
					drawFuel(buf, 0.01, ky, kz, ((RBMKColumn.FuelColumn) col).enrichment);
					break;
				case CONTROL:
					drawControl(buf, 0.01, ky, kz, ((RBMKColumn.ControlColumn) col).level);
					break;
				case CONTROL_AUTO:
					drawControlAuto(buf, 0.01, ky, kz, ((RBMKColumn.ControlColumn) col).level);
					break;
				default:
			}
		}
	}
	/// stolen code
	@SideOnly(Side.CLIENT)
	private void drawColumn(BufferBuilder buf,double x,double y,double z,float r,float g,float b) {
		double width = 0.0625D * 0.75;
		buf.pos(x, y + width, z - width).color(r, g, b, 1.0F).endVertex();
		buf.pos(x, y + width, z + width).color(r, g, b, 1.0F).endVertex();
		buf.pos(x, y - width, z + width).color(r, g, b, 1.0F).endVertex();
		buf.pos(x, y - width, z - width).color(r, g, b, 1.0F).endVertex();
	}
	/// stolen code
	@SideOnly(Side.CLIENT)
	private void drawFuel(BufferBuilder buf, double x, double y, double z, double enrichment) {
		this.drawDot(buf, x, y, z, 0F, 0.25F + (float) (enrichment * 0.75D), 0F);
	}
	/// stolen code
	@SideOnly(Side.CLIENT)
	private void drawControl(BufferBuilder buf, double x, double y, double z, double level) {
		this.drawDot(buf, x, y, z, (float) level, (float) level, 0F);
	}
	/// stolen code
	@SideOnly(Side.CLIENT)
	private void drawControlAuto(BufferBuilder buf, double x, double y, double z, double level) {
		this.drawDot(buf, x, y, z, (float) level, 0F, (float) level);
	}
	/// stolen code
	@SideOnly(Side.CLIENT)
	private void drawDot(BufferBuilder buf, double x, double y, double z, float r, float g, float b) {

		double width = 0.03125D;
		double edge = 0.022097D;

		buf.pos(x, y + width, z).color(r, g, b, 1F).endVertex();
		buf.pos(x, y + edge, z + edge).color(r, g, b, 1F).endVertex();
		buf.pos(x, y, z + width).color(r, g, b, 1F).endVertex();
		buf.pos(x, y - edge, z + edge).color(r, g, b, 1F).endVertex();

		buf.pos(x, y + edge, z - edge).color(r, g, b, 1F).endVertex();
		buf.pos(x, y + width, z).color(r, g, b, 1F).endVertex();
		buf.pos(x, y - edge, z - edge).color(r, g, b, 1F).endVertex();
		buf.pos(x, y, z - width).color(r, g, b, 1F).endVertex();

		buf.pos(x, y + width, z).color(r, g, b, 1F).endVertex();
		buf.pos(x, y - edge, z + edge).color(r, g, b, 1F).endVertex();
		buf.pos(x, y - width, z).color(r, g, b, 1F).endVertex();
		buf.pos(x, y - edge, z - edge).color(r, g, b, 1F).endVertex();
	}
	@SideOnly(Side.CLIENT)
	@Override
	public IModelCustom getModel() {
		return null;
	}
	@Override
	public ResourceLocation getGuiTexture() {
		return tex;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new RBMKDisplay(name,registryName,controlPanel);
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> list) { }
}
