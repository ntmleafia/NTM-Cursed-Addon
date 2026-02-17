package com.leafia.contents.network.ff_duct;

import com.google.common.collect.ImmutableMap;
import com.hbm.blocks.ILookOverlay;
import com.hbm.handler.radiation.RadiationSystemNT;
import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.items.IDynamicModels;
import com.hbm.render.block.BlockBakeFrame;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.render.block.BlockBakeFrame.BlockForm.ALL;

public class FFDuctRadShielded extends FFDuctBase implements IRadResistantBlock, ILookOverlay, IDynamicModels {
	protected BlockBakeFrame blockFrame;
	public FFDuctRadShielded(Material materialIn,String s) {
		super(materialIn);
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.blockFrame = new BlockBakeFrame(ALL, "fluid_duct_solid_sealed");
		AddonBlocks.ALL_BLOCKS.add(this);
		IDynamicModels.INSTANCES.add(this);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onBlockAdded(World worldIn,BlockPos pos,IBlockState state) {
		RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.onBlockAdded(worldIn, pos, state);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void printHook(RenderGameOverlayEvent.Pre event,World world,int x,int y,int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		if (!(te instanceof FFDuctTE duct))
			return;

		List<String> text = new ArrayList<>();
		String[] desc = I18nUtil.resolveKey("tile.ff_duct.desc").split("\\$");
		for (String s : desc)
			text.add(TextFormatting.GRAY+s);
		text.add("&[" + duct.getType().getColor() + "&]" + duct.getType().getLocalizedName());
		ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0x00ff00, 0x004000, text);
	}

	@Override
	public void bakeModel(ModelBakeEvent event) {

		try {
			IModel baseModel = ModelLoaderRegistry.getModel(new ResourceLocation(blockFrame.getBaseModel()));
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

	@Override
	public void addInformation(ItemStack stack,World player,List<String> tooltip,ITooltipFlag advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		tooltip.add("§2[" + I18nUtil.resolveKey("trait.radshield") + "]");
		float hardness = this.getExplosionResistance(null);
		if(hardness > 50){
			tooltip.add("§6" + I18nUtil.resolveKey("trait.blastres", hardness));
		}
	}
}
