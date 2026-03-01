package com.leafia.contents.machines.heat;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.tileentity.IItemRendererProvider;
import com.leafia.contents.AddonBlocks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class HeaterRTGRender extends TileEntitySpecialRenderer<HeaterRTGTE>
    implements IItemRendererProvider {
  @Override
  public boolean isGlobalRenderer(HeaterRTGTE te) {
    return true;
  }


  public static final ResourceLocation heater_radiothermal_tex = new ResourceLocation("leafia", "textures/models/radiothermal.png");

  @Override
  public void render(
          HeaterRTGTE te,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.enableCull();

    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    bindTexture(heater_radiothermal_tex);
    ResourceManager.heater_oilburner.renderAll();
    GlStateManager.shadeModel(GL11.GL_FLAT);

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(AddonBlocks.heater_rt);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.translate(0, -1, 0);
        GlStateManager.scale(1.9, 1.9, 1.9);
      }

      public void renderCommon() {
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(1.9, 1.9, 1.9);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        NTMRenderHelper.bindTexture(heater_radiothermal_tex);
        ResourceManager.heater_oilburner.renderAll();
        GlStateManager.shadeModel(GL11.GL_FLAT);
      }
    };
  }
}
