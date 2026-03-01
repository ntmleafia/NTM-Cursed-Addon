package com.leafia.contents.parts.depleteds;

import com.google.common.collect.ImmutableMap;
import com.hbm.items.IDynamicModels;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemDepletedFuel;
import com.hbm.main.MainRegistry;
import com.leafia.contents.AddonItems;
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

public class AddonDepletedFuelItem extends ItemDepletedFuel implements IDynamicModels {
	String texturePath;
	String domain;

	public AddonDepletedFuelItem(String s) {
		super(s);
		this.texturePath = "depleted_fuel/"+s;
		this.domain = "leafia";
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
		INSTANCES.add(this);
	}

	public AddonDepletedFuelItem(String s,String texturePath) {
		super(s);
		this.texturePath = texturePath;
		this.domain = "leafia";
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
		setCreativeTab(MainRegistry.partsTab);
		INSTANCES.add(this);
	}

	public AddonDepletedFuelItem(String s,String domain,String texturePath) {
		super(s);
		this.texturePath = texturePath;
		this.domain = domain;
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
		setCreativeTab(MainRegistry.partsTab);
		INSTANCES.add(this);
	}

	public void bakeModel(ModelBakeEvent event) {
		try {
			IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation("minecraft", "item/generated"));
			ResourceLocation spriteLoc = new ResourceLocation(domain, "items/" + this.texturePath);
			IModel retexturedModel = baseModel.retexture(ImmutableMap.of("layer0", spriteLoc.toString()));
			IBakedModel bakedModel = retexturedModel.bake(ModelRotation.X0_Y0, DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
			ModelResourceLocation bakedModelLocation = new ModelResourceLocation(spriteLoc, "inventory");
			event.getModelRegistry().putObject(bakedModelLocation, bakedModel);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(new ResourceLocation(domain, "items/" + this.texturePath), "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(new ResourceLocation(domain, "items/" + this.texturePath), "inventory"));
	}

	public void registerSprite(TextureMap map) {
		map.registerSprite(new ResourceLocation(domain, "items/" + this.texturePath));
	}
}
