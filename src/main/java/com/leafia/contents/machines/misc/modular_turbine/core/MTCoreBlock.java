// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.core;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.items.tool.ItemTooling;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.InventoryHelper;
import com.leafia.contents.AddonBlocks.ModularTurbines;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE.AssemblyReturnCode;
import com.leafia.contents.machines.misc.modular_turbine.core.MTCoreTE.AssemblyReturnCode.ReturnCodeError;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacketEncoder;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.passive.rendering.TopRender.Highlight;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MTCoreBlock extends ModularTurbineBlockBase {
	public MTCoreBlock(String s) {
		super(s);
		ModularTurbines.ALL_COMPONENTS_FOR_RENDER.remove(this);
	}
	@Override
	public int shaftHeight() {
		return 0;
	}
	@Override
	public TurbineComponentType componentType() {
		return null;
	}
	@Override
	public int[] canConnectTo() {
		return new int[]{}; // doesn't matter for the core
	}
	@Override
	public int size() {
		return 0; // doesn't matter for the core
	}
	@Override
	public double weight() {
		return 0;
	}
	@Override
	public int[] getDimensions() {
		return new int[]{ 0,0,0,0,0,0 };
	}
	@Override
	public int getOffset() {
		return 0;
	}
	@Override
	public @Nullable TileEntity createNewTileEntity(World worldIn,int meta) {
		return new MTCoreTE();
	}
	public static class TurbineErrorHighlight implements LeafiaCustomPacketEncoder {
		BlockPos posA;
		BlockPos posB;
		public TurbineErrorHighlight() { }
		public TurbineErrorHighlight(BlockPos posA,BlockPos posB) {
			this.posA = posA;
			this.posB = posB;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			buf.writeVec3i(posA);
			buf.writeVec3i(posB);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			posA = buf.readPos();
			posB = buf.readPos();
			return (ctx)->{
				Highlight highlightA = new Highlight(posA);
				Highlight highlightB = new Highlight(posB);
				highlightA.setLabel("ERROR");
				highlightB.setLabel("ERROR");
				highlightA.setLifetime(8);
				highlightB.setLifetime(8);
				highlightA.setColor(0xFF0000);
				highlightB.setColor(0xFF0000);
				highlightA.ray = new Vec3d(posB).subtract(new Vec3d(posA));
				highlightA.show();
				highlightB.show();
			};
		}
	}
	static Item getOverdriveUpgradeFromInt(int tier) {
		return switch(tier) {
			case 1 -> ModItems.upgrade_overdrive_1;
			case 2 -> ModItems.upgrade_overdrive_2;
			case 3 -> ModItems.upgrade_overdrive_3;
			default -> null;
		};
	}
	@Override
	public boolean onBlockActivated(World worldIn,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof MTCoreTE core) {
			ItemStack stack = playerIn.getHeldItem(hand);
			if (stack.getItem() instanceof ItemMachineUpgrade upgrade) {
				if (upgrade.type == UpgradeType.OVERDRIVE) {
					if (!worldIn.isRemote) {
						if (core.overdrive == 0) {
							core.overdrive = upgrade.tier;
							stack.shrink(1);
							playerIn.inventoryContainer.detectAndSendChanges();
							worldIn.playSound(null,pos,HBMSoundHandler.upgradePlug,SoundCategory.BLOCKS,1,1);
						}
					}
					return true;
				}
			} else if (stack.getItem() instanceof ItemTooling tool) {
				if (tool.getType() == ToolType.SCREWDRIVER) {
					if (!worldIn.isRemote) {
						Item item = getOverdriveUpgradeFromInt(core.overdrive);
						if (item != null) {
							core.overdrive = 0;
							InventoryHelper.spawnItemStack(worldIn,playerIn.posX,playerIn.posY,playerIn.posZ,new ItemStack(item));
							worldIn.playSound(null,pos,HBMSoundHandler.lockHang,SoundCategory.BLOCKS,0.85f,1);
						}
					}
					return true;
				}
			}
			if (core.components.isEmpty()) {
				if (!worldIn.isRemote) {
					AssemblyReturnCode code = core.reassemble();
					if (code instanceof ReturnCodeError error) {
						playerIn.sendMessage(new TextComponentTranslation("chat.turbine.error."+error.reason.name().toLowerCase()).setStyle(new Style().setColor(TextFormatting.RED)));
						LeafiaCustomPacket.__start(new TurbineErrorHighlight(error.errorPosA,error.errorPosB)).__sendToClient(playerIn);
					}
				}
				return true;
			}
		}
		return false;
	}
	@Override
	public void breakBlock(@NotNull World world,@NotNull BlockPos pos,IBlockState state) {
		if (world.getTileEntity(pos) instanceof MTCoreTE core) {
			Item item = getOverdriveUpgradeFromInt(core.overdrive);
			if (item != null) {
				core.overdrive = 0;
				InventoryHelper.spawnItemStack(world,pos.getX(),pos.getY(),pos.getZ(),new ItemStack(item));
			}
		}
		super.breakBlock(world,pos,state);
	}
}
