package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.api.item.IDesignatorItem;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.CoreComponent;
import com.hbm.items.tool.ItemDesignator;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.machine.TileEntityCoreReceiver;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.powercores.dfc.DFCBaseTE;
import com.leafia.contents.machines.powercores.dfc.IDFCBase;
import com.leafia.contents.machines.powercores.dfc.components.creativeemitter.CEmitterTE;
import com.leafia.dev.LeafiaDebug;
import com.leafia.dev.MachineTooltip;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

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
			LeafiaDebug.debugLog(world,"Interacted!");
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

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
		MachineTooltip.addMultiblock(tooltip);
		MachineTooltip.addModular(tooltip);
		if (this == ModBlocks.dfc_receiver || this == AddonBlocks.dfc_reinforced)
			MachineTooltip.addGenerator(tooltip);
		else if (this == AddonBlocks.dfc_exchanger)
			MachineTooltip.addBoiler(tooltip);
		else if (this == ModBlocks.dfc_stabilizer)
			tooltip.addAll(Arrays.asList(I18nUtil.resolveKey("tile.dfc_stabilizer.desc").split("\\$")));
		super.addInformation(stack,worldIn,tooltip,flagIn);
	}

	@Inject(method = "createNewTileEntity",at = @At(value = "HEAD"),remap = false,cancellable = true)
	public void onCreateNewTileEntity(World worldIn,int meta,CallbackInfoReturnable<TileEntity> cir) {
		if (this == AddonBlocks.dfc_reinforced) {
			cir.setReturnValue(new TileEntityCoreReceiver());
			cir.cancel();
		} else if (this == AddonBlocks.dfc_exchanger) {
			//cir.setReturnValue(new TileEntityCoreReceiver());
			//cir.cancel();
		} else if (this == AddonBlocks.dfc_cemitter) {
			cir.setReturnValue(new CEmitterTE());
			cir.cancel();
		}
	}
}
