package com.leafia.contents.consumable;

import com.google.common.collect.ImmutableMap;
import com.hbm.items.IDynamicModels;
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

public class AddonFoodBaked extends AddonFoodBase  implements IDynamicModels {
	String texturePath;

	public AddonFoodBaked(String s,String texturePath,int hunger,boolean isWolfFood) {
		super(s,hunger,isWolfFood);
		this.texturePath = texturePath;
		INSTANCES.add(this);
	}

	public AddonFoodBaked(String s,int hunger,boolean isWolfFood) {
		super(s,hunger,isWolfFood);
		this.texturePath = s;
		INSTANCES.add(this);
	}

	public void bakeModel(ModelBakeEvent event) {
		try {
			IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft", "item/generated"));
			ResourceLocation spriteLoc = new ResourceLocation("leafia", "items/" + this.texturePath);
			IModel retexturedModel = baseModel.retexture(ImmutableMap.of("layer0", spriteLoc.toString()));
			IBakedModel bakedModel = retexturedModel.bake(ModelRotation.X0_Y0, DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
			ModelResourceLocation bakedModelLocation = new ModelResourceLocation(spriteLoc, "inventory");
			event.getModelRegistry().putObject(bakedModelLocation, bakedModel);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(new ResourceLocation("leafia", "items/" + this.texturePath), "inventory"));
	}

	public void registerSprite(TextureMap map) {
		map.registerSprite(new ResourceLocation("leafia", "items/" + this.texturePath));
	}
}