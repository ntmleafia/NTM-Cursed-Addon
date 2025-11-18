package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.gui.GuiInfoContainer;
import com.leafia.contents.gear.utility.ItemFuzzyIdentifier;
import com.leafia.contents.gear.utility.ItemFuzzyIdentifier.FuzzyIdentifierPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FluidTankNTM.class)
public class MixinFluidTankNTM {
	@Shadow(remap = false) private @NotNull FluidType type;
	@Unique boolean lastClicked = false;
	@Inject(method = "renderTankInfo",at = @At(value = "INVOKE", target = "Lcom/hbm/inventory/fluid/FluidType;addInfo(Ljava/util/List;)V",remap = false),remap = false)
	void onRenderTankInfo(GuiInfoContainer gui,int mouseX,int mouseY,int x,int y,int width,int height,CallbackInfo ci) {
		if (Mouse.isButtonDown(0) && !lastClicked) {
			ItemStack item = Minecraft.getMinecraft().player.inventory.getItemStack();
			if (item != null && !item.isEmpty()) {
				if (item.getItem() instanceof ItemFuzzyIdentifier) {
					FuzzyIdentifierPacket packet = new FuzzyIdentifierPacket();
					packet.fluidRsc = type.getName();
					LeafiaCustomPacket.__start(packet).__sendToServer();
					Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("item.fuzzy_identifier.message",type.getLocalizedName()).setStyle(new Style().setColor(TextFormatting.YELLOW)));
				}
			}
		}
		lastClicked = Mouse.isButtonDown(0);
	}
}
