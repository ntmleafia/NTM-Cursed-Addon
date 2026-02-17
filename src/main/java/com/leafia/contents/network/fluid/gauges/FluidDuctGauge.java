package com.leafia.contents.network.fluid.gauges;

import com.hbm.blocks.ILookOverlay;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.util.I18nUtil;
import com.leafia.contents.network.fluid.FluidDuctEquipmentBase;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;

import java.util.ArrayList;
import java.util.List;

public class FluidDuctGauge extends FluidDuctEquipmentBase {
	public FluidDuctGauge(Material materialIn,String s) {
		super(materialIn,s);
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return new FluidDuctGaugeTE();
	}

	@Override
	public void printHook(Pre event,World world,int x,int y,int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		if (!(te instanceof TileEntityPipeBaseNT duct))
			return;

		List<String> text = new ArrayList<>();
		text.add("&[" + duct.getType().getColor() + "&]" + duct.getType().getLocalizedName());
		FluidDuctGaugeTE gauge = (FluidDuctGaugeTE)te;
		text.add(gauge.local_fillPerSec+"mB/s");
		ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getTranslationKey() + ".name"), 0xffff00, 0x404000, text);
	}
}
