package com.leafia.dev;

import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LeafiaClientUtil {
	public static void renderTankInfo(@NotNull FluidTankNTM tank,@NotNull LCEGuiInfoContainer gui,int mouseX,int mouseY,int x,int y,int width,int height) {
		if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
			List<String> list = new ArrayList();
			list.add(tank.getTankType().getLocalizedName());
			list.add(tank.getFill() + "/" + tank.getMaxFill() + "mB");
			if (tank.getPressure() != 0) {
				list.add(ChatFormatting.RED + "Pressure: " + tank.getPressure() + " PU");
			}

			tank.getTankType().addInfo(list);
			gui.drawFluidInfo((String[])list.toArray(new String[0]), mouseX, mouseY);
		}
	}
}
