package com.leafia.contents.gear.ntmfbottle;

import com.google.common.collect.Lists;
import com.leafia.contents.building.mixed.TextureAtlasSpriteHalf.HalfDirection;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.compress.utils.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class TextureAtlasSpriteMask extends TextureAtlasSprite {
	/// thanks community edition
	public final ResourceLocation mask;
	public final ResourceLocation texture;
	private int mipmap = 0;
	//private final String basePath = "textures";

	public TextureAtlasSpriteMask(String spriteName,ResourceLocation mask,ResourceLocation texture) {
		super(spriteName);
		this.mask = mask;
		this.texture = texture;
	}

	@Override
	public boolean hasCustomLoader(IResourceManager manager,ResourceLocation location) {
		return true;
	}
	/*private ResourceLocation completeResourceLocation(ResourceLocation loc) {
		return new ResourceLocation(loc.getNamespace(), String.format("%s/%s%s", new Object[] { this.basePath, loc.getPath(), ".png" }));
	}*/

	@Override
	public boolean load(IResourceManager man, ResourceLocation resourcelocation, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {


		ResourceLocation baseSpriteResourceLocationFull = mask; //this.completeResourceLocation(this.mask);
		ResourceLocation overlaySpriteResourceLocationFull = texture; //this.completeResourceLocation(this.texture);

		IResource iresource = null;
		IResource overlayResource = null;
		try {

			//Base texture
			iresource = man.getResource(baseSpriteResourceLocationFull);
			PngSizeInfo pngSizeInfo = PngSizeInfo.makeFromResource(iresource);
			boolean hasAnimation = iresource.getMetadata("animation") != null;
			this.loadSprite(pngSizeInfo, hasAnimation);

			//Overlay
			overlayResource = man.getResource(overlaySpriteResourceLocationFull);
			PngSizeInfo overlayPngSizeInfo = PngSizeInfo.makeFromResource(overlayResource);
			boolean overlayHasAnimation = iresource.getMetadata("animation") != null;
			this.loadSprite(overlayPngSizeInfo, overlayHasAnimation);

			if (pngSizeInfo.pngWidth != overlayPngSizeInfo.pngWidth || pngSizeInfo.pngHeight != overlayPngSizeInfo.pngHeight)
				throw new RuntimeException("no");

			iresource = man.getResource(baseSpriteResourceLocationFull);
			overlayResource = man.getResource(overlaySpriteResourceLocationFull);
			this.mipmap = Minecraft.getMinecraft().getTextureMapBlocks().getMipmapLevels()+1;
			this.loadSpriteFrames(iresource, overlayResource, this.mipmap);

		} catch (RuntimeException|IOException e) {
			net.minecraftforge.fml.client.FMLClientHandler.instance().trackBrokenTexture(baseSpriteResourceLocationFull, e.getMessage());
			return true;
		} finally {
			IOUtils.closeQuietly(iresource);
		}

		return false;
	}

	public void loadSpriteFrames(IResource maskRsc, IResource overlayRsc, int mipmapLevels) throws IOException {
		BufferedImage maskImg = ImageIO.read(maskRsc.getInputStream());

		AnimationMetadataSection animationMetadataSection = maskRsc.getMetadata("animation");

		int[][] baseFrameData = new int[mipmapLevels][];
		baseFrameData[0] = new int[maskImg.getWidth() * maskImg.getHeight()];
		maskImg.getRGB(0, 0, maskImg.getWidth(), maskImg.getHeight(), baseFrameData[0], 0, maskImg.getWidth());

		if(overlayRsc != null){
			BufferedImage texImg = ImageIO.read(overlayRsc.getInputStream());
			int[][] overlayData = new int[mipmapLevels][];
			overlayData[0] = new int[texImg.getWidth() * texImg.getHeight()];
			//if (texImg.getWidth() != maskImg.getWidth() || texImg.getHeight() != maskImg.getHeight()) return;

			int height = maskImg.getHeight();
			int width = maskImg.getWidth();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int maskPixel = maskImg.getRGB(x, y);
					int texPixel = texImg.getRGB(x, y);
					//boolean isTransparent = (maskPixel&0xFF000000) == 0;
					float mR = (maskPixel>>>16&0xFF)/255f;
					float mG = (maskPixel>>>8&0xFF)/255f;
					float mB = (maskPixel&0xFF)/255f;
					float mA = (maskPixel>>>24&0xFF)/255f;
					float tR = (texPixel>>>16&0xFF)/255f;
					float tG = (texPixel>>>8&0xFF)/255f;
					float tB = (texPixel&0xFF)/255f;
					float tA = (texPixel>>>24&0xFF)/255f;
					int color = ((int)(mR*255*tR)<<16)|((int)(mG*255*tG)<<8)|((int)(mB*255*tB))|((int)(mA*255*tA)<<24);
					baseFrameData[0][y*width+x] = color;//isTransparent ? maskPixel : texPixel;
				}
			}
		}

		if (animationMetadataSection == null) {
			this.framesTextureData.add(baseFrameData);
		} else {
			int frameCount = maskImg.getHeight() / this.width;

			if (animationMetadataSection.getFrameCount() > 0) {
				for (int frameIndex : animationMetadataSection.getFrameIndexSet()) {
					if (frameIndex >= frameCount) {
						throw new RuntimeException("Invalid frame index " + frameIndex);
					}

					this.allocateFrameTextureData(frameIndex);
					this.framesTextureData.set(frameIndex, getFrameTextureData(baseFrameData, this.width, this.width, frameIndex));
				}

				this.animationMetadata = animationMetadataSection;
			} else {
				List<AnimationFrame> frames = Lists.newArrayList();

				for (int i = 0; i < frameCount; ++i) {
					this.framesTextureData.add(getFrameTextureData(baseFrameData, this.width, this.width, i));
					frames.add(new AnimationFrame(i, -1));
				}

				this.animationMetadata = new AnimationMetadataSection(frames, this.width, this.height, animationMetadataSection.getFrameTime(), animationMetadataSection.isInterpolate());
			}
		}
	}

	@Override
	public int[][] getFrameTextureData(int index) {
		try {
			return super.getFrameTextureData(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	private void allocateFrameTextureData(int index)
	{
		if (this.framesTextureData.size() <= index)
		{
			for (int i = this.framesTextureData.size(); i <= index; ++i)
			{
				this.framesTextureData.add(null);
			}
		}
	}

	private static int[][] getFrameTextureData(int[][] data, int width, int height, int frame) {
		int[][] result = new int[data.length][];
		for (int i = 0; i < data.length; ++i) {
			int[] pixels = data[i];
			if (pixels != null) {
				result[i] = new int[(width >> i) * (height >> i)];
				System.arraycopy(pixels, frame * result[i].length, result[i], 0, result[i].length);
			}
		}
		return result;
	}
}
