package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.config.CompatibilityConfig;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.entity.logic.EntityNukeExplosionMK3.ATEntry;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.items.ModItems;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.database.FolkvangrJammers;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityNukeExplosionMK3.class)
public abstract class MixinEntityNukeExplosionMK3 extends Entity implements IChunkLoader {
	@Shadow(remap = false) public int extType;
	@Shadow(remap = false) public boolean did;
	@Shadow(remap = false) public boolean waste;

	@Shadow(remap = false) public abstract void setDead();

	@Shadow public abstract void onUpdate();

	public MixinEntityNukeExplosionMK3(World worldIn) {
		super(worldIn);
	}

	@Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
	private void onOnUpdate(CallbackInfo ci) {
		if (!this.world.isRemote) {
			if (CompatibilityConfig.isWarDim(this.world)) {
				if (!this.did) {
					if (!waste) {
						if (extType == 0) {
							super.onUpdate();
							EntityNukeFolkvangr folkvangr = new EntityNukeFolkvangr(world, this.getPositionVector(), null);
							world.spawnEntity(folkvangr);
							this.setDead();
							ci.cancel();
						}
					}
				}
			}
		}
	}

	@Inject(method = "isJammed",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setDead()V"),remap = false)
	private static void onIsJammed(World world,Entity entity,CallbackInfoReturnable<Boolean> cir,@Local(name = "jammer") ATEntry jammer) {
		FolkvangrJammers.lastDetectedJammer = new BlockPos(jammer.x, jammer.y, jammer.z);
	}
}
