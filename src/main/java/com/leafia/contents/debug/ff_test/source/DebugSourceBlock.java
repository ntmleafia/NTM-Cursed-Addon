package com.leafia.contents.debug.ff_test.source;

import com.hbm.blocks.ILookOverlay;
import com.leafia.dev.blocks.blockbase.AddonBlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DebugSourceBlock extends AddonBlockContainer implements ILookOverlay {
	public DebugSourceBlock(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new DebugSourceTE();
	}
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	@Override
	public void printHook(Pre pre,World world,int x,int y,int z) {
		BlockPos pos = new BlockPos(x,y,z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof DebugSourceTE tank) {
			List<String> text = new ArrayList<>();
			text.add(tank.zaza.getFluidAmount()+"mB");
			ILookOverlay.printGeneric(pre,"Test Source Block",0xFF00FF,0x400040,text);
		}
	}
}
