package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.item.IDesignatorItem;
import com.hbm.blocks.machine.CoreComponent;
import com.hbm.items.tool.ItemDesignator;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import com.leafia.contents.machines.powercores.dfc.DFCBaseTE;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CoreComponent.class)
public abstract class MixinCoreComponent extends BlockContainer {
	protected MixinCoreComponent(Material materialIn) {
		super(materialIn);
	}
	@SuppressWarnings(value = "compileJava")
	@Inject(method = {"onBlockActivated", "func_180639_a"},at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/internal/FMLNetworkHandler;openGui(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;ILnet/minecraft/world/World;III)V",shift = Shift.BEFORE,remap = false),cancellable = true)
	public void onOnBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ,CallbackInfoReturnable<Boolean> cir) {
		if (!player.getHeldItem(hand).isEmpty()) {
			TileEntity te = world.getTileEntity(pos);
			NBTTagCompound nbt = player.getHeldItem(hand).getTagCompound();
			if (player.getHeldItem(hand).getItem() instanceof IDesignatorItem && te instanceof IDFCBase && nbt != null) {
				BlockPos target =
						new BlockPos(nbt.getInteger("xCoord"), nbt.getInteger("yCoord"), nbt.getInteger("zCoord"));
				if (target.equals(pos)) {
					world.playSound(null, pos, HBMSoundHandler.buttonNo, SoundCategory.BLOCKS, 1, 1);
					cir.setReturnValue(true);
					cir.cancel();
					return;
				}
				((IDFCBase)te).setTargetPosition(target);
				world.playSound(null, pos, HBMSoundHandler.buttonYes, SoundCategory.BLOCKS, 1, 1);
				cir.setReturnValue(true);
				cir.cancel();
			}
		}
	}

	/**
	 * @author ntmleafia
	 * @reason fuck obfuscation
	 */
	/*@Overwrite
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		} else if (!player.isSneaking()) {
			if (!player.getHeldItem(hand).isEmpty()) {
			TileEntity te = world.getTileEntity(pos);
			NBTTagCompound nbt = player.getHeldItem(hand).getTagCompound();
			if (player.getHeldItem(hand).getItem() instanceof IDesignatorItem && te instanceof IDFCBase && nbt != null) {
				BlockPos target =
						new BlockPos(nbt.getInteger("xCoord"), nbt.getInteger("yCoord"), nbt.getInteger("zCoord"));
				if (target.equals(pos)) {
					world.playSound(null, pos, HBMSoundHandler.buttonNo, SoundCategory.BLOCKS, 1, 1);
					return true;
				}
				((IDFCBase)te).setTargetPosition(target);
				world.playSound(null, pos, HBMSoundHandler.buttonYes, SoundCategory.BLOCKS, 1, 1);
				return true;
			}
		}
			FMLNetworkHandler.openGui(player, MainRegistry.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		} else {
			return false;
		}
	}*/
}
