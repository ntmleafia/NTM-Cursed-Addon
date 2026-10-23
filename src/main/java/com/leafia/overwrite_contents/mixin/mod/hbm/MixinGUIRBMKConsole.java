// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.handler.threading.PacketThreading;
import com.hbm.inventory.gui.GUIRBMKConsole;
import com.hbm.packet.toserver.NBTControlPacket;
import com.hbm.tileentity.machine.rbmk.RBMKColumn;
import com.hbm.tileentity.machine.rbmk.RBMKColumn.ColumnType;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKConsole;
import com.leafia.Tags;
import com.leafia.overwrite_contents.interfaces.IMixinRBMKColumn$ControlColumn;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scala.tools.asm.Opcodes;

@Mixin(value = GUIRBMKConsole.class)
public class MixinGUIRBMKConsole extends GuiScreen {
	@Shadow(remap = false) @Final private TileEntityRBMKConsole console;
	@Shadow(remap = false) protected int guiLeft;
	@Shadow(remap = false) protected int guiTop;
	@Shadow(remap = false) @Final private static ResourceLocation texture;
	@Unique private static final ResourceLocation leafia$additions = new ResourceLocation(Tags.MODID,"textures/gui/reactors/rbmk_console_additions.png");
	@Inject(method = "mouseClicked",at = @At(value = "FIELD", target = "Lcom/hbm/inventory/gui/GUIRBMKConsole;lastPress:J",opcode = Opcodes.PUTFIELD,remap = false),require = 1)
	void leafia$onMouseClicked(int mouseX,int mouseY,int button,CallbackInfo ci) {
		NBTTagCompound control = new NBTTagCompound();
		control.setBoolean("fuckingstopalldamnautocontrolrods",true);
		PacketThreading.createSendToServerThreadedPacket(new NBTControlPacket(control,console.getPos()));
	}
	@Inject(method = "drawGuiContainerBackgroundLayer",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onDrawGuiContainerBackgroundLayer(float interp,int mX,int mY,CallbackInfo ci) {
		final int bX = 86;
		final int bY = 11;
		final int size = 10;
		LeafiaGls.color(1,1,1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(leafia$additions);
		for (int i = 0; i < console.columns.length; i++) {
			RBMKColumn col = console.columns[i];
			if (col == null) continue;

			int x = bX + size * (i % 15);
			int y = bY + size * (i / 15);

			if (col.type == ColumnType.CONTROL_AUTO) {
				IMixinRBMKColumn$ControlColumn mixin = (IMixinRBMKColumn$ControlColumn)col;
				if (mixin.leafia$getExclamation())
					drawTexturedModalRect(guiLeft + x, guiTop + y, 0, 0, size, size);
			}
		}
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	}
}
