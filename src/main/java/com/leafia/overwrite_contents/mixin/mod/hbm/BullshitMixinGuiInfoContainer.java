package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.gui.GuiInfoContainer;
import com.hbm.lib.Library;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.leafia.settings.AddonConfig;
import com.leafia.unsorted.BullshitInDavenportIowa;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GuiInfoContainer.class)
public class BullshitMixinGuiInfoContainer {
	/**
	 * @author ntmleafia
	 * @reason bullshit unit system
	 */
	@Overwrite(remap = false)
	public void drawElectricityInfo(GuiInfoContainer gui,int mouseX,int mouseY,int x,int y,int width,int height,long power,long maxPower) {
		if(x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
			if (!AddonConfig.bullshitUnits)
				gui.drawFluidInfo(new String[] { Library.getShortNumber(power) + "/" + Library.getShortNumber(maxPower) + "HE" }, mouseX, mouseY);
			else
				gui.drawFluidInfo(new String[] { Library.getShortNumber((long) BullshitInDavenportIowa.HEToCn(power)) + "/" + Library.getShortNumber((long)BullshitInDavenportIowa.HEToCn(maxPower)) + "corn" }, mouseX, mouseY);
		}
	}
}
