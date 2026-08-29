package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.bomb.Landmine;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.interfaces.IBomb;
import com.hbm.tileentity.bomb.TileEntityLandmine;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityLandmine;
import com.leafia.unsorted.explosion_vnt.EntityProcessorLandmine;
import com.leafia.unsorted.explosion_vnt.EntityProcessorPlayerLandmine;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Landmine.class)
public abstract class MixinLandmine extends BlockContainer implements IBomb {
	@Unique private static boolean leafia$wasPlayerPlaced = false;
	protected MixinLandmine(Material materialIn) {
		super(materialIn);
	}
	@Override
	public void onBlockPlacedBy(World worldIn,BlockPos pos,IBlockState state,EntityLivingBase placer,ItemStack stack) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityLandmine mine)
			((IMixinTileEntityLandmine)mine).leafia$setPlayerPlaced();
	}
	@Redirect(method = "explode",at = @At(value = "NEW", target = "(DF)Lcom/hbm/explosion/vanillant/standard/EntityProcessorCrossSmooth;"),remap = false,require = 1)
	public EntityProcessorCrossSmooth leafia$onExplode(double nodeDist,float fixedDamage) {
		if (leafia$wasPlayerPlaced)
			return new EntityProcessorPlayerLandmine(nodeDist,fixedDamage);
		return new EntityProcessorLandmine(nodeDist,fixedDamage);
	}
	@Inject(method = "explode",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;destroyBlock(Lnet/minecraft/util/math/BlockPos;Z)Z",remap = true),require = 1,remap = false)
	public void leafia$onExplodePre(World world,BlockPos pos,Entity detonator,CallbackInfoReturnable<BombReturnCode> cir) {
		TileEntity te = world.getTileEntity(pos);
		leafia$wasPlayerPlaced = false;
		if (te instanceof TileEntityLandmine mine) {
			if (((IMixinTileEntityLandmine)mine).leafia$getPlayerPlaced())
				leafia$wasPlayerPlaced = true;
		}
	}
}
