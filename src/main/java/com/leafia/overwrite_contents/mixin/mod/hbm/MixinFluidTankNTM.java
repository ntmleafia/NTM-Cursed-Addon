package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.tank.IFluidLoadingHandler;
import com.hbm.inventory.gui.GuiInfoContainer;
import com.leafia.contents.gear.utility.FuzzyIdentifierItem;
import com.leafia.contents.gear.utility.FuzzyIdentifierItem.FuzzyIdentifierPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.overwrite_contents.interfaces.IMixin;
import com.leafia.unsorted.fluids.FluidLoaderBottle;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = FluidTankNTM.class)
public class MixinFluidTankNTM implements IMixin {
	@Shadow(remap = false) private @NotNull FluidType type;
	@Shadow(remap = false) @Final public static List<IFluidLoadingHandler> loadingHandlers;
	@Unique boolean lastClicked = false;

	@Inject(method = "<clinit>",at = @At(value = "TAIL"),require = 1,remap = false)
	private static void onClInit(CallbackInfo ci) {
		loadingHandlers.add(0,new FluidLoaderBottle());
	}
	@SideOnly(Side.CLIENT)
	@Inject(method = "renderTankInfo",at = @At(value = "INVOKE", target = "Lcom/hbm/inventory/fluid/FluidType;addInfo(Ljava/util/List;)V",remap = false),remap = false,require = 1)
	void onRenderTankInfo(GuiInfoContainer gui,int mouseX,int mouseY,int x,int y,int width,int height,CallbackInfo ci) {
		if (Mouse.isButtonDown(0) && !lastClicked) {
			ItemStack item = Minecraft.getMinecraft().player.inventory.getItemStack();
			if (item != null && !item.isEmpty()) {
				if (item.getItem() instanceof FuzzyIdentifierItem) {
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
