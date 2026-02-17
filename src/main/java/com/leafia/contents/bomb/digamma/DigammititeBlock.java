package com.leafia.contents.bomb.digamma;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockMeta;
import com.hbm.blocks.generic.BlockSellafield;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.items.IDynamicModels;
import com.hbm.potion.HbmPotion;
import com.hbm.render.block.BlockBakeFrame;
import com.hbm.render.icon.RGBMutatorInterpolatedComponentRemap;
import com.hbm.render.icon.TextureAtlasSpriteMutatable;
import com.hbm.render.model.VariantBakedModel;
import com.hbm.util.ContaminationUtil;
import com.leafia.contents.AddonBlocks;
import com.llib.math.LeafiaColor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static com.hbm.blocks.generic.BlockSellafieldSlaked.getVariantForPos;
import static com.hbm.blocks.generic.BlockSellafieldSlaked.sellafieldTextures;
import static com.hbm.render.block.BlockBakeFrame.ROOT_PATH;

public class DigammititeBlock extends BlockMeta implements IDynamicModels {
	public static final IUnlistedProperty<Integer> VARIANT = BlockSellafield.VARIANT;
	public final static int LEVELS = 6;
	public static final float rad = 5f;
	public static final int[][] colors;
	private static LeafiaColor transformColor(LeafiaColor col,double temperature) {
		double delta = col.green-col.red;
		return new LeafiaColor(col.green,col.blue,col.blue);
	}
	static {
		int[][] sellafieldColors = new int[][]{
				{0x4C7939, 0x41463F},
				{0x418223, 0x3E443B},
				{0x338C0E, 0x3B5431},
				{0x1C9E00, 0x394733},
				{0x02B200, 0x37492F},
				{0x00D300, 0x324C26}
		};
		for (int i = 0; i < sellafieldColors.length; i++) {
			LeafiaColor colorA = new LeafiaColor(sellafieldColors[i][0]);
			LeafiaColor colorB = new LeafiaColor(sellafieldColors[i][1]);
			colorA = transformColor(colorA,(i+1)/(double)sellafieldColors.length*0.85);
			colorB = transformColor(colorB,(i+1)/(double)sellafieldColors.length*0.35);
			sellafieldColors[i][0] = colorA.toInARGB();
			sellafieldColors[i][1] = colorB.toInARGB();
		}
		colors = sellafieldColors;
	}


	public DigammititeBlock(Material mat,SoundType type,String s) {
		super(mat, type, s, (short) LEVELS);
		this.showMetaInCreative = true;
		this.needsRandomTick = true;
		ModBlocks.ALL_BLOCKS.remove(this);
		AddonBlocks.ALL_BLOCKS.add(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{META}, new IUnlistedProperty[]{VARIANT});

	}

	@Override
	public void onBlockAdded(World worldIn,BlockPos pos,IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state,IBlockAccess world,BlockPos pos) {
		IExtendedBlockState extended = (IExtendedBlockState) state;
		int variantValue = getVariantForPos(pos);
		// Variant #2 has some weird green dots on it
		if (variantValue == 2)
			variantValue--;
		return extended.withProperty(VARIANT, variantValue);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote) return;
		IBlockState currentState = world.getBlockState(pos);
		int level = currentState.getValue(META);
		float netRad = rad * (level + 1);
		ContaminationUtil.radiate(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 32, 0, netRad, 0, 0, 0);
	}


	@SideOnly(Side.CLIENT)
	public void registerSprite(TextureMap map) {
		for (int level = 0; level < LEVELS; level++) {
			int[] tint = colors[level];

			for (BlockBakeFrame texture : sellafieldTextures) {
				ResourceLocation spriteLoc = new ResourceLocation("leafia", ROOT_PATH + texture.textureArray[0] + "-" + level+"-copy");
				TextureAtlasSpriteMutatable mutatedTexture = new TextureAtlasSpriteMutatable(spriteLoc.toString(), new RGBMutatorInterpolatedComponentRemap(0x858384, 0x434343, tint[0], tint[1]));
				map.setTextureEntry(mutatedTexture);
			}
		}
	}


	@SideOnly(Side.CLIENT)
	public void bakeModel(ModelBakeEvent event) {
		for (int level = 0; level < LEVELS; level++) {
			var models = new IBakedModel[4];
			for (int variant = 0; variant < 4; variant++) {
				IModel baseModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(sellafieldTextures[0].getBaseModel()));
				ImmutableMap.Builder<String, String> textureMap = ImmutableMap.builder();
				textureMap.put("all", new ResourceLocation("leafia", ROOT_PATH) + sellafieldTextures[variant].textureArray[0] + "-" + level+"-copy");

				IModel retexturedModel = baseModel.retexture(textureMap.build());
				IBakedModel bakedModel = retexturedModel.bake(
						ModelRotation.X0_Y0, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()
				);

				models[variant] = bakedModel;
			}
			event.getModelRegistry().putObject(new ModelResourceLocation(this.getRegistryName(), "meta=" + level), new VariantBakedModel(models, models[0], VARIANT));
		}

	}
}
