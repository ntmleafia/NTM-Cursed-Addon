package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.item.IDesignatorItem;
import com.hbm.blocks.bomb.LaunchPad;
import com.hbm.items.tool.ItemDesignatorRange;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.Library;
import com.hbm.util.I18nUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemDesignatorRange.class)
public abstract class MixinItemDesignatorRange extends Item implements IDesignatorItem {
	@Override
	public EnumActionResult onItemUse(EntityPlayer player,World world,BlockPos pos,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (!(world.getBlockState(pos).getBlock() instanceof LaunchPad)) {
			if (stack.getTagCompound() != null) {
				stack.getTagCompound().setInteger("xCoord", pos.getX());
				stack.getTagCompound().setInteger("zCoord", pos.getZ());
				stack.getTagCompound().setInteger("yCoord", pos.getY());
			} else {
				stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("xCoord", pos.getX());
				stack.getTagCompound().setInteger("zCoord", pos.getZ());
				stack.getTagCompound().setInteger("yCoord", pos.getY());
			}

			if (world.isRemote) {
				player.sendMessage(new TextComponentTranslation(TextFormatting.GREEN + "[" + I18nUtil.resolveKey("chat.posset", new Object[0]) + "]", new Object[0]));
			}

			world.playSound(player.posX, player.posY, player.posZ, HBMSoundHandler.techBleep, SoundCategory.PLAYERS, 1.0F, 1.0F, true);
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.PASS;
		}
	}

	@Inject(method = "onItemRightClick",at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z",shift = Shift.BEFORE))
	public void onOnItemRightClick(World world,EntityPlayer player,EnumHand hand,CallbackInfoReturnable<ActionResult<ItemStack>> cir,@Local(name = "pos") BlockPos pos,@Local(name = "stack") ItemStack stack) {
		stack.getTagCompound().setInteger("yCoord", pos.getY());
	}

	/**
	 * @author leafinia
	 * @reason ajfasdkljflksdajglsadjlgjksdajg
	 */
	@Overwrite(remap = false)
	public Vec3d getCoords(World world,ItemStack stack,int i,int i1,int i2) {
		return new Vec3d((double)stack.getTagCompound().getInteger("xCoord"), (double)stack.getTagCompound().getInteger("yCoord"), (double)stack.getTagCompound().getInteger("zCoord"));
	}
}
