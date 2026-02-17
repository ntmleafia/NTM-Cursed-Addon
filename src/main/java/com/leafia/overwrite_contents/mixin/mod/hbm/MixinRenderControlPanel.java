package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.render.tileentity.RenderControlPanel;
import com.hbm.tileentity.machine.TileEntityControlPanel;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityControlPanel;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = RenderControlPanel.class)
public class MixinRenderControlPanel {
	public ResourceLocation formatRsc(String s) {
		return new ResourceLocation(s.replaceFirst("(\\w+:)?(.*)","$1textures/$2.png"));
	}
	public ResourceLocation getRsc(String s,TileEntityControlPanel panel) {
		IMixinTileEntityControlPanel mixin = (IMixinTileEntityControlPanel)panel;
		if (mixin.getSkin() != null) {
			Block block = mixin.getSkin();
			IBlockState display = block.getDefaultState();
			IBakedModel baked = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(display);
			try {
				List<BakedQuad> quads = baked.getQuads(display,EnumFacing.NORTH,0);
				if (quads.size() > 0)
					return formatRsc(quads.get(0).getSprite().getIconName());
				else
					return formatRsc(baked.getParticleTexture().getIconName());
			} catch (IllegalArgumentException ignored) {} // FUCK YOUU
			return new ResourceLocation(s);
		} else
			return new ResourceLocation(s);
	}
	@Redirect(method = "renderCustomPanel",at = @At(value = "NEW", target = "(Ljava/lang/String;)Lnet/minecraft/util/ResourceLocation;"),remap = false,require = 1)
	public ResourceLocation onRenderCustomPanel(String s,@Local(type = TileEntityControlPanel.class) TileEntityControlPanel te) {
		return getRsc(s,te);
	}
	@Redirect(method = "renderFrontPanel",at = @At(value = "NEW", target = "(Ljava/lang/String;)Lnet/minecraft/util/ResourceLocation;"),remap = false,require = 1)
	public ResourceLocation onRenderFrontPanel(String s,@Local(type = TileEntityControlPanel.class) TileEntityControlPanel te) {
		return getRsc(s,te);
	}
}
