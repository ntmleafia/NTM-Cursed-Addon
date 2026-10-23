// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.generic;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.generic.BlockConcreteColoredExt.EnumConcreteType;
import com.leafia.dev.blocks.AddonBlockBakeFrame;
import com.leafia.dev.blocks.blockbase.AddonRadResistantBlockBaked;
import com.leafia.dev.blocks.blockbase.meta.IMetaPlacable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

/// now that i know BlockMeta exists, i feel really retarded
public class ConcreteBricks extends AddonRadResistantBlockBaked implements IMetaPlacable {
	public static final PropertyEnum<ConcreteBrickType> VARIANT = PropertyEnum.create("variant",ConcreteBrickType.class);
	@Override
	public String getTranslationKey(int meta) {
		return getTranslationKey()+"."+(ConcreteBrickType.values()[meta%ConcreteBrickType.values().length]).getName();
	}
	public enum ConcreteBrickType implements IStringSerializable {
		NORMAL,MOSSY,CRACKED,BROKEN;
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	public static final String dir = "building/concrete/";
	protected AddonBlockBakeFrame blockFrameMossy;
	protected AddonBlockBakeFrame blockFrameCracked;
	protected AddonBlockBakeFrame blockFrameBroken;
	public ConcreteBricks(Material m,String s,String tex) {
		super(m,s,dir+tex);
		this.blockFrameMossy = AddonBlockBakeFrame.cubeAll(dir+tex+"_mossy");
		this.blockFrameCracked = AddonBlockBakeFrame.cubeAll(dir+tex+"_cracked");
		this.blockFrameBroken = AddonBlockBakeFrame.cubeAll(dir+tex+"_broken");
	}
	@Override
	public void bakeModel(ModelBakeEvent event) {
		try {
			for (ConcreteBrickType value : ConcreteBrickType.values()) {
				AddonBlockBakeFrame frame = null;
				switch(value) {
					case NORMAL -> frame = blockFrame;
					case MOSSY -> frame = blockFrameMossy;
					case CRACKED -> frame = blockFrameCracked;
					case BROKEN -> frame = blockFrameBroken;
				}
				IModel baseModel = ModelLoaderRegistry.getModel(frame.getBaseModelLocation());
				ImmutableMap.Builder<String,String> textureMap = ImmutableMap.builder();

				frame.putTextures(textureMap);
				IModel retexturedModel = baseModel.retexture(textureMap.build());
				IBakedModel bakedModel = retexturedModel.bake(
						ModelRotation.X0_Y0,DefaultVertexFormats.BLOCK,ModelLoader.defaultTextureGetter()
				);
				ModelResourceLocation worldLocation = new ModelResourceLocation(getRegistryName(),"variant="+value.getName());
				event.getModelRegistry().putObject(worldLocation,bakedModel);
				if (value.ordinal() == 0) { // fucking idiot
					ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(),"inventory");
					event.getModelRegistry().putObject(modelLocation,bakedModel);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void registerModel() {
		for (ConcreteBrickType value : ConcreteBrickType.values())
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this),value.ordinal(),new ModelResourceLocation(this.getRegistryName(),"variant="+value.getName()));
	}
	@Override
	public void registerSprite(TextureMap map) {
		blockFrame.registerBlockTextures(map);
		blockFrameMossy.registerBlockTextures(map);
		blockFrameCracked.registerBlockTextures(map);
		blockFrameBroken.registerBlockTextures(map);
	}
	@Override
	public boolean isRadResistant(int meta) {
		return meta <= ConcreteBrickType.MOSSY.ordinal();
	}
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,VARIANT);
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT,ConcreteBrickType.values()[meta%ConcreteBrickType.values().length]);
	}
	@Override
	public void getSubBlocks(CreativeTabs itemIn,NonNullList<ItemStack> items) {
		for (EnumConcreteType value : EnumConcreteType.values()) {
			if (value.ordinal() >= 4) break; // wtf
			items.add(new ItemStack(this,1,value.ordinal()));
		}
	}
}
