package com.leafia.contents.machines.reactors.apr.blocks.core;

import com.hbm.render.NTMRenderHelper;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.hbm.render.util.SmallBlockPronter;
import com.leafia.AddonBase;
import com.leafia.transformer.LeafiaGls;
import com.llib.exceptions.messages.TextWarningLeafia;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.leafia.AddonBase.getIntegrated;
import static com.leafia.init.ResourceInit.getVAO;

public class APRCoreRender extends TileEntitySpecialRenderer<APRCoreTE> {
	public static final WaveFrontObjectVAO vao = getVAO(getIntegrated("machines/reactors/apr/aprcore.obj"));
	public static final Map<String,TextureAtlasSprite> sprs = new HashMap<>();
	@Override
	public void render(APRCoreTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		bindTexture(AddonBase.solid);
		vao.renderAll();
		LeafiaGls.popMatrix();
		if (!te.assembled) {
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(x-te.getPos().getX(),y-te.getPos().getY(),z-te.getPos().getZ());
			SmallBlockPronter.startDrawing();
			NTMRenderHelper.bindBlockTexture();
			NTMRenderHelper.startDrawingTexturedQuads();
			for (Entry<BlockPos,IBlockState> entry : te.mbRequirement.entrySet()) {
				if (entry.getValue().getBlock() instanceof BlockAir) continue;
				String spr = entry.getValue().getBlock().getRegistryName().toString()+":"+entry.getValue().getBlock().getMetaFromState(entry.getValue());
				if (!sprs.containsKey(spr)) {
					Minecraft.getMinecraft().player.sendMessage(new TextWarningLeafia("Couldn't find preview sprite for "+spr+"!"));
					continue;
				}
				SmallBlockPronter.renderSimpleBlockAt(sprs.get(spr),entry.getKey().getX(),entry.getKey().getY(),entry.getKey().getZ());
			}
			NTMRenderHelper.draw();
			LeafiaGls.disableBlend();
			LeafiaGls.enableAlpha();
			LeafiaGls.enableLighting();
			LeafiaGls.popMatrix();
		}
	}
}
