package com.leafia.contents.bomb.missile;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.render.item.ItemRenderMissileGeneric;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.AddonItems;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

import static com.leafia.init.ResourceInit.getVAO;
import static com.hbm.render.item.ItemRenderMissileGeneric.generateStandard;

public class AddonMissileItemRender {
	public static HashMap<ComparableStack,Consumer<TextureManager>> renderers = ItemRenderMissileGeneric.renderers;
	public static final ResourceLocation neonc_tex = new ResourceLocation("leafia","textures/models/leafia/missileneonc.png");
	public static final WaveFrontObjectVAO neonc_mdl = getVAO(new ResourceLocation("leafia","models/leafia/missileneon_mini_upscaled.obj"));
	public static void init() {
		renderers.put(new ComparableStack(AddonItems.missile_customnuke),generateStandard(neonc_tex,neonc_mdl));
	}
}
