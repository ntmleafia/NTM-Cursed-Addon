package com.leafia.dev;

import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.leafia.contents.gear.utility.ItemFuzzyIdentifier;
import com.leafia.contents.gear.utility.ItemFuzzyIdentifier.FuzzyIdentifierPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LeafiaClientUtil {
	static boolean lastClicked = false;
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
					if (item.getItem() instanceof ItemFuzzyIdentifier) {
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
}
