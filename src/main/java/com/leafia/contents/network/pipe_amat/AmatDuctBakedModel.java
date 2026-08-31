package com.leafia.contents.network.pipe_amat;

import com.hbm.blocks.network.FluidDuctStandard;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.model.AbstractWavefrontBakedModel;
import com.hbm.render.model.BakedModelTransforms;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class AmatDuctBakedModel extends AbstractWavefrontBakedModel {
	private final TextureAtlasSprite baseSprite;
	private final TextureAtlasSprite overlaySprite;
	private final boolean forBlock;
	private final float itemYaw;

	private final Map<Integer,List<BakedQuad>> cacheByMask = new HashMap<>();
	private List<BakedQuad> itemQuads;

	private AmatDuctBakedModel(HFRWavefrontObject model,TextureAtlasSprite baseSprite,TextureAtlasSprite overlaySprite,boolean forBlock,float baseScale,float tx,float ty,float tz,float itemYaw) {
		super(model, forBlock ? DefaultVertexFormats.BLOCK : DefaultVertexFormats.ITEM, baseScale, tx, ty, tz, BakedModelTransforms.isbrh());
		this.baseSprite = baseSprite;
		this.overlaySprite = overlaySprite;
		this.forBlock = forBlock;
		this.itemYaw = itemYaw;
	}

	public static AmatDuctBakedModel forBlock(HFRWavefrontObject model,TextureAtlasSprite baseSprite,TextureAtlasSprite overlaySprite) {
		return new AmatDuctBakedModel(model, baseSprite, overlaySprite, true, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	public static AmatDuctBakedModel forItem(HFRWavefrontObject model, TextureAtlasSprite baseSprite, TextureAtlasSprite overlaySprite, float baseScale, float tx, float ty, float tz, float yaw) {
		return new AmatDuctBakedModel(model, baseSprite, overlaySprite, false, 1.25F, 0.5F, 0.5F, 0.5F, (float) Math.PI);
	}

	public static AmatDuctBakedModel empty(TextureAtlasSprite sprite) {
		return new AmatDuctBakedModel(new HFRWavefrontObject(new ResourceLocation("minecraft:empty")), sprite, sprite, true, 1.0F, 0, 0, 0, 0);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state,EnumFacing side,long rand) {
		if (side != null) return Collections.emptyList();

		if (forBlock) {
			boolean pX = false, nX = false, pY = false, nY = false, pZ = false, nZ = false;
			if (state != null) {
				try {
					if (state.getPropertyKeys().contains(FluidDuctStandard.POS_X)) pX = state.getValue(FluidDuctStandard.POS_X);
					if (state.getPropertyKeys().contains(FluidDuctStandard.NEG_X)) nX = state.getValue(FluidDuctStandard.NEG_X);
					if (state.getPropertyKeys().contains(FluidDuctStandard.POS_Y)) pY = state.getValue(FluidDuctStandard.POS_Y);
					if (state.getPropertyKeys().contains(FluidDuctStandard.NEG_Y)) nY = state.getValue(FluidDuctStandard.NEG_Y);
					if (state.getPropertyKeys().contains(FluidDuctStandard.POS_Z)) pZ = state.getValue(FluidDuctStandard.POS_Z);
					if (state.getPropertyKeys().contains(FluidDuctStandard.NEG_Z)) nZ = state.getValue(FluidDuctStandard.NEG_Z);
				} catch (Exception ignored) {}
			}

			int mask = (pX ? 32 : 0)
					| (nX ? 16 : 0)
					| (pY ? 8 : 0)
					| (nY ? 4 : 0)
					| (pZ ? 2 : 0)
					| (nZ ? 1 : 0);

			List<BakedQuad> quads = cacheByMask.get(mask);
			if (quads != null) return quads;

			quads = buildWorldQuads(pX, nX, pY, nY, pZ, nZ);
			cacheByMask.put(mask, quads);
			return quads;
		} else {
			if (itemQuads == null) {
				itemQuads = buildItemQuads();
			}
			return itemQuads;
		}
	}

	private List<BakedQuad> buildWorldQuads(boolean pX, boolean nX, boolean pY, boolean nY, boolean pZ, boolean nZ) {
		Set<String> parts = new ObjectOpenHashSet<>();

		int mask = (pX ? 32 : 0)
				| (nX ? 16 : 0)
				| (pY ? 8 : 0)
				| (nY ? 4 : 0)
				| (pZ ? 2 : 0)
				| (nZ ? 1 : 0);

		if (mask == 0) {
			parts.add("Core");
		} else if (mask == 0b100000 || mask == 0b010000 || mask == 0b110000) {
			parts.add("pX");
			parts.add("nX");
		} else if (mask == 0b001000 || mask == 0b000100 || mask == 0b001100) {
			parts.add("pY");
			parts.add("nY");
		} else if (mask == 0b000010 || mask == 0b000001 || mask == 0b000011) {
			parts.add("pZ");
			parts.add("nZ");
		} else {
			if (pX) parts.add("pX");
			if (nX) parts.add("nX");
			if (pY) parts.add("pY");
			if (nY) parts.add("nY");
			if (pZ) parts.add("nZ"); // mirrors original (pZ -> nZ)
			if (nZ) parts.add("pZ"); // mirrors original (nZ -> pZ)
			parts.add("Core");
		}

		return bakeWithOverlay(parts, 0.0F, 0.0F, 0.0F, true);
	}

	private List<BakedQuad> buildItemQuads() {
		Set<String> parts = Set.of("pX", "nX", "pZ", "nZ", "Core");
		return bakeWithOverlay(parts, 0.0F, 0.0F, itemYaw, false);
	}

	private List<BakedQuad> bakeWithOverlay(Set<String> parts, float roll, float pitch, float yaw, boolean centerToBlock) {
		List<FaceGeometry> geometry = buildGeometry(parts, roll, pitch, yaw, true, centerToBlock);
		List<BakedQuad> quads = new ArrayList<>(geometry.size() * 2);
		for (FaceGeometry geo : geometry) {
			quads.add(geo.buildQuad(baseSprite, -1));
			quads.add(geo.buildQuad(overlaySprite, 1));
		}
		return quads;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return baseSprite;
	}
}
