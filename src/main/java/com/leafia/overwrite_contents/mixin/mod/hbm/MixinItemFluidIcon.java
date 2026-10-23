// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.google.common.collect.ImmutableMap;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ItemBakedBase;
import com.hbm.items.machine.ItemFluidIcon;
import com.leafia.unsorted.fluids.FluidIconOverlays;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.hbm.items.ItemEnumMulti.ROOT_PATH;

@Mixin(value = ItemFluidIcon.class)
public abstract class MixinItemFluidIcon extends ItemBakedBase {
	@Shadow(remap = false) @Final public String texturePath;

	public MixinItemFluidIcon(String s) {
		super(s);
	}

	/**
	 * @author ntmleafia
	 * @reason add overlays
	 */
	@Overwrite(remap = false)
	public void registerModel() {
		for (FluidType ft : Fluids.getInNiceOrder()) {
			String path = ROOT_PATH + texturePath;
			String overlay = FluidIconOverlays.getOverlay(ft);
			if (overlay != null)
				path = path + "_" + overlay;
			ResourceLocation loc = new ResourceLocation("hbm", path);
			ModelResourceLocation mrl = new ModelResourceLocation(loc, "inventory");
			ModelLoader.setCustomModelResourceLocation(this, ft.getID(), mrl);
		}
	}

	@Override
	public void bakeModel(ModelBakeEvent event) {
		try {
			IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft", "item/generated"));
			ResourceLocation spriteLoc = new ResourceLocation("hbm", ROOT_PATH + texturePath);
			for (int i = -1; i < FluidIconOverlays.overlays.size(); i++) {
				ImmutableMap<String,String> layers;
				if (i >= 0) {
					layers = ImmutableMap.of(
							"layer0",spriteLoc.toString(),
							"layer1","leafia:"+FluidIconOverlays.prefix+FluidIconOverlays.overlays.get(i)
					);
				} else
					layers = ImmutableMap.of("layer0",spriteLoc.toString());
				IModel retexturedModel = baseModel.retexture(layers);
				IBakedModel bakedModel = retexturedModel.bake(ModelRotation.X0_Y0,DefaultVertexFormats.ITEM,ModelLoader.defaultTextureGetter());
				String suffix = "";
				if (i >= 0)
					suffix = suffix+"_"+FluidIconOverlays.overlays.get(i);
				ModelResourceLocation bakedModelLocation = new ModelResourceLocation(spriteLoc+suffix,"inventory");
				event.getModelRegistry().putObject(bakedModelLocation,bakedModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void registerSprite(TextureMap map) {
		super.registerSprite(map);
		for (String overlay : FluidIconOverlays.overlays)
			map.registerSprite(new ResourceLocation("leafia",FluidIconOverlays.prefix+overlay));
	}
}
