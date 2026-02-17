package com.leafia.unsorted;

import com.leafia.contents.AddonBlocks.Elevators;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.contents.machines.elevators.floors.EvFloorGUI;
import com.leafia.contents.machines.elevators.floors.EvFloorTE;
import com.leafia.contents.machines.elevators.gui.EvCabinGUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

public class AddonGuiHandler implements IGuiHandler {
	@Override
	public @Nullable Object getServerGuiElement(int ID,EntityPlayer player,World world,int x,int y,int z) {
		return null;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public @Nullable Object getClientGuiElement(int ID,EntityPlayer player,World world,int x,int y,int z) {
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
		return switch(ID) {
			case Elevators.guiIdFloor -> {
				if (entity instanceof EvFloorTE) {
					yield new EvFloorGUI((EvFloorTE) entity);
				}
				yield null;
			}
			case Elevators.guiIdCabin -> new EvCabinGUI(ElevatorEntity.lastEntityEw);
			default -> null;
		};
	}
}
