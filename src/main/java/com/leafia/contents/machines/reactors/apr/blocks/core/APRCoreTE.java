package com.leafia.contents.machines.reactors.apr.blocks.core;

import com.hbm.tileentity.IGUIProvider;
import com.leafia.contents.AddonBlocks.APR;
import com.leafia.contents.machines.reactors.apr.blocks.APRComponentBlock;
import com.leafia.contents.machines.reactors.apr.blocks.APRComponentBlock.APRComponentType;
import com.leafia.contents.machines.reactors.apr.blocks.core.container.APRMBUI;
import com.leafia.dev.machine.LCETileEntityMachineBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class APRCoreTE extends LCETileEntityMachineBase implements IGUIProvider {
	public final Map<BlockPos,IBlockState> mbRequirement = new HashMap<>();
	public final List<Integer> chambers = new ArrayList<>();
	public boolean assembled = false;
	public APRCoreTE() {
		super(6);
	}
	public void sortChambers() {
		chambers.sort(Comparator.naturalOrder());
	}
	public void rebuildMBRequirement() {
		sortChambers();
		mbRequirement.clear();
		if (!chambers.isEmpty()) {
			// DISC
			IBlockState plate = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.PLATING);
			IBlockState dark = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.PLATING_DARK);
			IBlockState parts = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.MECHANICAL);
			int radius = chambers.get(chambers.size()-1);
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockPos p = new BlockPos(pos.getX()+x,pos.getY(),pos.getZ()+z);
					if (Math.sqrt(p.distanceSq(getPos())) <= radius) {
						IBlockState cover = (x == 0 || z == 0) ? dark : plate;
						mbRequirement.put(p.down(),cover);
						mbRequirement.put(p.down(2),parts);
						mbRequirement.put(p.down(3),parts);
						mbRequirement.put(p.down(4),parts);
						mbRequirement.put(p.down(5),cover);
					}
				}
			}
			// COILS
			IBlockState coil = APR.apr_component.getDefaultState()
					.withProperty(APRComponentBlock.VARIANT,APRComponentType.COIL);
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y >= -4; y--)
					mbRequirement.put(getPos().add(x,y,0),coil);
			}
			for (int z = -1; z <= 1; z++) {
				for (int y = -1; y >= -4; y--)
					mbRequirement.put(getPos().add(0,y,z),coil);
			}
			// FUNNEL CHAMBER
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int y = -1; y >= -4; y--)
						mbRequirement.remove(getPos().add(x,y,z));
				}
			}
		}
		for (Integer r : chambers)
			APRCoreBlock.generateTorus(getPos(),r,mbRequirement);
	}
	@Override
	public String getDefaultName() {
		return "container.apr";
	}
	AxisAlignedBB bb = null;
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(bb == null) {
			bb = new AxisAlignedBB(
					pos.getX() - 3,
					pos.getY() - 2,
					pos.getZ() - 3,
					pos.getX() + 4,
					pos.getY() + 3,
					pos.getZ() + 4
			);
		}
		return bb;
	}
	@Override
	public Container provideContainer(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		if (!assembled)
			return null;
		// TODO: add actual container
		return null;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen provideGUI(int i,EntityPlayer entityPlayer,World world,int i1,int i2,int i3) {
		if (!assembled)
			return new APRMBUI(this);
		// TODO: add actual gui
		return null;
	}
}
