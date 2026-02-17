package com.leafia.contents.control.battery;

import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.util.EnumUtil;
//import com.leafia.contents.control.battery.AddonBatteryPackItem.AddonEnumBatteryPack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@Deprecated // suck my butt
public class AddonBatteryPackItemRender extends ItemRenderBase {
/*
    @Override
    public void renderInventory() {
        GlStateManager.translate(0, -3, 0);
        GlStateManager.scale(5, 5, 5);
    }

    @Override
    public void renderCommon(ItemStack item) {
        AddonEnumBatteryPack pack = EnumUtil.grabEnumSafely(AddonEnumBatteryPack.class, item.getItemDamage());
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(pack.texture);
        ResourceManager.battery_socket.renderPart(pack.isCapacitor() ? "Capacitor" : "Battery");
        GlStateManager.shadeModel(GL11.GL_FLAT);
    }*/
}
