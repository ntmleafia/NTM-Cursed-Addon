package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.generic.WasteGrassTall;
import com.hbm.items.IDynamicModels;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WasteGrassTall.class)
public class MixinWasteGrassTall extends BlockBush implements IDynamicModels {
	@Inject(method = "<init>",at = @At(value = "TAIL"),require = 1,remap = false)
	public void onInit(Material materialIn,String s,CallbackInfo ci) {
		if (s.equals("waste_grass_tall"))
			INSTANCES.add(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void bakeModel(ModelBakeEvent evt) {
		try {
			for (int z = 0; z <= 6; z++) {
				IModel mdl = ModelLoaderRegistry.getModel(
						new ResourceLocation("minecraft","block/cross")
				);
				ImmutableMap.Builder<String,String> map = ImmutableMap.builder();
				map.put("cross","leafia:blocks/contamination/grass_tall/waste_grass_tall_"+z);
				mdl = mdl.retexture(map.build());
				IBakedModel baked = mdl.bake(
						ModelRotation.X0_Y0,
						DefaultVertexFormats.BLOCK,
						ModelLoader.defaultTextureGetter()
				);
				evt.getModelRegistry().putObject(
						new ModelResourceLocation(getRegistryName(),"meta="+z),
						baked
				);
				if (z == 0) {
					evt.getModelRegistry().putObject(
							new ModelResourceLocation(getRegistryName(),"normal"),
							baked
					);
				}
			}
		} catch (Exception e) {
			throw new LeafiaDevFlaw("Error while baking model: "+e);
		}
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel() {
		for (int z = 0; z <= 6; z++)
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this),z, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void registerSprite(TextureMap textureMap) {
		for (int z = 0; z <= 6; z++) {
			textureMap.registerSprite(new ResourceLocation(
					"leafia","blocks/contamination/grass_tall/waste_grass_tall_"+z)
			);
		}
	}
}
