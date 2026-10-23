// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.doors.special.reactor_door;

import com.hbm.tileentity.DoorDecl;
import com.hbm.util.I18nUtil;
import com.leafia.contents.building.doors.AddonDoorGeneric;
import com.leafia.dev.math.FiaMatrix;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockProperties;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class ReactorDoorBlock extends AddonDoorGeneric {
	public ReactorDoorBlock(Material materialIn,DoorDecl type,boolean isRadResistant,String s) {
		super(materialIn,type,isRadResistant,s);
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn,int meta) {
		return meta >= 12 ? new ReactorDoorTE() : null;
	}
	@Override
	public boolean onScrew(World world,EntityPlayer player,int x,int y,int z,EnumFacing side,float fX,float fY,float fZ,EnumHand hand,ToolType tool) {
		if (tool == ToolType.SCREWDRIVER && player.isSneaking()) {
			BlockPos pos1 = this.findCore(world, new BlockPos(x, y, z));
			if (pos1 == null) {
				return false;
			} else {
				ReactorDoorTE door = (ReactorDoorTE)world.getTileEntity(pos1);
				if (door != null && !world.isRemote) {
					ItemStack lol = player.getHeldItemOffhand();
					if (lol.getItem() instanceof ItemBlock block) {
						boolean nevermind = false;
						if (block.getBlock() instanceof IBlockProperties properties) {
							if (!properties.getRenderType().equals(EnumBlockRenderType.MODEL))
								nevermind = true;
						}
						if (!nevermind) {
							FiaMatrix mat = new FiaMatrix(new Vec3d(pos1),new Vec3d(pos1.offset(EnumFacing.byIndex(world.getBlockState(pos1).getValue(META)-10)))).toObjectSpace(new FiaMatrix(new Vec3d(x,y,z)));
							if (mat.getX() < 1) {
								door.skinA = block.getBlock();
								door.metaA = block.getMetadata(lol);
							} else {
								door.skinB = block.getBlock();
								door.metaB = block.getMetadata(lol);
							}
							door.generateSyncPacket().__sendToAffectedClients();
							return true;
						}
					}
					if (door.getDoorType().hasSkins()) {
						if (world.isRemote)
							return true;
						else {
							door.cycleSkinIndex();
							return true;
						}
					}
				}
				return false;
			}
		} else {
			return false;
		}
	}
	@Override
	public void addInformation(ItemStack stack,World player,List<String> tooltip,ITooltipFlag advanced) {
		super.addInformation(stack,player,tooltip,advanced);
		tooltip.addAll(Arrays.asList(I18nUtil.resolveKey("tile.reactor_door.paint").split("\\$")));
	}
}
