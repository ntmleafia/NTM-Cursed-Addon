package com.leafia.contents.network.computers.audiocable;

import com.hbm.handler.radiation.RadiationSystemNT;
import com.hbm.interfaces.IRadResistantBlock;
import com.hbm.util.I18nUtil;
import com.leafia.dev.blocks.blockbase.AddonBlockBaked;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.computronics.tile.TileAudioCable;

import java.util.List;

/**
 * Important: Do NOT <b>EVER</b> refer to this class in ANY way unless it's guaranteed that Computronics is installed.
 */
public class AudioCableBlock extends AddonBlockBaked implements ITileEntityProvider, IRadResistantBlock {
	final boolean radSealed;
	public AudioCableBlock(Material m,String s,boolean radSealed,String tex) {
		super(m,s,tex);
		this.radSealed = radSealed;
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return new TileAudioCable();
	}
	@Override
	public boolean isRadResistant(World worldIn,BlockPos blockPos) {
		return radSealed;
	}
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (radSealed)
			RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.onBlockAdded(worldIn, pos, state);
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (radSealed)
			RadiationSystemNT.markSectionForRebuild(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}
	@Override
	public void addInformation(ItemStack stack,World player,List<String> tooltip,ITooltipFlag advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if (radSealed)
			tooltip.add("§2[" + I18nUtil.resolveKey("trait.radshield") + "]");
		float hardness = this.getExplosionResistance(null);
		if(hardness > 50){
			tooltip.add("§6" + I18nUtil.resolveKey("trait.blastres", hardness));
		}
	}
}
