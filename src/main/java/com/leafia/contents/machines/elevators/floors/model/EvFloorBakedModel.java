package com.leafia.contents.machines.elevators.floors.model;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.model.BakedModelTransforms;
import com.leafia.contents.machines.elevators.floors.EvFloor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EvFloorBakedModel implements IBakedModel {

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state,@Nullable EnumFacing side,long rand) {
		List<BakedQuad> quads = new ArrayList<>();
		IBlockState disguiseState = ModBlocks.block_steel.getDefaultState();
		if (state instanceof IExtendedBlockState) {
			IBlockState painty = ((IExtendedBlockState)state).getValue(EvFloor.PAINT);
			if (painty != null)
				disguiseState = painty;
		}
		IBakedModel disguiseModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(disguiseState);
		quads.addAll(disguiseModel.getQuads(disguiseState, side, rand));
		return quads;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return BakedModelTransforms.standardBlock();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}
}
