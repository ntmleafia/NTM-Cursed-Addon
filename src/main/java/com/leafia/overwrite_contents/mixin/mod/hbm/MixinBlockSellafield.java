package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.generic.BlockMeta;
import com.hbm.blocks.generic.BlockSellafield;
import com.hbm.items.IDynamicModels;
import com.llib.math.LeafiaColor;
import net.minecraft.block.material.Material;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockSellafield.class)
public class MixinBlockSellafield extends BlockMeta implements IDynamicModels {
	@Shadow(remap = false)
	@Final
	@Mutable
	public static int[][] colors;

	public MixinBlockSellafield(Material m,String s) {
		super(m,s);
	}

	private static LeafiaColor transformColor(LeafiaColor col,double temperature) {
		double delta = col.green-col.blue;
		return new LeafiaColor(col.green,col.blue+delta*Math.pow(temperature,0.75)*0.65,col.blue);
	}

	@Inject(method = "<clinit>",at = @At(value = "TAIL"),require = 1,remap = false)
	private static void onClinit(CallbackInfo ci) {
		int[][] value = colors;
		for (int i = 0; i < value.length; i++) {
			LeafiaColor colorA = new LeafiaColor(value[i][0]);
			LeafiaColor colorB = new LeafiaColor(value[i][1]);
			colorA = transformColor(colorA,(i+1)/(double)value.length*0.85);
			colorB = transformColor(colorB,(i+1)/(double)value.length*0.35);
			value[i][0] = colorA.toInARGB();
			value[i][1] = colorB.toInARGB();
		}
		colors = value;
	}
}
