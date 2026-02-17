package com.leafia.dev;

import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.render.NTMRenderHelper;
import com.hbm.util.I18nUtil;
import com.hbm.util.RenderUtil;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.AddonItems;
import com.leafia.contents.fluids.traits.FT_LFTRCoolant;
import com.leafia.contents.gear.utility.FuzzyIdentifierItem;
import com.leafia.contents.gear.utility.FuzzyIdentifierItem.FuzzyIdentifierPacket;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import com.leafia.contents.machines.reactors.lftr.components.element.MSRElementTE.MSRFuel;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Client util, mostly for fluids.
 * <p>All methods are made @SideOnly(Side.CLIENT).
 * <p>Do NOT make the entire class client only as that would
 * <br>cause issues for dedicated server saying the entire class is missing.
 */
public class LeafiaClientUtil {
	@SideOnly(Side.CLIENT)
	public static String[] statusDecimals(String template,double value,int decimals) {
		double mul = Math.pow(10,decimals);
		return I18nUtil.resolveKeyArray(template,String.format("%01."+decimals+"f",Math.floor(value*mul+0.5)/mul));
	}
	static boolean lastClicked = false;
	/// add NTMFluid type fluid info for JEI
	@SideOnly(Side.CLIENT)
	public static void renderTankInfo(@NotNull FluidTankNTM tank,@NotNull LCEGuiInfoContainer gui,int mouseX,int mouseY,int x,int y,int width,int height) {
		if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
			List<String> list = new ArrayList();
			list.add(tank.getTankType().getLocalizedName());
			list.add(tank.getFill() + "/" + tank.getMaxFill() + "mB");
			if (tank.getPressure() != 0) {
				list.add(ChatFormatting.RED + "Pressure: " + tank.getPressure() + " PU");
			}

			if (Mouse.isButtonDown(0) && !lastClicked) {
				ItemStack item = Minecraft.getMinecraft().player.inventory.getItemStack();
				if (item != null && !item.isEmpty()) {
					if (item.getItem() instanceof FuzzyIdentifierItem) {
						FuzzyIdentifierPacket packet = new FuzzyIdentifierPacket();
						packet.fluidRsc = tank.getTankType().getName();
						LeafiaCustomPacket.__start(packet).__sendToServer();
						Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("item.fuzzy_identifier.message",tank.getTankType().getLocalizedName()).setStyle(new Style().setColor(TextFormatting.YELLOW)));
					}
				}
			}
			lastClicked = Mouse.isButtonDown(0);
			tank.getTankType().addInfo(list);
			gui.drawFluidInfo((String[])list.toArray(new String[0]), mouseX, mouseY);
		}
	}
	/// add NTMFluid type fluid info for JEI
	@SideOnly(Side.CLIENT)
	public static void jeiFluidRenderInfo(FluidStack stack,List<String> info,int mx,int my,int x,int y,int width,int height) {
		if (stack == null) return;
		mx--; my--;
		if (mx >= x && mx <= x+width && my >= y && my <= y+height) {
			info.add(stack.type.getLocalizedName());
			info.add(TextFormatting.GRAY+Integer.toString(stack.fill)+"mB");
			if (stack.pressure != 0) {
				info.add(ChatFormatting.RED + "Pressure: " + stack.pressure + " PU");
			}
			stack.type.addInfo(info);
		}
	}
	/// render NTMFluid type fluid for JEI
	@SideOnly(Side.CLIENT)
	public static void jeiFluidRenderTank(List<FluidStack> stacks,FluidStack stack,int x,int y,int width,int height,boolean horizontal) {
		if (stack == null) return;
		x++; y++;
		FluidType type = stack.type;
		int color = type.getTint();
		double r = ((color & 0xff0000) >> 16) / 255D;
		double g = ((color & 0x00ff00) >> 8) / 255D;
		double b = ((color & 0x0000ff) >> 0) / 255D;
		GL11.glColor3d(r, g, b);
		boolean wasBlendEnabled = RenderUtil.isBlendEnabled();
		if (!wasBlendEnabled) GlStateManager.enableBlend();
		Minecraft.getMinecraft().getTextureManager().bindTexture(type.getTexture());
		int maxFill = 0;
		for (FluidStack stack1 : stacks)
			maxFill = Math.max(maxFill,stack1.fill);
		int px = maxFill != 0 ? MathHelper.ceil(stack.fill * height / (float)maxFill) : 0;
		double minX = x;
		double maxX = x;
		double minY = y;
		double maxY = y;
		double minV = 1D - px / 16D;
		double maxV = 1D;
		double minU = 0D;
		double maxU = width / 16D;
		if (horizontal) {
			px = maxFill != 0 ? MathHelper.ceil(stack.fill * width / (float)maxFill) : 0;
			maxX += px;
			maxY += height;
			minV = 0D;
			maxV = height / 16D;
			minU = 1D;
			maxU = 1D - px / 16D;
		} else {
			maxX += width;
			minY += height - px;
			maxY += height;
		}
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(minX, maxY, 0).tex(minU, maxV).endVertex();
		bufferbuilder.pos(maxX, maxY, 0).tex(maxU, maxV).endVertex();
		bufferbuilder.pos(maxX, minY, 0).tex(maxU, minV).endVertex();
		bufferbuilder.pos(minX, minY, 0).tex(minU, minV).endVertex();
		tessellator.draw();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (!wasBlendEnabled) GlStateManager.disableBlend();
	}





	/**
	 * Renders ForgeFluid type fluid tank info, like fluid type and millibucket amount.
	 *
	 * @param gui
	 *            - the gui to render the fluid info on
	 * @param mouseX
	 *            - the cursor's x position
	 * @param mouseY
	 *            - the cursor's y position
	 * @param x
	 *            - the x left corner of where to render the info
	 * @param y
	 *            - the y top corner of where to render the info
	 * @param width
	 *            - how wide the area to render info inside is
	 * @param height
	 *            - how tall the area to render info inside is
	 * @param fluidTank
	 *            - the tank to render info of
	 */
	public static void renderTankInfo(LCEGuiInfoContainer gui, int mouseX, int mouseY, int x, int y, int width, int height, FluidTank fluidTank){
		renderTankInfo(gui, mouseX, mouseY, x, y, width, height, fluidTank, null);
	}

	/// render ForgeFluid type fluid info
	public static void renderTankInfo(LCEGuiInfoContainer gui, int mouseX, int mouseY, int x, int y, int width, int height, FluidTank fluidTank, Fluid fluid){
		/*if(fluidTank.getFluid() != null) {
			renderFluidInfo(gui, mouseX, mouseY, x, y, width, height, fluidTank.getFluid().getFluid(), fluidTank.getFluidAmount(), fluidTank.getCapacity());
		} else {
			renderFluidInfo(gui, mouseX, mouseY, x, y, width, height, fluid, 0, fluidTank.getCapacity());
		}*/
		if(fluidTank.getFluid() != null)
			renderFluidInfo(gui,mouseX,mouseY,x,y,width,height,fluidTank.getFluid(),fluidTank.getCapacity());
		else
			renderFluidInfo(gui,mouseX,mouseY,x,y,width,height,fluid != null ? new net.minecraftforge.fluids.FluidStack(fluid,0) : null,fluidTank.getCapacity());
	}
	/// add ForgeFluid type fluid info
	public static void addFluidInfo(net.minecraftforge.fluids.FluidStack stack,List<String> texts){
		addFluidInfo(stack,texts,"");
	}

	/// add ForgeFluid type fluid info
	@SideOnly(Side.CLIENT)
	public static void addFluidInfo(net.minecraftforge.fluids.FluidStack stack,List<String> texts,String prefix){
		Fluid fluid = stack.getFluid();
		FluidType ntmf = AddonFluids.fromFF(stack.getFluid());
		boolean ntmfExists = !ntmf.equals(Fluids.NONE);
		int temp = fluid.getTemperature()-273;
		if (ntmfExists) {
			if (ntmf.hasTrait(FT_LFTRCoolant.class)) {
				NBTTagCompound tag = MSRTEBase.nbtProtocol(stack.tag);
				temp = (int) (temp+tag.getDouble("heat"));
			}
		}
		if(temp != 27){
			String tempColor = "";
			if(temp < -130) {
				tempColor = "§3";
			} else if(temp < 0) {
				tempColor = "§b";
			} else if(temp < 100) {
				tempColor = "§e";
			} else if(temp < 300) {
				tempColor = "§6";
			} else if(temp < 1000) {
				tempColor = "§c";
			} else if(temp < 3000) {
				tempColor = "§4";
			} else if(temp < 20000) {
				tempColor = "§5";
			} else {
				tempColor = "§d";
			}
			texts.add(prefix+String.format("%s%d°C", tempColor, temp));
		}
		boolean hasInfo = false;
		boolean shiftHeld = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		// TRAITS
		if (ntmfExists) {

			List<String> hidden = new ArrayList<>();

			for(Class<? extends FluidTrait> clazz : FluidTrait.traitList) {
				FluidTrait trait = ntmf.getTrait(clazz);
				if(trait != null) {
					trait.addInfo(texts);
					if(shiftHeld) trait.addInfoHidden(texts);
					trait.addInfoHidden(hidden);
				}
			}
			hasInfo = !hidden.isEmpty();
			if (ntmf.hasTrait(FT_LFTRCoolant.class)) {
				NBTTagCompound tag = MSRTEBase.nbtProtocol(stack.tag);
				Map<String,Double> mixture = MSRTEBase.readMixture(tag);
				if (!mixture.isEmpty()) {
					texts.add(prefix+TextFormatting.LIGHT_PURPLE+I18nUtil.resolveKey("tile.msr.mixture"));
					for (Entry<String,Double> entry : mixture.entrySet()) {
						texts.add(prefix+" "+TextFormatting.LIGHT_PURPLE+I18nUtil.resolveKey("tile.msr.fuel."+entry.getKey())+" "+String.format("%01.3f",entry.getValue())+"/B ");
						try {
							MSRFuel fuel = MSRFuel.valueOf(entry.getKey());
							if (!fuel.funcString.equals("0"))
								texts.add(prefix+TextFormatting.LIGHT_PURPLE+"  Heat Function: "+fuel.funcString);
							if (fuel.decayRate > 0)
								texts.add(prefix+TextFormatting.DARK_PURPLE+"  Decay Function: ΔT×"+fuel.decayRate);
						} catch (IllegalArgumentException ignored) {}
					}
				}
			}
		}

		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player.getHeldItemMainhand().getItem() == AddonItems.wand_v || player.getHeldItemOffhand().getItem() == AddonItems.wand_v) {
			if (stack.tag != null) {
				for (String s : stack.tag.getKeySet()) {
					NBTBase tag = stack.tag.getTag(s);
					texts.add("TAG >> "+s+": "+tag.toString());
				}
			}
		}
		if(hasInfo && !shiftHeld) {
			texts.add(I18nUtil.resolveKey("desc.tooltip.hold", "LSHIFT"));
		}
	}
	/// render ForgeFluid type fluid info
	@SideOnly(Side.CLIENT)
	private static void renderFluidInfo(LCEGuiInfoContainer gui,int mouseX,int mouseY,int x,int y,int width,int height,net.minecraftforge.fluids.FluidStack stack,int capacity) {
		if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
			List<String> texts = new ArrayList<>();
			if (stack != null) {
				String name = stack.getLocalizedName();
				/*
				if (stack.tag != null) {
					if (stack.tag.hasKey("enrichment")) {
						name = I18nUtil.resolveKey("fluid._enrichment."+stack.tag.getByte("enrichment"),name);
					}
				}*/
				texts.add(name);
				texts.add(stack.amount + "/" + capacity + "mB");
				addFluidInfo(stack, texts);
				if (!lastClicked && gui.clickDown) {
					ItemStack item = Minecraft.getMinecraft().player.inventory.getItemStack();
					if (item != null && !item.isEmpty()) {
						FluidType ntmf = AddonFluids.fromFF(stack.getFluid());
						if (item.getItem() instanceof FuzzyIdentifierItem && !ntmf.equals(Fluids.NONE)) {
							FuzzyIdentifierPacket packet = new FuzzyIdentifierPacket();
							packet.fluidRsc = ntmf.getName();
							LeafiaCustomPacket.__start(packet).__sendToServer();
							Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("item.fuzzy_identifier.message",stack.getLocalizedName()).setStyle(new Style().setColor(TextFormatting.YELLOW)));
						}
					}
				}
			} else {
				texts.add(I18nUtil.resolveKey("desc.none"));
				texts.add("0/" + capacity + "mB");
			}
			gui.drawFluidInfo(texts, mouseX, mouseY);
			lastClicked = gui.clickDown;
		}
	}
	/// render ForgeFluid type fluid info
	@SideOnly(Side.CLIENT)
	private static void renderFluidInfo(LCEGuiInfoContainer gui, int mouseX, int mouseY, int x, int y, int width, int height, Fluid fluid, int amount, int capacity) {
		renderFluidInfo(gui,mouseX,mouseY,x,y,width,height,fluid != null ? new net.minecraftforge.fluids.FluidStack(fluid, amount) : null,capacity);
	}



	/// get ForgeFluid type fluid texture
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getTextureFromFluid(Fluid f){
		if(f == null) {
			return null;
		}
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(f.getStill().toString());
	}
	// Drillgon200: Wow that took a while to fix. Now the code is ugly and I'll
	// probably never fix it because it works. Dang it.
	/**
	 * Tessellates a ForgeFluid type liquid texture across a rectangle without looking weird and
	 * stretched.
	 * <p>TO CORRECTLY RENDER, PUT 28 ON Y OFFSET.
	 *
	 * @param tank
	 *            - the tank with the fluid to render
	 * @param guiLeft
	 *            - the left side of the gui
	 * @param guiTop
	 *            - the top of the gui
	 * @param zLevel
	 *            - the z level of the gui
	 * @param sizeX
	 *            - how big the rectangle should be
	 * @param sizeY
	 *            - how tall the rectangle should be
	 * @param offsetX
	 *            - where the starting x of the rectangle should be on screen
	 * @param offsetY
	 *            - where the starting y of the rectangle should be on screen
	 */
	@SideOnly(Side.CLIENT)
	public static void drawLiquid(FluidTank tank,int guiLeft,int guiTop,float zLevel,int sizeX,int sizeY,int offsetX,int offsetY){
		drawLiquid(tank, guiLeft, guiTop, zLevel, sizeX, sizeY, offsetX, offsetY, false);
	}

	/// draw ForgeFluid type fluids
	@SideOnly(Side.CLIENT)
	public static void drawLogLiquid(FluidTank tank, int guiLeft, int guiTop, float zLevel, int sizeX, int sizeY, int offsetX, int offsetY){
		drawLiquid(tank, guiLeft, guiTop, zLevel, sizeX, sizeY, offsetX, offsetY, true);
	}

	/// draw ForgeFluid type fluids
	@SideOnly(Side.CLIENT)
	public static void drawLiquid(FluidTank tank, int guiLeft, int guiTop, float zLevel, int sizeX, int sizeY, int offsetX, int offsetY, boolean log){
		// This is retarded, but it would be too much of a pain to fix it
		offsetY -= 44;
		NTMRenderHelper.bindBlockTexture();

		if(tank.getFluid() != null) {
			TextureAtlasSprite liquidIcon = getTextureFromFluid(tank.getFluid().getFluid());

			if(liquidIcon != null) {
				int level = 0;
				if(log){
					if(tank.getFluidAmount() > 0){
						level = (int)(sizeY * (Math.log(tank.getFluidAmount()) / Math.log(tank.getCapacity())));
					}
				} else{
					level = (int)(((double)tank.getFluidAmount() / (double)tank.getCapacity()) * sizeY);
				}

				drawFull(tank.getFluid().getFluid(), guiLeft, guiTop, zLevel, liquidIcon, level, sizeX, offsetX, offsetY, sizeY);
			}
		}
	}

	/// draw ForgeFluid type fluids
	@SideOnly(Side.CLIENT)
	public static void drawLiquid(net.minecraftforge.fluids.FluidStack fluid,int guiLeft,int guiTop,float zLevel,int sizeX,int sizeY,int offsetX,int offsetY){
		if(fluid == null || fluid.getFluid() == null)
			return;
		drawLiquid(fluid.getFluid(), guiLeft, guiTop, zLevel, sizeX, sizeY, offsetX, offsetY);
	}

	/// draw ForgeFluid type fluids
	@SideOnly(Side.CLIENT)
	public static void drawLiquid(Fluid fluid, int guiLeft, int guiTop, float zLevel, int sizeX, int sizeY, int offsetX, int offsetY){
		NTMRenderHelper.bindBlockTexture();
		if(fluid != null) {
			TextureAtlasSprite liquidIcon = getTextureFromFluid(fluid);
			if(liquidIcon != null) {
				drawFull(fluid, guiLeft, guiTop, zLevel, liquidIcon, sizeY, sizeX, offsetX, offsetY, sizeY);
			}
		}
	}

	/**
	 * Internal method to actually render the fluid
	 *
	 * @param guiLeft
	 * @param guiTop
	 * @param zLevel
	 * @param liquidIcon
	 * @param level
	 * @param sizeX
	 * @param offsetX
	 * @param offsetY
	 */
	@SideOnly(Side.CLIENT)
	private static void drawFull(Fluid f, int guiLeft, int guiTop, float zLevel, TextureAtlasSprite liquidIcon, int level, int sizeX, int offsetX, int offsetY, int sizeY){
		int color = f.getColor();
		NTMRenderHelper.setColor(color);
		NTMRenderHelper.startDrawingTexturedQuads();
		for(int i = 0; i < level; i += 16) {
			for(int j = 0; j < sizeX; j += 16) {
				int drawX = Math.min(16, sizeX - j);
				int drawY = Math.min(16, level - i);
				NTMRenderHelper.drawScaledTexture(liquidIcon, guiLeft + offsetX + j, guiTop + offsetY - i + (16 - drawY), drawX, drawY, zLevel);
			}
		}
		NTMRenderHelper.draw();
	}
}
