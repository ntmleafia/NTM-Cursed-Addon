package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.generic.BlockSellafieldSlaked;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.*;

import static com.hbm.blocks.generic.BlockSellafieldSlaked.SHADE;

@Mixin(value = BlockSellafieldSlaked.class)
public class MixinBlockSellafieldSlaked {
	/**
	 * @author ntmleafia
	 * @reason make them darker
	 */
	@Overwrite(remap = false)
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColorHandler() {
		return (state,wa,w,a) -> {
			int meta = state.getValue(SHADE);
			return Color.HSBtoRGB(0F, 0F, 1F - (float)Math.pow(Math.min(meta,9) / 11F,0.75));
		};
	}
	/**
	 * @author ntmleafia
	 * @reason make them darker
	 */
	@Overwrite(remap = false)
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColorHandler() {
		return (stack, wawa) -> {
			int meta = stack.getMetadata() & 15;
			return Color.HSBtoRGB(0F, 0F, 1F - (float)Math.pow(Math.min(meta,9) / 11F,0.75));
		};
	}
}
