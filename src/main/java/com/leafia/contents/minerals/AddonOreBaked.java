package com.leafia.contents.minerals;

import com.google.common.collect.ImmutableMap;
import com.hbm.items.IDynamicModels;
import com.leafia.dev.blocks.AddonBlockBakeFrame;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class AddonOreBaked extends AddonOreBase implements IDynamicModels {
	protected AddonBlockBakeFrame blockFrame;
	public AddonOreBaked(String s,int harvestLvl,AddonBlockBakeFrame blockFrame) {
		super(s,harvestLvl);
		this.blockFrame = blockFrame;
		IDynamicModels.INSTANCES.add(this);

	}
	public AddonOreBaked(String s,int harvestLvl,String texture) {
		super(s,harvestLvl);
		this.blockFrame = AddonBlockBakeFrame.cubeAll(texture);
		IDynamicModels.INSTANCES.add(this);
	}
	@Override
	public void bakeModel(ModelBakeEvent event) {

		try {
			IModel baseModel = ModelLoaderRegistry.getModel(blockFrame.getBaseModelLocation());
			ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();

			blockFrame.putTextures(textureMap);
			IModel retexturedModel = baseModel.retexture(textureMap.build());
			IBakedModel bakedModel = retexturedModel.bake(
					ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()
			);

			ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "inventory");
			event.getModelRegistry().putObject(modelLocation, bakedModel);
			ModelResourceLocation worldLocation = new ModelResourceLocation(getRegistryName(), "normal");
			event.getModelRegistry().putObject(worldLocation, bakedModel);

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this),0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	@Override
	public void registerSprite(TextureMap map) {
		blockFrame.registerBlockTextures(map);
	}
}
