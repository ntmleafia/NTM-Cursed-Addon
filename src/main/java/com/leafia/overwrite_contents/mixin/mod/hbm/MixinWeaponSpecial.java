package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.generic.BlockControlPanel;
import com.hbm.items.ModItems;
import com.hbm.items.gear.WeaponSpecial;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater;
import com.leafia.contents.worldgen.biomes.artificial.DigammaCrater.ForceSchizoPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(WeaponSpecial.class)
public class MixinWeaponSpecial {
	@Inject(method = "onItemUse",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;destroyBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"),require = 1)
	public void leafia$onOnItemUse(EntityPlayer player,World world,BlockPos pos,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ,CallbackInfoReturnable<EnumActionResult> cir) {
		if (world.getBlockState(pos).getBlock() instanceof BlockControlPanel)
			LeafiaCustomPacket.__start(new ForceSchizoPacket()).__sendToClient(player);
	}
	@SideOnly(Side.CLIENT)
	@Inject(method = "addInformation",at = @At("HEAD"),require = 1)
	public void leafia$onAddInformation(ItemStack stack,World worldIn,List<String> list,ITooltipFlag flagIn,CallbackInfo ci) {
		if (stack.getItem() == ModItems.shimmer_sledge)
			list.add("Nate dont break that control panel!");
	}
}
