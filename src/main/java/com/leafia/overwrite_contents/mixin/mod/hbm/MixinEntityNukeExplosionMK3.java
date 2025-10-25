package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.config.CompatibilityConfig;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityNukeExplosionMK3.class)
public abstract class MixinEntityNukeExplosionMK3 extends Entity implements IChunkLoader {
	@Shadow(remap = false) public int extType;
	@Shadow(remap = false) public boolean did;
	@Shadow(remap = false) public boolean waste;

	@Shadow(remap = false) public abstract void setDead();

	public MixinEntityNukeExplosionMK3(World worldIn) {
		super(worldIn);
	}

	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void onOnUpdate(CallbackInfo ci) {
		if (!this.world.isRemote) {
			if (CompatibilityConfig.isWarDim(this.world)) {
				if (!this.did) {
					if (!waste) {
						if (extType == 0) {
							System.out.println("TEST");
							for (EntityPlayer player : world.playerEntities) {
								if (player.getHeldItemMainhand().getItem() == ModItems.wand_d)
									player.sendMessage(new TextComponentString("TEST").setStyle(new Style().setColor(TextFormatting.YELLOW)));
							}
							this.setDead();
						}
					}
				}
			}
		}
	}
}
