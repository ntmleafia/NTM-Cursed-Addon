package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.items.tool.ItemDiscord;
import com.hbm.lib.Library;
import com.leafia.dev.LeafiaUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemDiscord.class)
public class MixinItemDiscord {
	@Redirect(method = "onItemRightClick",at = @At(value = "INVOKE", target = "Lcom/hbm/lib/Library;rayTrace(Lnet/minecraft/entity/player/EntityPlayer;DF)Lnet/minecraft/util/math/RayTraceResult;",remap = false),require = 1)
	public RayTraceResult onOnItemRightClick(EntityPlayer player,double length,float interpolation) {
		World world = player.world;
		if (world.getWorldInfo().getGameType() == GameType.CREATIVE)
			return LeafiaUtil.leafiaRayTraceBlocks(world,player.getPositionVector(),player.getPositionVector().add(player.getLook(1).scale(500)),false,false,true);
		return Library.rayTrace(player,length,interpolation);
	}
}
