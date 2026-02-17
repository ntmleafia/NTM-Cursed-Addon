package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.machine.MachineSiren;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntitySiren;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MachineSiren.class)
public abstract class MixinMachineSiren extends BlockContainer {
	protected MixinMachineSiren(Material materialIn) {
		super(materialIn);
	}

	@Inject(method = "onBlockActivated",at = @At(value = "HEAD"),require = 1,cancellable = true)
	public void onOnBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ,CallbackInfoReturnable<Boolean> cir) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IMixinTileEntitySiren siren) {
			if (siren.speakerMode()) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}
}
